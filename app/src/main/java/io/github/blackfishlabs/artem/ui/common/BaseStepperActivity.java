package io.github.blackfishlabs.artem.ui.common;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.helper.StepperAdapter;

public abstract class BaseStepperActivity extends BaseActivity
        implements StepperLayout.StepperListener {

    private static final String CURRENT_STEP_POSITION_KEY = "position";

    protected StepperAdapter mStepperAdapter;

    @BindView(R.id.stepper_layout)
    protected StepperLayout mStepperLayout;

    protected abstract void provideSteps();

    @Override
    protected void onCreate(@Nullable final Bundle inState) {
        super.onCreate(inState);
        setUpStepper(inState);
    }

    private void setUpStepper(final Bundle inState) {
        int startingStepPosition = inState != null ? inState.getInt(CURRENT_STEP_POSITION_KEY) : 0;

        mStepperLayout.setAdapter(mStepperAdapter = new StepperAdapter(
                getSupportFragmentManager(), this), startingStepPosition);
        mStepperLayout.setListener(this);
        provideSteps();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_STEP_POSITION_KEY, mStepperLayout.getCurrentStepPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        final int currentStepPosition = mStepperLayout.getCurrentStepPosition();
        if (currentStepPosition > 0) {
            mStepperLayout.setCurrentStepPosition(currentStepPosition - 1);
        } else {
            finish();
        }
    }

    @Override
    public void onCompleted(final View completeButton) {

    }

    @Override
    public void onError(final VerificationError verificationError) {

    }

    @Override
    public void onStepSelected(final int newStepPosition) {
        getSupportActionBar().setSubtitle(mStepperAdapter.getViewModel(newStepPosition).getTitle());
    }

    @Override
    public void onReturn() {
        finish();
    }
}

