package io.github.blackfishlabs.artem.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.stepstone.stepper.VerificationError;

import org.greenrobot.eventbus.Subscribe;

import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.helper.PrefUtils;
import io.github.blackfishlabs.artem.ui.common.BaseStepperActivity;
import io.github.blackfishlabs.artem.ui.event.SelectedCustomerEvent;
import io.github.blackfishlabs.artem.ui.event.SelectedOrderEvent;

import static java.util.Objects.requireNonNull;

public class AddOrderActivity extends BaseStepperActivity {

    @Override
    protected void provideSteps() {
        mStepperAdapter.addStep(SelectCustomerStepFragment.newInstance(),
                getString(R.string.add_order_select_customer_step));
        mStepperAdapter.addStep(SelectOrderItemsStepFragment.newInstance(),
                getString(R.string.add_order_select_items_step));
        mStepperAdapter.addStep(OrderFormStepFragment.newInstance(),
                getString(R.string.add_order_form_step));

        mStepperLayout.setOffscreenPageLimit(mStepperAdapter.getCount());
    }

    @Override
    protected int provideContentViewResource() {
        return R.layout.form_add_order_stepper_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle inState) {
        super.onCreate(inState);
        setAsInitialFlowActivity();

        if (PrefUtils.getAtelierKey(this) == null) {
            Toast.makeText(this, "Cadastre seu Ateliê antes de gerenciar seus pedidos!", Toast.LENGTH_LONG).show();
            finish();
        }

        SelectedOrderEvent event = eventBus().getStickyEvent(SelectedOrderEvent.class);
        if (event != null) {
            requireNonNull(getSupportActionBar()).setTitle(R.string.add_order_editing_title);
            mStepperLayout.setCurrentStepPosition(mStepperAdapter.getCount() - 1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        eventBus().unregister(this);
    }

    @Override
    public void onCompleted(View completeButton) {
        finish();
    }

    @Override
    public void onError(VerificationError verificationError) {

    }

    @Subscribe
    public void onSelectedCustomer(SelectedCustomerEvent event) {
        mStepperLayout.proceed();
    }
}
