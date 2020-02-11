package io.github.blackfishlabs.artem.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.data.LocalDataInjector;
import io.github.blackfishlabs.artem.domain.entity.ProductEntity;
import io.github.blackfishlabs.artem.domain.json.OrderItemJson;
import io.github.blackfishlabs.artem.domain.json.OrderJson;
import io.github.blackfishlabs.artem.domain.json.ProductJson;
import io.github.blackfishlabs.artem.helper.PrefUtils;
import io.github.blackfishlabs.artem.ui.adapter.SelectOrderItemsAdapter;
import io.github.blackfishlabs.artem.ui.adapter.SelectOrderItemsCallbacks;
import io.github.blackfishlabs.artem.ui.common.BaseFragment;
import io.github.blackfishlabs.artem.ui.event.AddedOrderItemsEvent;
import io.github.blackfishlabs.artem.ui.event.SelectedOrderEvent;
import io.realm.RealmResults;

import static java.util.Objects.requireNonNull;

public class SelectOrderItemsStepFragment extends BaseFragment implements Step, SelectOrderItemsCallbacks {

    private OrderJson selectedOrder;
    private List<OrderItemJson> orderItems;
    private Set<OrderItemJson> selectedOrderItems;

    private SearchView searchView;

    @BindView(R.id.swipe_container_all_pull_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view_order_items)
    RecyclerView recyclerViewOrderItems;

    private SelectOrderItemsAdapter selectOrderItemsAdapter;

    private ViewTreeObserver.OnGlobalLayoutListener recyclerViewLayoutListener = null;

    public static SelectOrderItemsStepFragment newInstance() {
        return new SelectOrderItemsStepFragment();
    }

    @Override
    protected int provideContentViewResource() {
        return R.layout.fragment_select_order_items;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        SelectedOrderEvent event = eventBus().getStickyEvent(SelectedOrderEvent.class);
        if (event != null) {
            selectedOrder = event.getOrder();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle inState) {
        final View view = super.onCreateView(inflater, container, inState);

        recyclerViewOrderItems.setHasFixedSize(true);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewOrderItems.setLayoutManager(layoutManager);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.ms_white);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadProducts();
    }

    private void loadProducts() {
        startLoadingProducts();

        RealmResults<ProductEntity> products = LocalDataInjector
                .getProductRealmDB(getContext())
                .findByOwner(PrefUtils.getAtelierKey(getContext()));
        List<ProductJson> viewProducts = LocalDataInjector
                .getProductMapper()
                .toViewObjects(products);
        for (ProductJson product : viewProducts) {
            orderItems.add(createOrderItem(product));
        }

        showOrderItems();
    }

    private void showOrderItems() {
        if (!orderItems.isEmpty()) {
            recyclerViewOrderItems.setVisibility(View.VISIBLE);
            recyclerViewOrderItems.setAdapter(
                    selectOrderItemsAdapter = new SelectOrderItemsAdapter(orderItems, this, getContext()));
            recyclerViewOrderItems
                    .getViewTreeObserver()
                    .addOnGlobalLayoutListener(
                            recyclerViewLayoutListener = this::onRecyclerViewFinishLoading);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
        }

    }

    private void onRecyclerViewFinishLoading() {
        if (getView() != null) {
            recyclerViewOrderItems
                    .getViewTreeObserver()
                    .removeOnGlobalLayoutListener(recyclerViewLayoutListener);
            recyclerViewLayoutListener = null;

            requireNonNull(getActivity()).invalidateOptionsMenu();
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(false);
            assert mLinearLayoutEmptyState != null;
            mLinearLayoutEmptyState.setVisibility(View.GONE);
        }
    }

