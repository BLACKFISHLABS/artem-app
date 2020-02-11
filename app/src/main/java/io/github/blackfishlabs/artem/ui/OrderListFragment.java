package io.github.blackfishlabs.artem.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.ISelectionListener;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter.listeners.OnLongClickListener;
import com.mikepenz.fastadapter_extensions.ActionModeHelper;
import com.mikepenz.materialize.util.UIUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.data.LocalDataInjector;
import io.github.blackfishlabs.artem.data.realm.specification.OrderStatusSpecificationFilter;
import io.github.blackfishlabs.artem.data.realm.specification.OrdersByUserSpecification;
import io.github.blackfishlabs.artem.domain.OrderStatus;
import io.github.blackfishlabs.artem.domain.entity.OrderEntity;
import io.github.blackfishlabs.artem.domain.json.OrderJson;
import io.github.blackfishlabs.artem.helper.PrefUtils;
import io.github.blackfishlabs.artem.ui.adapter.OrderAdapterItem;
import io.github.blackfishlabs.artem.ui.common.BaseFragment;
import io.github.blackfishlabs.artem.ui.event.SavedOrderEvent;
import io.github.blackfishlabs.artem.ui.event.SelectedOrderEvent;
import io.realm.RealmResults;

import static java.util.Objects.requireNonNull;

