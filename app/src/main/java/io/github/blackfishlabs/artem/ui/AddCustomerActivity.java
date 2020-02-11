package io.github.blackfishlabs.artem.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Collections;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnTextChanged;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.data.LocalDataInjector;
import io.github.blackfishlabs.artem.data.RemoteDataInjector;
import io.github.blackfishlabs.artem.domain.json.CustomerJson;
import io.github.blackfishlabs.artem.domain.json.PostalCode;
import io.github.blackfishlabs.artem.helper.InputMask;
import io.github.blackfishlabs.artem.helper.PrefUtils;
import io.github.blackfishlabs.artem.helper.ValidationUtils;
import io.github.blackfishlabs.artem.ui.common.BaseActivity;
import io.github.blackfishlabs.artem.ui.event.SelectedCustomerEvent;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static io.github.blackfishlabs.artem.helper.StringUtils.isNullOrEmpty;
import static java.util.Objects.requireNonNull;

public class AddCustomerActivity extends BaseActivity {

    @BindView(R.id.input_layout_customer_name)
    TextInputLayout mInputLayoutSocialName;
    @BindView(R.id.input_layout_email)
    TextInputLayout mInputLayoutEmail;
    @BindView(R.id.input_layout_address)
    TextInputLayout mInputLayoutAddress;
    @BindView(R.id.input_layout_address_number)
    TextInputLayout mInputLayoutAddressNumber;
    @BindView(R.id.input_layout_district)
    TextInputLayout mInputLayoutDistrict;
    @BindView(R.id.input_layout_city)
    TextInputLayout mInputLayoutCity;
    @BindView(R.id.input_layout_postal_code)
    TextInputLayout mInputLayoutPostalCode;
    @BindView(R.id.input_layout_address_complement)
    TextInputLayout mInputLayoutAddressComplement;
    @BindView(R.id.input_layout_main_phone)
    TextInputLayout mInputLayoutMainPhone;

    @BindView(R.id.edit_text_postal_code)
    TextInputEditText mInputEditTextPostalCode;
    @BindView(R.id.edit_text_main_phone)
    TextInputEditText mInputEditTextPhone;

    private CompositeDisposable disposable = new CompositeDisposable();
    private MaterialDialog mProgressDialog;
    private CustomerJson mCurrentCustomer;

    @Override
    protected int provideContentViewResource() {
        return R.layout.activity_add_customer;
    }

    @Override
    protected void onCreate(@Nullable Bundle inState) {
        super.onCreate(inState);
        setAsInitialFlowActivity();
        setUpViews();
        loadCurrentCustomer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus().removeStickyEvent(SelectedCustomerEvent.class);
        disposable.dispose();
    }

    private void setUpViews() {
        mInputEditTextPostalCode.addTextChangedListener(InputMask.insert("##.###-###", mInputEditTextPostalCode));
        mInputEditTextPostalCode.setOnFocusChangeListener((view1, b) -> {
            if (!b) {
                subscribeToSearchPostalCode(InputMask.unmask(mInputEditTextPostalCode.getText().toString()));
            }
        });

        mInputEditTextPhone.addTextChangedListener(InputMask.insert("(##)#.####-####", mInputEditTextPhone));
    }

