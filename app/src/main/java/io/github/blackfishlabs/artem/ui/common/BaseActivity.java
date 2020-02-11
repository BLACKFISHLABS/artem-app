package io.github.blackfishlabs.artem.ui.common;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.ui.PresentationInjector;

public abstract class BaseActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Navigator mNavigator;

    @Nullable
    @BindView(R.id.toolbar_all_actionbar)
    protected Toolbar mToolbar;

    @LayoutRes
    protected abstract int provideContentViewResource();

    @Override
    protected void onCreate(@Nullable final Bundle inState) {
        super.onCreate(inState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(provideContentViewResource());
        ButterKnife.bind(this);
        mNavigator = new Navigator(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getParentActivityIntent() == null) {
                onBackPressed();
            } else {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void setAsHomeActivity() {
        setupToolbar(R.drawable.ic_menu);
    }

    protected void setAsInitialFlowActivity() {
        setupToolbar(R.drawable.ic_clear);
    }

    protected void setAsSubActivity() {
        setupToolbar(R.drawable.ic_arrow_back);
    }

    @SuppressWarnings("ConstantConditions")
    private void setupToolbar(int drawableRes) {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setHomeAsUpIndicator(drawableRes);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected Navigator navigate() {
        if (mNavigator == null) {
            mNavigator = new Navigator(this);
        }
        return mNavigator;
    }

    protected EventBus eventBus() {
        return PresentationInjector.provideEventBus();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}