package io.github.blackfishlabs.artem.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.stepstone.stepper.VerificationError;

import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.helper.PrefUtils;
import io.github.blackfishlabs.artem.ui.common.BaseStepperActivity;
import io.github.blackfishlabs.artem.ui.event.SelectedProductEvent;

import static java.util.Objects.requireNonNull;

public class AddProductCostActivity extends BaseStepperActivity {

    @Override
    protected void provideSteps() {
        mStepperAdapter.addStep(ProductFormStepFragment.newInstance(), getString(R.string.add_product_form_step));
        mStepperAdapter.addStep(SelectProductItemsStepFragment.newInstance(), getString(R.string.add_product_select_items_step));
        mStepperAdapter.addStep(FinishProductStepFragment.newInstance(), getString(R.string.finish_product_step));

        mStepperLayout.setOffscreenPageLimit(mStepperAdapter.getCount());
    }

    @Override
    protected int provideContentViewResource() {
        return R.layout.form_product_cost_stepper_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle inState) {
        super.onCreate(inState);
        setAsInitialFlowActivity();

        if (PrefUtils.getAtelierKey(this) == null) {
            Toast.makeText(this, "Cadastre seu Ateliê antes de gerenciar seus produtos!", Toast.LENGTH_LONG).show();
            finish();
        }

        SelectedProductEvent event = eventBus().getStickyEvent(SelectedProductEvent.class);
        if (event != null) {
            requireNonNull(getSupportActionBar()).setTitle(R.string.add_product_editing_title);
        }
    }

    @Override
    public void onCompleted(final View completeButton) {
        finish();
    }

    @Override
    public void onError(final VerificationError verificationError) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus().removeStickyEvent(SelectedProductEvent.class);
    }
}
