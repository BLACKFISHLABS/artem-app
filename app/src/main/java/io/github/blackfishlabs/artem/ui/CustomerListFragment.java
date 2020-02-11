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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.data.LocalDataInjector;
import io.github.blackfishlabs.artem.domain.entity.CustomerEntity;
import io.github.blackfishlabs.artem.domain.json.CustomerJson;
import io.github.blackfishlabs.artem.helper.PrefUtils;
import io.github.blackfishlabs.artem.ui.adapter.CustomerListAdapter;
import io.github.blackfishlabs.artem.ui.common.BaseFragment;
import io.github.blackfishlabs.artem.ui.event.SelectedCustomerEvent;
import io.github.blackfishlabs.artem.ui.widget.recyclerview.OnItemClickListener;
import io.github.blackfishlabs.artem.ui.widget.recyclerview.OnItemTouchListener;

import static java.util.Objects.requireNonNull;

public class CustomerListFragment extends BaseFragment implements OnRefreshListener, OnItemClickListener {

    public static final String TAG = CustomerListFragment.class.getName();

    @BindView(R.id.swipe_container_all_pull_refresh)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view_customers)
    protected RecyclerView mRecyclerViewCustomers;

    protected CustomerListAdapter mCustomerListAdapter;
    protected ViewTreeObserver.OnGlobalLayoutListener mRecyclerViewLayoutListener = null;
    protected OnItemTouchListener mRecyclerViewItemTouchListener = null;
    protected SearchView mSearchView;

    @Override
    protected int provideContentViewResource() {
        return R.layout.fragment_customer_list;
    }

    public static CustomerListFragment newInstance() {
        return new CustomerListFragment();
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

        mRecyclerViewCustomers.setHasFixedSize(true);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerViewCustomers.setLayoutManager(layoutManager);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_all_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setQueryHint(getString(R.string.customer_list_search_hint));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mCustomerListAdapter != null) {
                    mCustomerListAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.action_all_search).setVisible(mCustomerListAdapter != null && !mCustomerListAdapter.isEmptyList());
    }

    @Override
    public void onRefresh() {
        loadCustomers();
    }

    @Override
    public void onSingleTapUp(View view, int position) {
        CustomerJson selectedCustomer = mCustomerListAdapter.getCustomer(position);
        if (selectedCustomer != null) {
            onSelectedCustomerFromList(selectedCustomer);
        }
    }

    protected void onSelectedCustomerFromList(@NonNull CustomerJson selectedCustomer) {
        eventBus().postSticky(SelectedCustomerEvent.selectCustomer(selectedCustomer));
        navigate().toNewCustomer();
    }

    @Override
    public void onLongPress(View view, int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getHostActivity());

        alert.setTitle(getString(R.string.app_name));
        alert.setMessage("Deseja deletar este item ?");

        alert.setCancelable(false);
        alert
                .setNegativeButton("Não", (dialogInterface, i) -> {
                })
                .setPositiveButton("Sim", (dialogInterface, i) -> {
                    CustomerJson selectedCustomer = mCustomerListAdapter.getCustomer(position);
                    if (selectedCustomer != null) {
                        LocalDataInjector.getCustomerRealmDB(getContext()).delete(selectedCustomer.getId());
                        loadCustomers();
                    }
                });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private void loadCustomers() {
        if (PrefUtils.getAtelierKey(getContext()) != null) {
            startLoadingCustomers();
            List<CustomerEntity> customers = LocalDataInjector.getCustomerRealmDB(getContext()).findByOwner(PrefUtils.getAtelierKey(getContext()));
            if (!customers.isEmpty()) {
                mRecyclerViewCustomers.setVisibility(View.VISIBLE);
                mRecyclerViewCustomers.setAdapter(mCustomerListAdapter = new CustomerListAdapter(LocalDataInjector.getCustomerMapper().toViewObjects(customers)));
                mRecyclerViewCustomers
                        .getViewTreeObserver()
                        .addOnGlobalLayoutListener(
                                mRecyclerViewLayoutListener = this::onRecyclerViewFinishLoading);
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void startLoadingCustomers() {
        mSwipeRefreshLayout.setRefreshing(true);
        mCustomerListAdapter = null;
        mRecyclerViewCustomers.setAdapter(null);
        mRecyclerViewCustomers.setVisibility(View.GONE);
        requireNonNull(mLinearLayoutEmptyState).setVisibility(View.VISIBLE);
        requireNonNull(getActivity()).invalidateOptionsMenu();
    }

    protected void onRecyclerViewFinishLoading() {
        if (getView() != null) {
            mRecyclerViewCustomers
                    .getViewTreeObserver()
                    .removeOnGlobalLayoutListener(mRecyclerViewLayoutListener);
            mRecyclerViewLayoutListener = null;

            if (mRecyclerViewItemTouchListener != null) {
                mRecyclerViewCustomers.removeOnItemTouchListener(mRecyclerViewItemTouchListener);
                mRecyclerViewItemTouchListener = null;
            }

            mRecyclerViewCustomers.addOnItemTouchListener(
                    mRecyclerViewItemTouchListener = new OnItemTouchListener(getHostActivity(), mRecyclerViewCustomers, this));

            requireNonNull(getActivity()).invalidateOptionsMenu();
            mSwipeRefreshLayout.setRefreshing(false);
            requireNonNull(mLinearLayoutEmptyState).setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.button_all_retry)
    void onButtonRetryClicked() {
        requireNonNull(mLinearLayoutErrorState).setVisibility(View.GONE);
        loadCustomers();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCustomers();
    }
}
