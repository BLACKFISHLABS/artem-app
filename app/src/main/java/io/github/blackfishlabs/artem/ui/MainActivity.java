package io.github.blackfishlabs.artem.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.List;

import butterknife.OnClick;
import io.github.blackfishlabs.artem.BuildConfig;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.helper.AndroidUtils;
import io.github.blackfishlabs.artem.ui.common.BaseActivity;

import static java.util.Objects.requireNonNull;

public class MainActivity extends BaseActivity implements Drawer.OnDrawerItemClickListener {

    private static final int DRAWER_ITEM_DASHBOARD = 1;
    private static final int DRAWER_ITEM_PRODUCT = 2;
    private static final int DRAWER_ITEM_CUSTOMER = 3;
    private static final int DRAWER_ITEM_ORDER = 4;
    private static final int DRAWER_ITEM_ATELIER = 5;
    private static final int DRAWER_ITEM_REPORT = 6;

    private static final int DRAWER_ITEM_ABOUT = 10;
    private static final int DRAWER_ITEM_INFO = 11;
    private static final int DRAWER_ITEM_EXIT = 12;
    private static final int DRAWER_ITEM_PAG_SEGURO = 13;

    private AccountHeader accountHeader;

    private PrimaryDrawerItem dashboardDrawerItem;
    private PrimaryDrawerItem productDrawerItem;
    private PrimaryDrawerItem customerDrawerItem;
    private PrimaryDrawerItem orderDrawerItem;
    private PrimaryDrawerItem atelierDrawerItem;
    private PrimaryDrawerItem reportDrawerItem;

    private SecondaryDrawerItem aboutDrawerItem;
    private SecondaryDrawerItem infoDrawerItem;
    private SecondaryDrawerItem exitDrawerItem;
    private SecondaryDrawerItem adsDrawerItem;

    private BottomSheetBehavior<View> bottomSheetBehavior;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected int provideContentViewResource() {
        return R.layout.activity_main;
    }

