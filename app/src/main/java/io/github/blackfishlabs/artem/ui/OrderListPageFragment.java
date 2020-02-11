package io.github.blackfishlabs.artem.ui;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.ui.common.BaseFragment;
import io.github.blackfishlabs.artem.ui.widget.TabAdapter;

import static java.util.Objects.requireNonNull;

public class OrderListPageFragment extends BaseFragment {

    public static final String TAG = OrderListPageFragment.class.getName();

    private AppBarLayout mAppBarLayoutActivity;
    private TabLayout mTabLayout;
    private TabAdapter mTabAdapter;

    @BindView(R.id.view_pager_all_container)
    protected ViewPager mViewPager;

    public static OrderListPageFragment newInstance() {
        return new OrderListPageFragment();
    }

    @Override
    protected int provideContentViewResource() {
        return R.layout.fragment_tabs;
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mAppBarLayoutActivity == null) {
            mAppBarLayoutActivity = requireNonNull(getActivity()).findViewById(R.id.app_bar_all_actionbar);
        }

        if (mTabLayout == null) {
            mTabLayout = (TabLayout) LayoutInflater.from(getContext())
                    .inflate(R.layout.view_tab_layout, mAppBarLayoutActivity, false);

            mTabAdapter = new TabAdapter(getChildFragmentManager());
            mTabAdapter.addFragment(OrderListFragment.newInstance(false),
                    getString(R.string.order_list_all_orders_fragment_title));
            mTabAdapter.addFragment(OrderListFragment.newInstance(true),
                    getString(R.string.order_list_pending_orders_fragment_title));
            mViewPager.setAdapter(mTabAdapter);
            mViewPager.setOffscreenPageLimit(2);

            mTabLayout.setupWithViewPager(mViewPager);
        }

        mAppBarLayoutActivity.addView(mTabLayout);
    }

    @Override
    public void onDestroyView() {
        mAppBarLayoutActivity.removeView(mTabLayout);
        super.onDestroyView();
    }
}
