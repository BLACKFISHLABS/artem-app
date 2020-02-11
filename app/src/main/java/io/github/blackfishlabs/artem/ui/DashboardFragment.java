package io.github.blackfishlabs.artem.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.data.LocalDataInjector;
import io.github.blackfishlabs.artem.data.realm.specification.OrderStatusSpecificationFilter;
import io.github.blackfishlabs.artem.data.realm.specification.OrdersByUserSpecification;
import io.github.blackfishlabs.artem.domain.OrderChartData;
import io.github.blackfishlabs.artem.domain.entity.OrderEntity;
import io.github.blackfishlabs.artem.domain.json.OrderJson;
import io.github.blackfishlabs.artem.helper.AndroidUtils;
import io.github.blackfishlabs.artem.helper.DateUtils;
import io.github.blackfishlabs.artem.helper.PrefUtils;
import io.github.blackfishlabs.artem.ui.common.BaseFragment;
import io.github.blackfishlabs.artem.ui.event.SavedOrderEvent;

import static java.util.Objects.requireNonNull;

public class DashboardFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener {

    public static final String TAG = DashboardFragment.class.getName();

    @BindView(R.id.swipe_container_all_pull_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.pie_chart_orders_by_customer)
    PieChart pieChart;

    private ViewTreeObserver.OnGlobalLayoutListener pieChartLayoutListener = null;
    private DateTime initialDateFilter;
    private DateTime finalDateFilter;

