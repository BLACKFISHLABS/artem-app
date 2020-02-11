package io.github.blackfishlabs.artem.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;

import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
import io.github.blackfishlabs.artem.domain.entity.OrderEntity;
import io.github.blackfishlabs.artem.domain.json.OrderJson;
import io.github.blackfishlabs.artem.helper.DateUtils;
import io.github.blackfishlabs.artem.helper.FormattingUtils;
import io.github.blackfishlabs.artem.helper.PrefUtils;
import io.github.blackfishlabs.artem.ui.adapter.OrdersReportAdapter;
import io.github.blackfishlabs.artem.ui.common.BaseFragment;
import io.github.blackfishlabs.artem.ui.event.SavedOrderEvent;

import static java.util.Objects.requireNonNull;

public class OrdersReportFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, DatePickerDialog.OnDateSetListener {

    public static final String TAG = OrdersReportFragment.class.getName();

    private OrdersReportAdapter ordersReportAdapter;
    private ViewTreeObserver.OnGlobalLayoutListener recyclerViewLayoutListener = null;
    private MaterialDialog filtersDialog;
    private DateTime initialDateFilter;
    private DateTime finalDateFilter;

    @StatusFilter
    private int statusFilter = STATUS_FILTER_ALL;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_FILTER_ALL, STATUS_FILTER_PENDING, STATUS_FILTER_CANCEL, STATUS_FILTER_INVOICED})
    private @interface StatusFilter {
    }

    private static final int STATUS_FILTER_ALL = 0;
    private static final int STATUS_FILTER_PENDING = 1;
    private static final int STATUS_FILTER_CANCEL = 2;
    private static final int STATUS_FILTER_INVOICED = 3;

    @BindView(R.id.swipe_container_all_pull_refresh)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view_orders_report)
    protected RecyclerView recyclerViewOrders;

    public static OrdersReportFragment newInstance() {
        return new OrdersReportFragment();
    }

    @Override
    protected int provideContentViewResource() {
        return R.layout.fragment_orders_report;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle inState) {
        View view = super.onCreateView(inflater, container, inState);

        recyclerViewOrders.setHasFixedSize(true);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewOrders.setLayoutManager(layoutManager);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.md_white_1000);

        return view;
    }

    @Override
    public void onViewCreated(
            final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadOrdersByDefault();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.action_all_filter) {
            showFiltersDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        loadOrdersByDefault();
    }

    @OnClick(R.id.button_all_retry)
    void onButtonRetryClicked() {
        requireNonNull(mLinearLayoutErrorState).setVisibility(View.GONE);
        loadOrdersByDefault();
    }

    @Subscribe(sticky = true)
    public void onSavedOrder(SavedOrderEvent event) {
        final OrderJson order = event.getOrder();
        eventBus().removeStickyEvent(SavedOrderEvent.class);
        if (ordersReportAdapter != null) {
            final int position = ordersReportAdapter.updateOrder(order);
            if (position != RecyclerView.NO_POSITION) {
                recyclerViewOrders.scrollToPosition(position);
            }
        } else {
            showOrders(new ArrayList<>(Collections.singletonList(order)));
        }
    }

    @Override
    public void onDateSet(final DatePickerDialog view,
                          final int year, final int monthOfYear, final int dayOfMonth,
                          final int yearEnd, final int monthOfYearEnd, final int dayOfMonthEnd) {
        setInitialIssueDateFilter(year, DateUtils.convertFromZeroBasedIndex(monthOfYear), dayOfMonth);
        setFinalIssueDateFilter(yearEnd, DateUtils.convertFromZeroBasedIndex(monthOfYearEnd), dayOfMonthEnd);
    }

    private void loadOrdersByDefault() {
        if (PrefUtils.getAtelierKey(getContext()) == null) {
            return;
        }
        loadOrders(new OrdersByUserSpecification(PrefUtils.getAtelierKey(getContext()))
                .orderByIssueDate());
    }

    private void loadOrders(OrdersByUserSpecification specification) {
        startLoadingOrders();

        List<OrderEntity> query = LocalDataInjector.getOrderRealmDB(getContext()).query(specification);
        showOrders(LocalDataInjector.getOrderMapper().toViewObjects(query));
    }

    private void startLoadingOrders() {
        swipeRefreshLayout.setRefreshing(true);
        ordersReportAdapter = null;
        recyclerViewOrders.setAdapter(null);
        recyclerViewOrders.setVisibility(View.GONE);
        requireNonNull(mLinearLayoutEmptyState).setVisibility(View.VISIBLE);
        setTitle(getString(R.string.title_report));
    }

    private void showOrders(List<OrderJson> orders) {
        if (!orders.isEmpty()) {
            setTitleWithTotalOrders(orders);
            recyclerViewOrders.setVisibility(View.VISIBLE);
            recyclerViewOrders.setAdapter(
                    ordersReportAdapter = new OrdersReportAdapter(orders));
            recyclerViewOrders.addItemDecoration(
                    new DividerItemDecoration(
                            requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
            recyclerViewOrders
                    .getViewTreeObserver()
                    .addOnGlobalLayoutListener(
                            recyclerViewLayoutListener = this::onRecyclerViewFinishLoading);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            if (!eventBus().isRegistered(this)) {
                eventBus().register(this);
            }
        }
    }

    private void setTitleWithTotalOrders(List<OrderJson> orders) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderJson order : orders) {
            total = total.add(order.getTotalOrder());
        }
        setTitle(getString(R.string.orders_report_total_title,
                FormattingUtils.formatAsCurrency(total.doubleValue())));
    }

    private void onRecyclerViewFinishLoading() {
        if (getView() != null) {
            recyclerViewOrders
                    .getViewTreeObserver()
                    .removeOnGlobalLayoutListener(recyclerViewLayoutListener);
            recyclerViewLayoutListener = null;

            swipeRefreshLayout.setRefreshing(false);
            requireNonNull(mLinearLayoutEmptyState).setVisibility(View.GONE);
            if (!eventBus().isRegistered(this)) {
                eventBus().register(this);
            }
        }
    }

    private void showFiltersDialog() {
        filtersDialog = new MaterialDialog.Builder(requireNonNull(getContext()))
                .customView(R.layout.dialog_orders_report_filter, false)
                .positiveText(R.string.all_apply_filter)
                .onPositive((dialog, which) -> applyFilters())
                .show();

        resetFiltersInitialAndFinalDate();
        setFiltersDefaultStatus();
        setUpFiltersTouchListener();
        setUpFiltersCheckedChangeListener();
    }

    private void resetFiltersInitialAndFinalDate() {
        initialDateFilter = null;
        finalDateFilter = null;
    }

    private void setFiltersDefaultStatus() {
        final View filtersDialogView = filtersDialog.getCustomView();
        if (filtersDialogView != null) {
            RadioGroup statusRadioGroup = filtersDialogView.findViewById(R.id.radio_group_status);

            switch (statusFilter) {
                case STATUS_FILTER_ALL:
                    statusRadioGroup.check(R.id.radio_button_status_all);
                    break;
                case STATUS_FILTER_INVOICED:
                    statusRadioGroup.check(R.id.radio_button_status_invoiced);
                    break;
                case STATUS_FILTER_PENDING:
                    statusRadioGroup.check(R.id.radio_button_status_pending);
                    break;
                case STATUS_FILTER_CANCEL:
                    statusRadioGroup.check(R.id.radio_button_status_sent);
                    break;
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpFiltersTouchListener() {
        final View filtersDialogView = filtersDialog.getCustomView();
        if (filtersDialogView != null) {
            filtersDialogView.findViewById(R.id.edit_text_issue_date_initial)
                    .setOnTouchListener((v, e) -> {
                        if (e.getAction() == MotionEvent.ACTION_UP) {
                            showFilterByIssueDateDialog();
                            return false;
                        }
                        return true;
                    });

            filtersDialogView.findViewById(R.id.edit_text_issue_date_final)
                    .setOnTouchListener((v, e) -> {
                        if (e.getAction() == MotionEvent.ACTION_UP) {
                            showFilterByIssueDateDialog();
                            return false;
                        }
                        return true;
                    });
        }
    }

    private void setUpFiltersCheckedChangeListener() {
        final View filtersDialogView = filtersDialog.getCustomView();
        if (filtersDialogView != null) {
            filtersDialogView.<RadioGroup>findViewById(R.id.radio_group_status)
                    .setOnCheckedChangeListener((group, checkedId) -> {
                        switch (checkedId) {
                            case R.id.radio_button_status_all: {
                                statusFilter = STATUS_FILTER_ALL;
                                break;
                            }
                            case R.id.radio_button_status_pending: {
                                statusFilter = STATUS_FILTER_PENDING;
                                break;
                            }
                            case R.id.radio_button_status_sent: {
                                statusFilter = STATUS_FILTER_CANCEL;
                                break;
                            }
                            case R.id.radio_button_status_invoiced: {
                                statusFilter = STATUS_FILTER_INVOICED;
                                break;
                            }
                        }
                    });
        }
    }

    private void applyFilters() {
        if (PrefUtils.getAtelierKey(getContext()) == null) {
            return;
        }

        OrdersByUserSpecification specification =
                new OrdersByUserSpecification(PrefUtils.getAtelierKey(getContext()));

        if (initialDateFilter != null && finalDateFilter != null) {
            long initialDate = DateUtils.dateTimeToMillis(initialDateFilter);
            long finalDate = DateUtils.dateTimeToMillis(finalDateFilter);
            specification.byIssueDate(initialDate, finalDate);
        }

        switch (statusFilter) {
            case STATUS_FILTER_ALL:
                break;
            case STATUS_FILTER_PENDING: {
                specification.byStatus(OrderStatusSpecificationFilter.CREATED_OR_MODIFIED);
                break;
            }
            case STATUS_FILTER_CANCEL: {
                specification.byStatus(OrderStatusSpecificationFilter.CANCELLED);
                break;
            }
            case STATUS_FILTER_INVOICED: {
                specification.byStatus(OrderStatusSpecificationFilter.INVOICED);
                break;
            }
        }

        loadOrders(specification);
    }

    private void showFilterByIssueDateDialog() {
        final LocalDate now = LocalDate.now();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,
                DateUtils.getYear(now), DateUtils.getMonth(now), DateUtils.getDay(now),
                DateUtils.getYear(now), DateUtils.getMonth(now), DateUtils.getDay(now));

        datePickerDialog.setStartTitle(getString(R.string.all_issue_date_initial));
        datePickerDialog.setEndTitle(getString(R.string.all_issue_date_final));
        datePickerDialog.show(requireNonNull(getActivity()).getFragmentManager(), "DatePickerDialog");
    }

    private void setInitialIssueDateFilter(final int year, final int month, final int day) {
        initialDateFilter = DateUtils.toDateTime(year, month, day, LocalTime.MIDNIGHT);
        EditText editText = requireNonNull(filtersDialog.getCustomView())
                .findViewById(R.id.edit_text_issue_date_initial);
        editText.setText(FormattingUtils.formatAsDate(initialDateFilter));
    }

    private void setFinalIssueDateFilter(final int year, final int month, final int day) {
        finalDateFilter = DateUtils.toDateTime(year, month, day, DateUtils.BEFORE_MIDNIGHT);
        EditText editText = requireNonNull(filtersDialog.getCustomView())
                .findViewById(R.id.edit_text_issue_date_final);
        editText.setText(FormattingUtils.formatAsDate(finalDateFilter));
    }

    @Override
    public void onDestroyView() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
            assert mLinearLayoutEmptyState != null;
            mLinearLayoutEmptyState.setVisibility(View.GONE);
        }
        eventBus().unregister(this);
        super.onDestroyView();
    }


}