public class OrderListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ARG_SHOW_ONLY_PENDING_ORDERS = OrderListFragment.class.getName() + ".argShowOnlyPendingOrders";
    public static final String TAG = OrderListFragment.class.getName();

    @BindView(R.id.swipe_container_all_pull_refresh)
    protected SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recycler_view_orders)
    protected RecyclerView recyclerViewOrders;

    private FastItemAdapter<OrderAdapterItem> fastItemAdapter;
    private ActionModeHelper actionModeHelper;
    protected ViewTreeObserver.OnGlobalLayoutListener recyclerViewLayoutListener = null;

    private IItemAdapter.Predicate<OrderAdapterItem> filterPredicate = (item, constraint) ->
            !item.getOrder().getCustomer().getName().trim().toLowerCase()
                    .contains(requireNonNull(constraint).toString().toLowerCase());

    private OnClickListener<OrderAdapterItem> clickListener = (v, a, i, position) -> {
        if (!actionModeHelper.isActive()) {
            final OrderJson selectedOrder = fastItemAdapter.getAdapterItem(position).getOrder();

            if (selectedOrder != null) {
                eventBus().postSticky(SelectedOrderEvent.selectOrder(selectedOrder));
                // FIXME: Edit sales without lost customers and products
//                if (selectedOrder.isStatusCreatedOrModified()) {
//                    navigate().toNewOrder();
//                } else {
//                    navigate().toViewOrder();
//                }
                navigate().toViewOrder();
            }
        } else {
            actionModeHelper.getActionMode().invalidate();
        }

        return false;
    };

    private OnLongClickListener<OrderAdapterItem> preLongClickListener = (v, d, i, position) -> {
        ActionMode actionMode = actionModeHelper.onLongClick(getHostActivity(), position);

        if (actionMode != null) {
            //we want color our CAB
            getHostActivity()
                    .findViewById(R.id.action_mode_bar)
                    .setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(requireNonNull(getContext()),
                            R.attr.colorPrimary, R.color.material_drawer_primary));
        }

        return actionMode != null;
    };

    private ISelectionListener<OrderAdapterItem> selectionListener = (item, selected) -> {
        if (actionModeHelper.isActive()) {
            actionModeHelper.getActionMode().invalidate();
        }
    };

    public static OrderListFragment newInstance(boolean showOnlyPendingOrders) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_SHOW_ONLY_PENDING_ORDERS, showOnlyPendingOrders);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int provideContentViewResource() {
        return R.layout.fragment_order_list;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle inState) {
        View view = super.onCreateView(inflater, container, inState);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.md_white_1000);

        fastItemAdapter = new FastItemAdapter<>();

        fastItemAdapter.setHasStableIds(true);
        fastItemAdapter.getItemFilter().withFilterPredicate(filterPredicate);

        fastItemAdapter
                .withSelectable(true)
                .withMultiSelect(true)
                .withSelectOnLongClick(true)
                .withSavedInstanceState(inState)
                .withOnClickListener(clickListener)
                .withOnPreLongClickListener(preLongClickListener)
                .withSelectionListener(selectionListener);

        actionModeHelper = new ActionModeHelper(fastItemAdapter, R.menu.menu_cab_order_list, new ActionBarCallback());

        recyclerViewOrders.setHasFixedSize(true);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewOrders.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return getHostActivity().onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadOrders();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_all_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.order_list_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fastItemAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                fastItemAdapter.filter(query);
                return true;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.action_all_search).setVisible(recyclerViewOrders.getAdapter() != null);
    }

    @Override
    public void onRefresh() {
        loadOrders();
    }

    @OnClick(R.id.button_all_retry)
    void onButtonRetryClicked() {
        requireNonNull(mLinearLayoutErrorState).setVisibility(View.GONE);
        loadOrders();
    }

    @Subscribe(sticky = true)
    public void onSavedOrder(SavedOrderEvent event) {
        eventBus().removeStickyEvent(SavedOrderEvent.class);
    }

    private void loadOrders() {
        if (PrefUtils.getAtelierKey(getContext()) != null) {
            startLoadingOrders();
            OrdersByUserSpecification specification = new OrdersByUserSpecification(PrefUtils.getAtelierKey(getContext())).orderByIssueDate();

            if (isShowOnlyPendingOrders()) {
                specification.byStatus(OrderStatusSpecificationFilter.CREATED_OR_MODIFIED);
            }

            RealmResults<OrderEntity> query = LocalDataInjector.getOrderRealmDB(getContext()).query(specification);
            showOrders(LocalDataInjector.getOrderMapper().toViewObjects(query));
        }
    }

    private boolean isShowOnlyPendingOrders() {
        return getArguments() != null && getArguments().getBoolean(ARG_SHOW_ONLY_PENDING_ORDERS, false);
    }

    private void startLoadingOrders() {
        if (actionModeHelper.isActive()) {
            actionModeHelper.reset();
        }
        swipeRefreshLayout.setRefreshing(true);
        fastItemAdapter.clear();
        recyclerViewOrders.setAdapter(null);
        recyclerViewOrders.setVisibility(View.GONE);
        requireNonNull(mLinearLayoutEmptyState).setVisibility(View.VISIBLE);
        requireNonNull(getActivity()).invalidateOptionsMenu();
    }

    private void showOrders(List<OrderJson> orders) {
        if (!orders.isEmpty()) {
            recyclerViewOrders.setVisibility(View.VISIBLE);

            List<OrderAdapterItem> items = new ArrayList<>();
            for (OrderJson order : orders) {
                items.add(OrderAdapterItem
                        .create(!isShowOnlyPendingOrders())
                        .withOrder(order));
            }
            fastItemAdapter.add(items);
            recyclerViewOrders.setAdapter(fastItemAdapter);

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

    private void onRecyclerViewFinishLoading() {
        if (getView() != null) {
            recyclerViewOrders
                    .getViewTreeObserver()
                    .removeOnGlobalLayoutListener(recyclerViewLayoutListener);
            recyclerViewLayoutListener = null;
            requireNonNull(getActivity()).invalidateOptionsMenu();
            swipeRefreshLayout.setRefreshing(false);
            requireNonNull(mLinearLayoutEmptyState).setVisibility(View.GONE);
            if (!eventBus().isRegistered(this)) {
                eventBus().register(this);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState = fastItemAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
            requireNonNull(mLinearLayoutEmptyState).setVisibility(View.GONE);
        }
        eventBus().unregister(this);
        super.onDestroyView();
    }

    private class ActionBarCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                requireNonNull(getActivity()).getWindow().setStatusBarColor(ContextCompat.getColor(requireNonNull(getContext()), R.color.colorPrimaryDark));
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
            // FIXME: This size don't show more then one selected
            final int selectionSize = fastItemAdapter.getSelectedItems().size();
            OrderAdapterItem[] orderAdapterItems = fastItemAdapter.getSelectedItems().toArray(new OrderAdapterItem[selectionSize]);

            final MenuItem invoiceMenuItem = menu.findItem(R.id.action_invoice);
            final MenuItem removeItemsMenuItem = menu.findItem(R.id.action_delete);
            final MenuItem cancelItemsMenuItem = menu.findItem(R.id.action_cancel);

            if (selectionSize == 1) {
                OrderJson firstOrder = orderAdapterItems[0].getOrder();
                if (removeItemsMenuItem.isVisible()) {
                    removeItemsMenuItem.setVisible(false);
                }

                if (cancelItemsMenuItem.isVisible()) {
                    cancelItemsMenuItem.setVisible(false);
                }

                if (invoiceMenuItem.isVisible()) {
                    invoiceMenuItem.setVisible(false);
                }

                if (firstOrder.isStatusCreatedOrModified()) {
                    if (!cancelItemsMenuItem.isVisible()) {
                        cancelItemsMenuItem.setVisible(true);
                    }

                    if (!invoiceMenuItem.isVisible()) {
                        invoiceMenuItem.setVisible(true);
                    }
                }

                if (firstOrder.isStatusInvoice()) {
                    if (invoiceMenuItem.isVisible()) {
                        invoiceMenuItem.setVisible(false);
                    }

                    if (!cancelItemsMenuItem.isVisible()) {
                        cancelItemsMenuItem.setVisible(true);
                    }
                }

                if (firstOrder.isStatusCanceled()) {
                    if (!removeItemsMenuItem.isVisible()) {
                        removeItemsMenuItem.setVisible(true);
                    }
                }

                return true;
            } else if (selectionSize > 1) {
                if (removeItemsMenuItem.isVisible()) {
                    removeItemsMenuItem.setVisible(false);
                }

                if (cancelItemsMenuItem.isVisible()) {
                    cancelItemsMenuItem.setVisible(false);
                }

                if (invoiceMenuItem.isVisible()) {
                    invoiceMenuItem.setVisible(false);
                }

                //FIXME: Por enquanto nao faz nada ao selecionar mais de Um
                /*
                for (OrderAdapterItem orderAdapterItem : orderAdapterItems) {
                    if (orderAdapterItem.getOrder().isStatusCreatedOrModified()) {
                        if (!cancelItemsMenuItem.isVisible()) {
                            cancelItemsMenuItem.setVisible(true);
                        }

                        if (!invoiceMenuItem.isVisible()) {
                            invoiceMenuItem.setVisible(true);
                        }
                    }

                    if (orderAdapterItem.getOrder().isStatusInvoice()) {
                        if (invoiceMenuItem.isVisible()) {
                            invoiceMenuItem.setVisible(false);
                        }
                    }

                    if (orderAdapterItem.getOrder().isStatusCanceled()) {
                        if (!removeItemsMenuItem.isVisible()) {
                            removeItemsMenuItem.setVisible(true);
                        }
                    }
                }*/

                return true;
            }

            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_cancel: {
                    Set<OrderAdapterItem> selectedItems = fastItemAdapter.getSelectedItems();

                    for (OrderAdapterItem orderAdapterItem : selectedItems) {
                        //LocalDataInjector.getOrderRealmDB(getContext()).delete(orderAdapterItem.getOrder().getId());
                        OrderJson order = orderAdapterItem.getOrder();
                        order.withStatus(OrderStatus.STATUS_CANCELLED);

                        LocalDataInjector.getOrderRealmDB(getContext())
                                .store(Collections.singletonList(LocalDataInjector.getOrderMapper().toEntity(order)));
                        loadOrders();
                    }
                }
                case R.id.action_delete: {
                    Set<OrderAdapterItem> selectedItems = fastItemAdapter.getSelectedItems();

                    for (OrderAdapterItem orderAdapterItem : selectedItems) {
                        LocalDataInjector.getOrderRealmDB(getContext()).delete(orderAdapterItem.getOrder().getId());
                        loadOrders();
                    }
                }
                case R.id.action_invoice: {

                    Set<OrderAdapterItem> selectedItems = fastItemAdapter.getSelectedItems();

                    if (selectedItems != null && selectedItems.size() == 1) {
                        OrderAdapterItem orderAdapterItem = selectedItems.iterator().next();

                        OrderJson order = orderAdapterItem.getOrder();
                        order.withStatus(OrderStatus.STATUS_INVOICED);

                        LocalDataInjector.getOrderRealmDB(getContext())
                                .store(Collections.singletonList(LocalDataInjector.getOrderMapper().toEntity(order)));
                        loadOrders();
                    }
                }
            }

            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(final ActionMode mode) {
        }
    }

}