    private void startLoadingProducts() {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
            selectedOrderItems = new LinkedHashSet<>();
        } else {
            orderItems.clear();
            selectedOrderItems.clear();
        }
        mSwipeRefreshLayout.setRefreshing(true);
        selectOrderItemsAdapter = null;
        recyclerViewOrderItems.setAdapter(null);
        recyclerViewOrderItems.setVisibility(View.GONE);
        requireNonNull(mLinearLayoutEmptyState).setVisibility(View.VISIBLE);
        requireNonNull(getActivity()).invalidateOptionsMenu();
    }

    private OrderItemJson createOrderItem(final ProductJson productJson) {
        OrderItemJson orderItem;
        if (selectedOrder != null) {
            for (final OrderItemJson item : selectedOrder.getItems()) {
                if (item.getItem().equals(productJson)) {
                    orderItem = new OrderItemJson()
                            .withTempId(UUID.randomUUID().toString())
                            .withItem(productJson)
                            .withQuantity(item.getQuantity());

                    if (selectedOrderItems == null)
                        selectedOrderItems = new LinkedHashSet<>();
                    selectedOrderItems.add(orderItem);
                    return orderItem;
                }
            }
        }

        orderItem = new OrderItemJson()
                .withTempId(UUID.randomUUID().toString())
                .withItem(productJson)
                .withQuantity(0.0f);
        return orderItem;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_all_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.select_order_items_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (selectOrderItemsAdapter != null) {
                    selectOrderItemsAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.action_all_search).setVisible(selectOrderItemsAdapter != null && !selectOrderItemsAdapter.isEmptyList());
    }

    @OnClick(R.id.button_all_retry)
    void onButtonRetryClicked() {
        requireNonNull(mLinearLayoutErrorState).setVisibility(View.GONE);
        loadProducts();
    }


    @Override
    public VerificationError verifyStep() {
        if (!TextUtils.isEmpty(searchView.getQuery())) {
            searchView.setQuery("", false);
            searchView.clearFocus();
            selectOrderItemsAdapter.getFilter().filter("");
        }

        List<OrderItemJson> addedOrderItems = getSelectedOrderItems();
        if (!addedOrderItems.isEmpty()) {
            eventBus().post(AddedOrderItemsEvent.newEvent(addedOrderItems));
            return null;
        } else {
            return new VerificationError(getString(R.string.select_order_items_no_order_items));
        }
    }

    private List<OrderItemJson> getSelectedOrderItems() {
        if (selectedOrderItems == null || selectedOrderItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<OrderItemJson> orderItems = new ArrayList<>();
        for (final OrderItemJson item : selectedOrderItems) {
            if (item.getQuantity() > 0) {
                orderItems.add(item);
            }
        }
        return orderItems;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {
        Snackbar.make(requireNonNull(getView()), error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onAddOrderItemRequested(OrderItemJson orderItem, int position) {
        final float quantity = orderItem.getQuantity() + 1;
        if (checkQuantity(quantity)) {
            orderItem.addQuantity(1);
            selectOrderItemsAdapter.notifyItemChanged(position);
            selectedOrderItems.add(orderItem);
        }
    }

    @Override
    public void onRemoveOrderItemRequested(OrderItemJson orderItem, int position) {
        final Float quantity = orderItem.getQuantity();
        if (quantity != null && quantity >= 1) {
            orderItem.removeOneFromQuantity();
            selectOrderItemsAdapter.notifyItemChanged(position);
            if (orderItem.getQuantity() == 0)
                selectedOrderItems.remove(orderItem);
        }
    }

    private boolean checkQuantity(final float quantity) {
        if (quantity < 0) {
            Snackbar.make(requireNonNull(getView()), R.string.select_order_items_invalid_quantity, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onChangeOrderItemQuantityRequested(OrderItemJson orderItem, float quantity, int position) {
        if (checkQuantity(quantity)) {
            orderItem.withQuantity(quantity);
            selectOrderItemsAdapter.notifyItemChanged(position);
            if (orderItem.getQuantity() == 0)
                selectedOrderItems.remove(orderItem);
            else
                selectedOrderItems.add(orderItem);
        }
    }

}
