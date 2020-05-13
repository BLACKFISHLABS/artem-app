package io.github.blackfishlabs.artem.ui.common;

import android.content.Intent;
import android.net.Uri;

import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.ui.AboutFragment;
import io.github.blackfishlabs.artem.ui.AddCustomerActivity;
import io.github.blackfishlabs.artem.ui.AddOrderActivity;
import io.github.blackfishlabs.artem.ui.AddProductCostActivity;
import io.github.blackfishlabs.artem.ui.AdsFragment;
import io.github.blackfishlabs.artem.ui.AtelierFragment;
import io.github.blackfishlabs.artem.ui.CustomerListFragment;
import io.github.blackfishlabs.artem.ui.DashboardFragment;
import io.github.blackfishlabs.artem.ui.EditInfoAtelierActivity;
import io.github.blackfishlabs.artem.ui.EditInfoValuePerHourActivity;
import io.github.blackfishlabs.artem.ui.FixedExpensesActivity;
import io.github.blackfishlabs.artem.ui.InvestmentActivity;
import io.github.blackfishlabs.artem.ui.MainActivity;
import io.github.blackfishlabs.artem.ui.OrderListPageFragment;
import io.github.blackfishlabs.artem.ui.OrdersReportFragment;
import io.github.blackfishlabs.artem.ui.ProductListFragment;
import io.github.blackfishlabs.artem.ui.ViewOrderActivity;
import io.github.blackfishlabs.artem.ui.WelcomeActivity;

public class Navigator {

    private final BaseActivity mActivity;

    Navigator(final BaseActivity activity) {
        mActivity = activity;
    }

    public void toDashboard() {
        mActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout_all_fragment_container,
                        DashboardFragment.newInstance(), DashboardFragment.TAG)
                .commitNow();

        FloatingActionButton fab = mActivity.findViewById(R.id.fab_all_main_action);
        fab.show();

        mActivity.setTitle(R.string.title_dashboard);
    }

    public void toMyProducts() {
        mActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout_all_fragment_container,
                        ProductListFragment.newInstance(), ProductListFragment.TAG)
                .commitNow();

        FloatingActionButton fab = mActivity.findViewById(R.id.fab_all_main_action);
        fab.show();

        mActivity.setTitle(R.string.title_product);
    }

    public void toMyCustomers() {
        mActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout_all_fragment_container,
                        CustomerListFragment.newInstance(), CustomerListFragment.TAG)
                .commitNow();

        FloatingActionButton fab = mActivity.findViewById(R.id.fab_all_main_action);
        fab.show();

        mActivity.setTitle(R.string.title_customer);
    }

    public void toMyOrders() {
        mActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout_all_fragment_container,
                        OrderListPageFragment.newInstance(), OrderListPageFragment.TAG)
                .commit();

        FloatingActionButton fab = mActivity.findViewById(R.id.fab_all_main_action);
        fab.show();

        mActivity.setTitle(R.string.order_list_fragment_title);
    }

    public void toMyAtelier() {
        mActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout_all_fragment_container,
                        AtelierFragment.newInstance(), AtelierFragment.TAG)
                .commitNow();

        FloatingActionButton fab = mActivity.findViewById(R.id.fab_all_main_action);
        fab.hide();

        mActivity.setTitle(R.string.title_atelier);
    }

    public void toAbout() {
        mActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout_all_fragment_container,
                        AboutFragment.newInstance(), AboutFragment.TAG)
                .commit();

        FloatingActionButton fab = mActivity.findViewById(R.id.fab_all_main_action);
        fab.hide();

        mActivity.setTitle(R.string.title_about_developer);
    }

    public void toAds() {
        mActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout_all_fragment_container,
                        AdsFragment.newInstance(), AdsFragment.TAG)
                .commit();

        FloatingActionButton fab = mActivity.findViewById(R.id.fab_all_main_action);
        fab.hide();

        mActivity.setTitle(R.string.title_ads);
    }

    public void toOrdersReport() {
        mActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout_all_fragment_container,
                        OrdersReportFragment.newInstance(), OrdersReportFragment.TAG)
                .commit();

        FloatingActionButton fab = mActivity.findViewById(R.id.fab_all_main_action);
        fab.hide();

        mActivity.setTitle(R.string.orders_report_fragment_title);
    }

    public void toMainActivity() {
        ActivityCompat.startActivity(mActivity, new Intent(mActivity, MainActivity.class), null);
    }

    public void toWelcome() {
        ActivityCompat.startActivity(mActivity, new Intent(mActivity, WelcomeActivity.class), null);
    }

    public void toWebSite(String site) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(site));
        ActivityCompat.startActivity(mActivity, browserIntent, null);
    }

    public void toNewOrder() {
        ActivityCompat.startActivity(mActivity, new Intent(mActivity, AddOrderActivity.class), null);
    }

    public void toViewOrder() {
        ActivityCompat.startActivity(mActivity, new Intent(mActivity, ViewOrderActivity.class), null);
    }

    public void toNewCustomer() {
        ActivityCompat.startActivity(mActivity, new Intent(mActivity, AddCustomerActivity.class), null);
    }

    public void toNewProduct() {
        ActivityCompat.startActivity(mActivity, new Intent(mActivity, AddProductCostActivity.class), null);
    }

    public void toEditInfoAtelier() {
        ActivityCompat.startActivity(mActivity, new Intent(mActivity, EditInfoAtelierActivity.class), null);
    }

    public void toEditInfoFixedSpending() {
        ActivityCompat.startActivity(mActivity, new Intent(mActivity, FixedExpensesActivity.class), null);
    }

    public void toEditInfoInvestment() {
        ActivityCompat.startActivity(mActivity, new Intent(mActivity, InvestmentActivity.class), null);
    }

    public void toEditInfoValuePerHour() {
        ActivityCompat.startActivity(mActivity, new Intent(mActivity, EditInfoValuePerHourActivity.class), null);
    }
}