    @Override
    protected int provideContentViewResource() {
        return R.layout.fragment_dashboard;
    }

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrderedOrders();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle inState) {
        View view = super.onCreateView(inflater, container, inState);

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);

        drawPieChart();

        eventBus().register(this);

        return view;
    }

    private void drawPieChart() {
        pieChart.getDescription().setEnabled(false);

        pieChart.setCenterText(generateCenterText());
        pieChart.setCenterTextSize(10f);
        pieChart.setCenterTextTypeface(AndroidUtils.getFont(getContext()));

        // radius of the center hole in percent of maximum radius
        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(50f);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
    }

    private SpannableString generateCenterText() {
        SpannableString s = new SpannableString(getString(R.string.dashboard_chart_center_text));
        s.setSpan(new RelativeSizeSpan(2f), 0, 7, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 8, s.length(), 0);
        return s;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_all_filter) {
            showFilterDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showFilterDialog() {
        if (initialDateFilter == null || finalDateFilter == null) {
            return;
        }

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,
                DateUtils.getYear(initialDateFilter), DateUtils.getMonth(initialDateFilter), DateUtils.getDay(initialDateFilter),
                DateUtils.getYear(finalDateFilter), DateUtils.getMonth(finalDateFilter), DateUtils.getDay(finalDateFilter));

        datePickerDialog.setStartTitle(getString(R.string.all_issue_date_initial));
        datePickerDialog.setEndTitle(getString(R.string.all_issue_date_final));
        datePickerDialog.show(requireNonNull(getActivity()).getFragmentManager(), "DatePickerDialog");
    }

    @Override
    public void onDateSet(final DatePickerDialog view,
                          final int year, final int monthOfYear, final int dayOfMonth,
                          final int yearEnd, final int monthOfYearEnd, final int dayOfMonthEnd) {
        initialDateFilter = DateUtils.toDateTime(
                year, DateUtils.convertFromZeroBasedIndex(monthOfYear), dayOfMonth, LocalTime.MIDNIGHT);
        finalDateFilter = DateUtils.toDateTime(
                yearEnd, DateUtils.convertFromZeroBasedIndex(monthOfYearEnd), dayOfMonthEnd, DateUtils.BEFORE_MIDNIGHT);
        loadOrderedOrders();
    }

    @Subscribe(sticky = true)
    public void onSavedOrder(SavedOrderEvent event) {
        loadOrderedOrders();
    }

    @OnClick(R.id.button_all_retry)
    void onButtonRetryClicked() {
        requireNonNull(mLinearLayoutErrorState).setVisibility(View.GONE);
        loadOrderedOrders();
    }

    private void loadOrderedOrders() {
        if (PrefUtils.getAtelierKey(getContext()) == null) {
            return;
        }

        startLoadingOrderedOrders();

        if (initialDateFilter == null || finalDateFilter == null) {
            final DateTime now = DateTime.now();
            final int year = now.getYear();
            final int monthOfYear = now.getMonthOfYear();
            final int dayOfMonth = now.getDayOfMonth();
            initialDateFilter = DateUtils.toDateTime(year, monthOfYear, 1, LocalTime.MIDNIGHT);
            finalDateFilter = DateUtils.toDateTime(year, monthOfYear, dayOfMonth, DateUtils.BEFORE_MIDNIGHT);
        }

        long initialDate = DateUtils.dateTimeToMillis(initialDateFilter);
        long finalDate = DateUtils.dateTimeToMillis(finalDateFilter);

        OrdersByUserSpecification specification = new OrdersByUserSpecification(PrefUtils.getAtelierKey(getContext()))
                .byStatus(OrderStatusSpecificationFilter.NOT_CANCELLED)
                .byIssueDate(initialDate, finalDate)
                .orderByCustomerName();

        List<OrderEntity> query = LocalDataInjector.getOrderRealmDB(getContext()).query(specification);

        showOrderedOrders(toChartData(LocalDataInjector.getOrderMapper().toViewObjects(query)));
    }

    private void startLoadingOrderedOrders() {
        clearPieChart(false);
        swipeRefreshLayout.setRefreshing(true);
        pieChart.setVisibility(View.GONE);
        requireNonNull(mLinearLayoutEmptyState).setVisibility(View.VISIBLE);
    }

    private void clearPieChart(final boolean setAsNull) {
        if (pieChart != null) {
            pieChart.clearAnimation();
            if (!pieChart.isEmpty()) {
                pieChart.clearValues();
            }
            pieChart.clear();
            if (setAsNull) {
                pieChart = null;
            }
        }
    }

    private List<OrderChartData> toChartData(List<OrderJson> orders) {
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }
        List<OrderChartData> chartData = new ArrayList<>();
        BigDecimal orderAmount = BigDecimal.ZERO;
        String name = orders.get(0).getCustomer().getName();
        for (OrderJson order : orders) {
            if (!name.equals(order.getCustomer().getName())) {
                chartData.add(OrderChartData.create(name, orderAmount.floatValue()));
                name = order.getCustomer().getName();
                orderAmount = BigDecimal.ZERO;
            }

            orderAmount = orderAmount.add(order.getTotalOrder());

            if (orders.indexOf(order) == orders.size() - 1) {
                chartData.add(OrderChartData.create(name, orderAmount.floatValue()));
            }
        }
        return chartData;
    }

    private void showOrderedOrders(List<OrderChartData> orders) {
        if (!orders.isEmpty()) {
            pieChart.setVisibility(View.VISIBLE);
            pieChart.getViewTreeObserver()
                    .addOnGlobalLayoutListener(
                            pieChartLayoutListener = this::onPieChartFinishLoading);
            convertToPieData(orders);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void convertToPieData(List<OrderChartData> data) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (OrderChartData item : data) {
            entries.add(new PieEntry(item.getAmount(), item.getName()));
        }

        PieDataSet ds1 = new PieDataSet(entries, getString(R.string.dashboard_chart_label));
        ds1.setColors(ColorTemplate.MATERIAL_COLORS);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.WHITE);
        ds1.setValueTextSize(12f);

        PieData pieData = new PieData(ds1);
        pieData.setValueTypeface(AndroidUtils.getFont(getContext()));
        pieData.setValueFormatter(new DefaultValueFormatter(2));

        showChartData(pieData);
    }

    private void showChartData(PieData pieData) {
        pieChart.setData(pieData);
        pieChart.setDrawEntryLabels(false);
        pieChart.invalidate();
    }

    private void onPieChartFinishLoading() {
        if (getView() != null) {
            pieChart
                    .getViewTreeObserver()
                    .removeOnGlobalLayoutListener(pieChartLayoutListener);
            pieChartLayoutListener = null;
            swipeRefreshLayout.setRefreshing(false);
            mLinearLayoutEmptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
            requireNonNull(mLinearLayoutEmptyState).setVisibility(View.GONE);
        }
        clearPieChart(true);
        eventBus().unregister(this);
        super.onDestroyView();
    }

}