    private void loadCurrentCustomer() {
        if (PrefUtils.getAtelierKey(this) == null) {
            Toast.makeText(this, "Cadastre seu Ateliê antes de gerenciar seus clientes!", Toast.LENGTH_LONG).show();
            finish();
        }

        SelectedCustomerEvent event = eventBus().getStickyEvent(SelectedCustomerEvent.class);
        if (event != null) {
            mCurrentCustomer = event.getCustomer();
            requireNonNull(getSupportActionBar()).setTitle(R.string.add_customer_editing_title);

            requireNonNull(mInputLayoutSocialName.getEditText()).setText(mCurrentCustomer.getName());
            requireNonNull(mInputLayoutCity.getEditText()).setText(mCurrentCustomer.getCity());

            if (!isNullOrEmpty(mCurrentCustomer.getEmail())) {
                requireNonNull(mInputLayoutEmail.getEditText()).setText(mCurrentCustomer.getEmail());
            }

            if (!isNullOrEmpty(mCurrentCustomer.getAddress())) {
                requireNonNull(mInputLayoutAddress.getEditText()).setText(mCurrentCustomer.getAddress());
            }

            if (!isNullOrEmpty(mCurrentCustomer.getNumber())) {
                requireNonNull(mInputLayoutAddressNumber.getEditText()).setText(mCurrentCustomer.getNumber());
            }

            if (!isNullOrEmpty(mCurrentCustomer.getNeighborhood())) {
                requireNonNull(mInputLayoutDistrict.getEditText()).setText(mCurrentCustomer.getNeighborhood());
            }

            if (!isNullOrEmpty(mCurrentCustomer.getPostalCode())) {
                requireNonNull(mInputLayoutPostalCode.getEditText()).setText(mCurrentCustomer.getPostalCode());
            }

            if (!isNullOrEmpty(mCurrentCustomer.getComplement())) {
                requireNonNull(mInputLayoutAddressComplement.getEditText()).setText(mCurrentCustomer.getComplement());
            }

            if (!isNullOrEmpty(mCurrentCustomer.getMainPhone())) {
                requireNonNull(mInputLayoutMainPhone.getEditText()).setText(mCurrentCustomer.getMainPhone());
            }
        } else {
            mCurrentCustomer = new CustomerJson();
            mCurrentCustomer.withId(UUID.randomUUID().toString());
            requireNonNull(getSupportActionBar()).setTitle(R.string.add_customer_new_title);
        }

        mCurrentCustomer.withCompanyId(PrefUtils.getAtelierKey(this));
    }

    private void subscribeToSearchPostalCode(String value) {
        if (isNullOrEmpty(value))
            return;
        disposable.add(
                RemoteDataInjector.providePostalCodeApi().get(value)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(createPostalCodeSubscriber())
        );

    }

    private DisposableSingleObserver<PostalCode> createPostalCodeSubscriber() {
        return new DisposableSingleObserver<PostalCode>() {
            @Override
            protected void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @Override
            public void onSuccess(PostalCode postalCode) {
                showPostalCodeInfo(postalCode);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Could not search postal code");
            }
        };
    }

    private void showProgressDialog() {
        mProgressDialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .title(R.string.add_customer_searching_postal_code_message)
                .content(R.string.all_please_wait)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .show();
    }

