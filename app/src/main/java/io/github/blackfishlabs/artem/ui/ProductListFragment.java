package io.github.blackfishlabs.artem.ui;

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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.data.LocalDataInjector;
import io.github.blackfishlabs.artem.domain.entity.ProductEntity;
import io.github.blackfishlabs.artem.domain.json.ProductJson;
import io.github.blackfishlabs.artem.helper.PrefUtils;
import io.github.blackfishlabs.artem.ui.adapter.ProductListAdapter;
import io.github.blackfishlabs.artem.ui.common.BaseFragment;
import io.github.blackfishlabs.artem.ui.event.SelectedProductEvent;
import io.github.blackfishlabs.artem.ui.widget.recyclerview.OnItemClickListener;
import io.github.blackfishlabs.artem.ui.widget.recyclerview.OnItemTouchListener;

import static java.util.Objects.requireNonNull;

public class ProductListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, OnItemClickListener {

    public static final String TAG = ProductListFragment.class.getName();

    @BindView(R.id.swipe_container_all_pull_refresh)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view_products)
    protected RecyclerView mRecyclerViewProducts;

    protected ProductListAdapter mProductListAdapter;
    protected ViewTreeObserver.OnGlobalLayoutListener mRecyclerViewLayoutListener = null;
    protected OnItemTouchListener mRecyclerViewItemTouchListener = null;
    protected SearchView mSearchView;

    @Override
    protected int provideContentViewResource() {
        return R.layout.fragment_product_list;
    }

    public static ProductListFragment newInstance() {
        return new ProductListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle inState) {
        View view = super.onCreateView(inflater, container, inState);

        mRecyclerViewProducts.setHasFixedSize(true);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerViewProducts.setLayoutManager(layoutManager);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.ms_white);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_all_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setQueryHint(getString(R.string.product_list_search_hint));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mProductListAdapter != null) {
                    mProductListAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.action_all_search).setVisible(mProductListAdapter != null && !mProductListAdapter.isEmptyList());
    }

    @Override
    public void onRefresh() {
        loadProducts();
    }

    @Override
    public void onSingleTapUp(View view, int position) {
        ProductJson selected = mProductListAdapter.getProduct(position);
        if (selected != null) {
            onSelectedFromList(selected);
        }
    }

    protected void onSelectedFromList(@NonNull ProductJson selected) {
        eventBus().postSticky(SelectedProductEvent.selectProduct(selected));
        navigate().toNewProduct();
    }

    @Override
    public void onLongPress(View view, int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getHostActivity());

        alert.setTitle(getString(R.string.app_name));
        alert.setMessage("Deseja deletar este item?");

        alert.setCancelable(false);
        alert
                .setNegativeButton("Não", (dialogInterface, i) -> {
                })
                .setPositiveButton("Sim", (dialogInterface, i) -> {
                    ProductJson selected = mProductListAdapter.getProduct(position);
                    if (selected != null) {
                        LocalDataInjector.getProductRealmDB(getContext()).delete(selected.getId());
                        loadProducts();
                    }
                });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private void loadProducts() {
        if (PrefUtils.getAtelierKey(getContext()) != null) {
            startLoading();
            List<ProductEntity> products = LocalDataInjector.getProductRealmDB(getContext()).findByOwner(PrefUtils.getAtelierKey(getContext()));
            if (!products.isEmpty()) {
                mRecyclerViewProducts.setVisibility(View.VISIBLE);
                mRecyclerViewProducts.setAdapter(mProductListAdapter = new ProductListAdapter(LocalDataInjector.getProductMapper().toViewObjects(products)));
                mRecyclerViewProducts
                        .getViewTreeObserver()
                        .addOnGlobalLayoutListener(
                                mRecyclerViewLayoutListener = this::onRecyclerViewFinishLoading);
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void startLoading() {
        mSwipeRefreshLayout.setRefreshing(true);
        mProductListAdapter = null;
        mRecyclerViewProducts.setAdapter(null);
        mRecyclerViewProducts.setVisibility(View.GONE);
        requireNonNull(mLinearLayoutEmptyState).setVisibility(View.VISIBLE);
        requireNonNull(getActivity()).invalidateOptionsMenu();
    }

    protected void onRecyclerViewFinishLoading() {
        if (getView() != null) {
            mRecyclerViewProducts
                    .getViewTreeObserver()
                    .removeOnGlobalLayoutListener(mRecyclerViewLayoutListener);
            mRecyclerViewLayoutListener = null;

            if (mRecyclerViewItemTouchListener != null) {
                mRecyclerViewProducts.removeOnItemTouchListener(mRecyclerViewItemTouchListener);
                mRecyclerViewItemTouchListener = null;
            }

            mRecyclerViewProducts.addOnItemTouchListener(mRecyclerViewItemTouchListener =
                    new OnItemTouchListener(getHostActivity(), mRecyclerViewProducts, this));

            requireNonNull(getActivity()).invalidateOptionsMenu();
            mSwipeRefreshLayout.setRefreshing(false);
            requireNonNull(mLinearLayoutEmptyState).setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.button_all_retry)
    void onButtonRetryClicked() {
        requireNonNull(mLinearLayoutErrorState).setVisibility(View.GONE);
        loadProducts();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProducts();
    }
}