    @OnClick(R.id.fab_all_main_action)
    void onFabClicked() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle inState) {
        super.onCreate(inState);
        setAsHomeActivity();

        initDrawerHeader(inState);
        initDrawer(inState);
        initBottomSheet();
    }

    private void initDrawerHeader(final Bundle inState) {
        String nameToShow = getString(R.string.app_name);
        String emailToShow = getString(R.string.blackfishlabs_github_io);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (TextUtils.isEmpty(user.getDisplayName()))
                nameToShow = requireNonNull(user.getEmail()).substring(0, user.getEmail().indexOf("@"));
            else
                nameToShow = user.getDisplayName();

            emailToShow = user.getEmail();
        }

        List<IProfile> profiles = Lists.newArrayList();
        TextDrawable.IShapeBuilder builder = createShapeBuilder();
        final String nameWithTwoLetters = generateNameWithTwoLetters(requireNonNull(nameToShow));

        ProfileDrawerItem profile = new ProfileDrawerItem()
                .withName(nameToShow.toUpperCase())
                .withEmail(emailToShow)
                .withTypeface(AndroidUtils.getFont(this));
                //.withIcon(builder.buildRound(nameWithTwoLetters, Color.TRANSPARENT));

        if (user != null && user.getPhotoUrl() != null) {
            profile.withIcon(user.getPhotoUrl());
        } else {
            profile.withIcon(builder.buildRound(nameWithTwoLetters, Color.WHITE));
        }

        profiles.add(profile);

        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withSavedInstance(inState)
                .withOnlyMainProfileImageVisible(true)
                .withCurrentProfileHiddenInList(true)
                .withSelectionListEnabledForSingleProfile(false)
                .withProfiles(profiles)
                .withCompactStyle(true)
                .withTypeface(AndroidUtils.getFont(this))
                .withTextColor(ContextCompat.getColor(this, R.color.white))
                .withHeaderBackground(R.color.colorPrimaryDark)
                .build();
    }

    private TextDrawable.IShapeBuilder createShapeBuilder() {
        return TextDrawable.builder()
                .beginConfig()
                .fontSize(AndroidUtils.dpToPx(this, 20))
                .toUpperCase()
                .useFont(AndroidUtils.getFont(this))
                .endConfig();
    }

    private String generateNameWithTwoLetters(String name) {
        String[] parts = name.trim().split("\\s+");

        if (parts.length == 1) {
            return parts[0].substring(0, 2);
        } else {
            return parts[0].substring(0, 1).concat(parts[1].substring(0, 1));
        }
    }

    private void initDrawer(final Bundle inState) {
        List<IDrawerItem> menuPrimaryToShow = Lists.newArrayList(
                createDashboardDrawerItem(),
                createProductDrawerItem(),
                createCustomerDrawerItem(),
                createOrderDrawerItem(),
                createAtelierDrawerItem(),
                createReportDrawerItem(),
                new DividerDrawerItem(),
                createAdsDrawerItem(),
                createAboutDrawerItem(),
                createInfoDrawerItem(),
                createExitDrawerItem());

        new DrawerBuilder()
                .withActivity(this)
                .withToolbar(requireNonNull(mToolbar))
                .withAccountHeader(accountHeader)
                .withDrawerItems(menuPrimaryToShow)
                .withSavedInstance(inState)
                .withShowDrawerOnFirstLaunch(true)
                .withOnDrawerItemClickListener(this)
                .withSelectedItem(DRAWER_ITEM_DASHBOARD)
                .withFireOnInitialOnClick(true)
                .withActionBarDrawerToggle(false)
                .build();
    }

    private void initBottomSheet() {
        final View bottomSheetView = getLayoutInflater().inflate(R.layout.view_bottom_sheet, null);
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setOnDismissListener(dialog ->
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
        bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());

        bottomSheetView.findViewById(R.id.main_new_order)
                .setOnClickListener(v -> {
                    navigate().toNewOrder();
                    bottomSheetDialog.dismiss();
                });
        bottomSheetView.findViewById(R.id.main_new_customer)
                .setOnClickListener(v -> {
                    navigate().toNewCustomer();
                    bottomSheetDialog.dismiss();
                });
        bottomSheetView.findViewById(R.id.main_new_product)
                .setOnClickListener(v -> {
                    navigate().toNewProduct();
                    bottomSheetDialog.dismiss();
                });
    }

    private PrimaryDrawerItem createDashboardDrawerItem() {
        if (dashboardDrawerItem == null) {
            dashboardDrawerItem = new PrimaryDrawerItem()
                    .withIdentifier(DRAWER_ITEM_DASHBOARD)
                    .withTypeface(AndroidUtils.getFont(this))
                    .withName(R.string.title_dashboard)
                    .withIcon(VectorDrawableCompat
                            .create(getResources(), R.drawable.ic_dashboard, getTheme()));
        }
        return dashboardDrawerItem;
    }

    private PrimaryDrawerItem createProductDrawerItem() {
        if (productDrawerItem == null) {
            productDrawerItem = new PrimaryDrawerItem()
                    .withIdentifier(DRAWER_ITEM_PRODUCT)
                    .withTypeface(AndroidUtils.getFont(this))
                    .withName(R.string.title_product)
                    .withIcon(VectorDrawableCompat
                            .create(getResources(), R.drawable.ic_local_mall, getTheme()));
        }
        return productDrawerItem;
    }

    private PrimaryDrawerItem createCustomerDrawerItem() {
        if (customerDrawerItem == null) {
            customerDrawerItem = new PrimaryDrawerItem()
                    .withIdentifier(DRAWER_ITEM_CUSTOMER)
                    .withTypeface(AndroidUtils.getFont(this))
                    .withName(R.string.title_customer)
                    .withIcon(VectorDrawableCompat
                            .create(getResources(), R.drawable.ic_group, getTheme()));
        }
        return customerDrawerItem;
    }

    private PrimaryDrawerItem createOrderDrawerItem() {
        if (orderDrawerItem == null) {
            orderDrawerItem = new PrimaryDrawerItem()
                    .withIdentifier(DRAWER_ITEM_ORDER)
                    .withTypeface(AndroidUtils.getFont(this))
                    .withName(R.string.title_order)
                    .withIcon(VectorDrawableCompat
                            .create(getResources(), R.drawable.ic_content_paste, getTheme()));
        }
        return orderDrawerItem;
    }

    private PrimaryDrawerItem createAtelierDrawerItem() {
        if (atelierDrawerItem == null) {
            atelierDrawerItem = new PrimaryDrawerItem()
                    .withIdentifier(DRAWER_ITEM_ATELIER)
                    .withTypeface(AndroidUtils.getFont(this))
                    .withName(R.string.title_atelier)
                    .withIcon(VectorDrawableCompat
                            .create(getResources(), R.drawable.ic_color, getTheme()));
        }
        return atelierDrawerItem;
    }

    private PrimaryDrawerItem createReportDrawerItem() {
        if (reportDrawerItem == null) {
            reportDrawerItem = new PrimaryDrawerItem()
                    .withIdentifier(DRAWER_ITEM_REPORT)
                    .withTypeface(AndroidUtils.getFont(this))
                    .withName(R.string.title_report)
                    .withIcon(VectorDrawableCompat
                            .create(getResources(), R.drawable.ic_format_align_center, getTheme()));
        }
        return reportDrawerItem;
    }

    private SecondaryDrawerItem createAboutDrawerItem() {
        if (aboutDrawerItem == null) {
            aboutDrawerItem = new SecondaryDrawerItem()
                    .withIdentifier(DRAWER_ITEM_ABOUT)
                    .withTypeface(AndroidUtils.getFont(this))
                    .withName(R.string.title_about_developer)
                    .withDescription("Desenvolvedor")
                    .withIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_developer_mode, getTheme()));
        }

        return aboutDrawerItem;
    }

    private SecondaryDrawerItem createInfoDrawerItem() {
        if (infoDrawerItem == null) {
            infoDrawerItem = new SecondaryDrawerItem()
                    .withIdentifier(DRAWER_ITEM_INFO)
                    .withTypeface(AndroidUtils.getFont(this))
                    .withIcon(VectorDrawableCompat
                            .create(getResources(), R.drawable.ic_bookmark_border, getTheme()))
                    .withName("Versão do Aplicativo")
                    .withDescription(BuildConfig.VERSION_NAME)
                    .withSelectable(false);
        }
        return infoDrawerItem;
    }

    private SecondaryDrawerItem createExitDrawerItem() {
        if (exitDrawerItem == null) {
            exitDrawerItem = new SecondaryDrawerItem()
                    .withIdentifier(DRAWER_ITEM_EXIT)
                    .withName(R.string.title_exit)
                    .withTypeface(AndroidUtils.getFont(this))
                    .withIcon(VectorDrawableCompat
                            .create(getResources(), R.drawable.ic_exit_to_app, getTheme()));
        }

        return exitDrawerItem;
    }

    private SecondaryDrawerItem createAdsDrawerItem() {
        if (adsDrawerItem == null) {
            adsDrawerItem = new SecondaryDrawerItem()
                    .withIdentifier(DRAWER_ITEM_PAG_SEGURO)
                    .withName(getString(R.string.title_ads))
                    .withDescription("Maquininhas de Cartões")
                    .withTypeface(AndroidUtils.getFont(this))
                    .withIcon(VectorDrawableCompat
                            .create(getResources(), R.drawable.ic_credit_card, getTheme()));
        }

        return adsDrawerItem;
    }


    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        try {
            Preconditions.checkNotNull(drawerItem);
        } catch (NullPointerException e) {
            return false;
        }

        switch ((int) drawerItem.getIdentifier()) {
            case DRAWER_ITEM_DASHBOARD: {
                if (!isViewingDashboard()) {
                    navigate().toDashboard();
                }
                break;
            }

            case DRAWER_ITEM_PRODUCT: {
                if (!isViewingMyProducts()) {
                    navigate().toMyProducts();
                }
                break;
            }

            case DRAWER_ITEM_CUSTOMER: {
                if (!isViewingMyCustomers()) {
                    navigate().toMyCustomers();
                }
                break;
            }

            case DRAWER_ITEM_ORDER: {
                if (!isViewingMyOrders()) {
                    navigate().toMyOrders();
                }
                break;
            }

            case DRAWER_ITEM_ATELIER: {
                if (!isViewingMyAtelier()) {
                    navigate().toMyAtelier();
                }
                break;
            }

            case DRAWER_ITEM_REPORT: {
                if (!isViewingReport()) {
                    navigate().toOrdersReport();
                }
                break;
            }

            case DRAWER_ITEM_ABOUT: {
                if (!isViewingAbout()) {
                    navigate().toAbout();
                }
                break;
            }

            case DRAWER_ITEM_PAG_SEGURO: {
                if (!isViewingAds()) {
                    navigate().toAds();
                }
                break;
            }


            case DRAWER_ITEM_EXIT: {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                alert.setTitle(getString(R.string.app_name));
                alert.setMessage("Deseja realmente sair da sua conta atual?");

                alert.setCancelable(false);
                alert
                        .setNegativeButton("VOLTAR", (dialogInterface, i) -> {
                        })
                        .setPositiveButton("SAIR", (dialogInterface, i) -> signOut());

                AlertDialog alertDialog = alert.create();
                alertDialog.show();

                TextView textView = alertDialog.findViewById(android.R.id.message);
                requireNonNull(textView).setTypeface(AndroidUtils.getFont(this));

                break;
            }
        }

        return false;
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    navigate().toWelcome();
                    finish();
                });
    }

    private boolean isViewingDashboard() {
        return isViewingFragmentByTag(DashboardFragment.TAG);
    }

    private boolean isViewingMyProducts() {
        return isViewingFragmentByTag(ProductListFragment.TAG);
    }

    private boolean isViewingMyCustomers() {
        return isViewingFragmentByTag(CustomerListFragment.TAG);
    }

    private boolean isViewingMyOrders() {
        return isViewingFragmentByTag(OrderListFragment.TAG);
    }

    private boolean isViewingMyAtelier() {
        return isViewingFragmentByTag(AtelierFragment.TAG);
    }

    private boolean isViewingAbout() {
        return isViewingFragmentByTag(AboutFragment.TAG);
    }

    private boolean isViewingReport() {
        return isViewingFragmentByTag(OrdersReportFragment.TAG);
    }

    private boolean isViewingAds() {
        return isViewingFragmentByTag(AdsFragment.TAG);
    }

    private boolean isViewingFragmentByTag(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        return fragment != null && fragment.isVisible();
    }
}
