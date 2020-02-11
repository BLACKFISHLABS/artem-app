package io.github.blackfishlabs.artem.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.UUID;

import butterknife.BindView;
import butterknife.OnTextChanged;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.domain.json.ProductJson;
import io.github.blackfishlabs.artem.helper.PrefUtils;
import io.github.blackfishlabs.artem.ui.common.BaseFragment;
import io.github.blackfishlabs.artem.ui.event.SelectedProductEvent;

import static io.github.blackfishlabs.artem.helper.StringUtils.isNullOrEmpty;
import static java.util.Objects.requireNonNull;

public class ProductFormStepFragment extends BaseFragment implements BlockingStep {

    @BindView(R.id.input_layout_product_name)
    TextInputLayout mInputLayoutProductName;

    private ProductJson mCurrentProduct;

    public static ProductFormStepFragment newInstance() {
        return new ProductFormStepFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadCurrentProduct();
    }

    private void loadCurrentProduct() {
        SelectedProductEvent event = eventBus().getStickyEvent(SelectedProductEvent.class);
        if (event != null) {
            mCurrentProduct = event.getProduct();
            requireNonNull(mInputLayoutProductName.getEditText()).setText(mCurrentProduct.getDescription());
        } else {
            mCurrentProduct = new ProductJson();
            mCurrentProduct.withId(UUID.randomUUID().toString());
            mCurrentProduct.withCompanyId(PrefUtils.getAtelierKey(getContext()));
        }
    }

    @OnTextChanged(R.id.edit_text_product_name)
    void onEditTextDescriptionChanged(CharSequence text) {
        mCurrentProduct.withDescription(text.toString());
    }

    @Override
    public VerificationError verifyStep() {
        clearInputErrors();

        if (checkEmptyRequiredInput(mInputLayoutProductName))
            return new VerificationError(getString(R.string.all_correct_required_fields_error));

        return null;
    }

    private void clearInputErrors() {
        mInputLayoutProductName.setError(null);
    }

    private boolean checkEmptyRequiredInput(TextInputLayout inputLayout) {
        if (isNullOrEmpty(requireNonNull(inputLayout.getEditText()).getText())) {
            inputLayout.setError(getString(R.string.all_required_field_error));
            return true;
        }
        return false;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {
        Snackbar.make(requireNonNull(getView()), error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected int provideContentViewResource() {
        return R.layout.fragment_product_form;
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        eventBus().post(SelectedProductEvent.selectProduct(mCurrentProduct));
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }
}