    private void showPostalCodeInfo(PostalCode postalCode) {
        hideProgressDialog();

        requireNonNull(mInputLayoutDistrict.getEditText()).setText(postalCode.district);
        requireNonNull(mInputLayoutAddress.getEditText()).setText(postalCode.address);
        requireNonNull(mInputLayoutCity.getEditText()).setText(postalCode.cityName);
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_all_done: {
                saveCustomer();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveCustomer() {
        clearInputErrors();

        final boolean emptyRequiredInputs = checkEmptyRequiredInputs();
        final boolean validInputs = checkValidInputs();

        if (!emptyRequiredInputs && validInputs) {
            saveData();
        }
    }

    private void saveData() {
        LocalDataInjector.getCustomerRealmDB(this).store(
                Collections.singletonList(
                        LocalDataInjector.getCustomerMapper().toEntity(mCurrentCustomer)));
        Toast.makeText(this, getString(R.string.add_customer_saved_successfully), Toast.LENGTH_LONG).show();
        finish();
    }

    private boolean checkValidInputs() {
        final boolean validEmail = checkValidEmailInput();
        final boolean validPostalCode = checkValidPostalCodeInput();
        final boolean validMainPhone = checkValidMainPhoneInput();

        return validEmail && validPostalCode && validMainPhone;
    }

    private boolean checkEmptyRequiredInputs() {
        final boolean emptySocialName = checkEmptyRequiredInput(mInputLayoutSocialName);
        final boolean emptyCity = checkEmptyRequiredInput(mInputLayoutCity);
        final boolean emptyMainPhone = checkEmptyRequiredInput(mInputLayoutMainPhone);

        return emptySocialName || emptyCity || emptyMainPhone;
    }

    private boolean checkEmptyRequiredInput(TextInputLayout inputLayout) {
        if (isNullOrEmpty(inputLayout.getEditText().getText())) {
            inputLayout.setError(getString(R.string.all_required_field_error));
            return true;
        }
        return false;
    }

    private boolean checkValidEmailInput() {
        if (!isNullOrEmpty(mCurrentCustomer.getEmail())) {
            if (!ValidationUtils.isValidEmail(mCurrentCustomer.getEmail())) {
                mInputLayoutEmail.setError(getString(R.string.add_customer_invalid_email));
                return false;
            }
        }
        return true;
    }

    private boolean checkValidPostalCodeInput() {
        if (!isNullOrEmpty(mCurrentCustomer.getPostalCode())) {
            if (!ValidationUtils.isValidPostalCode(InputMask.unmask(mCurrentCustomer.getPostalCode()))) {
                mInputLayoutPostalCode.setError(getString(R.string.add_customer_invalid_postal_code));
                return false;
            }
        }
        return true;
    }

    private boolean checkValidMainPhoneInput() {
        if (!isNullOrEmpty(mCurrentCustomer.getMainPhone())) {
            if (!ValidationUtils.isValidPhoneNumber(mCurrentCustomer.getMainPhone())) {
                mInputLayoutMainPhone.setError(getString(R.string.add_customer_invalid_phone_number));
                return false;
            }
        }
        return true;
    }

    private void clearInputErrors() {
        mInputLayoutSocialName.setError(null);
        mInputLayoutEmail.setError(null);
        mInputLayoutCity.setError(null);
        mInputLayoutPostalCode.setError(null);
        mInputLayoutMainPhone.setError(null);
    }

    @OnTextChanged(R.id.edit_text_customer_name)
    void onEditTextCustomerNameChanged(CharSequence text) {
        mCurrentCustomer.withName(text.toString());
    }

    @OnTextChanged(R.id.edit_text_email)
    void onEditTextCustomerEmailChanged(CharSequence text) {
        mCurrentCustomer.withEmail(text.toString());
    }

    @OnTextChanged(R.id.edit_text_postal_code)
    void onEditTextCustomerPostalCodeChanged(CharSequence text) {
        mCurrentCustomer.withPostalCode(text.toString());
    }

    @OnTextChanged(R.id.edit_text_address)
    void onEditTextCustomerAddressChanged(CharSequence text) {
        mCurrentCustomer.withAddress(text.toString());
    }

    @OnTextChanged(R.id.edit_text_district)
    void onEditTextCustomerNeighborhoodChanged(CharSequence text) {
        mCurrentCustomer.withNeighborhood(text.toString());
    }

    @OnTextChanged(R.id.edit_text_address_number)
    void onEditTextCustomerAddressNumberChanged(CharSequence text) {
        mCurrentCustomer.withNumber(text.toString());
    }

    @OnTextChanged(R.id.edit_text_address_complement)
    void onEditTextCustomerAddressComplementChanged(CharSequence text) {
        mCurrentCustomer.withComplement(text.toString());
    }

    @OnTextChanged(R.id.edit_text_city)
    void onEditTextCustomerCityChanged(CharSequence text) {
        mCurrentCustomer.withCity(text.toString());
    }

    @OnTextChanged(R.id.edit_text_main_phone)
    void onEditTextCustomerMainPhoneChanged(CharSequence text) {
        mCurrentCustomer.withMainPhone(text.toString());
    }

}
