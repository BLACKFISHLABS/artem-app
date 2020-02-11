package io.github.blackfishlabs.artem.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.data.LocalDataInjector;
import io.github.blackfishlabs.artem.data.realm.AtelierRealmDB;
import io.github.blackfishlabs.artem.domain.entity.PaymentMethodEntity;
import io.github.blackfishlabs.artem.domain.json.AtelierJson;
import io.github.blackfishlabs.artem.helper.InputMask;
import io.github.blackfishlabs.artem.helper.PrefUtils;
import io.github.blackfishlabs.artem.helper.ValidationUtils;
import io.github.blackfishlabs.artem.ui.common.BaseActivity;
import io.realm.RealmResults;

import static java.util.Objects.requireNonNull;

public class EditInfoAtelierActivity extends BaseActivity {

    @BindView(R.id.input_layout_atelier_name)
    TextInputLayout mInputLayoutAtelierName;
    @BindView(R.id.input_layout_main_phone)
    TextInputLayout mInputLayoutMainPhone;
    @BindView(R.id.edit_text_main_phone)
    TextInputEditText mPhoneMain;
    @BindView(R.id.edit_text_atelier_name)
    TextInputEditText mAtelierName;

    private AtelierJson mCurrentAtelier;
    private AtelierRealmDB mAtelierRealmDB;

    public static final String TAG = EditInfoAtelierActivity.class.getName();

    @Override
    protected int provideContentViewResource() {
        return R.layout.activity_edit_info_atelier;
    }

    @Override
    protected void onCreate(@Nullable Bundle inState) {
        super.onCreate(inState);
        setAsSubActivity();

        mAtelierRealmDB = LocalDataInjector.getAtelierRealmDB(this);

        mPhoneMain.addTextChangedListener(InputMask.insert("(##)#.####-####", mPhoneMain));

        if (PrefUtils.getAtelierKey(this) != null) {
            mCurrentAtelier = LocalDataInjector
                    .getAtelierMapper()
                    .toViewObject(mAtelierRealmDB
                            .findById(PrefUtils.getAtelierKey(this)).first());

            mAtelierName.setText(mCurrentAtelier.getName());
            mPhoneMain.setText(mCurrentAtelier.getPhoneNumber());
        } else {
            mCurrentAtelier = new AtelierJson();
            mCurrentAtelier.withId(UUID.randomUUID().toString());
            mCurrentAtelier.withFirebaseId(requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        }
    }

    @OnTextChanged(R.id.edit_text_atelier_name)
    void onEditTextAtelierNameChanged(CharSequence text) {
        mCurrentAtelier.withName(text.toString());
    }

    @OnTextChanged(R.id.edit_text_main_phone)
    void onEditTextMainPhoneChanged(CharSequence text) {
        mCurrentAtelier.withPhoneNumber(text.toString());
    }

    @OnClick(R.id.save_atelier)
    void onSaveClick(View view) {
        clearInputErrors();

        if (checkEmptyRequiredInputs()) {
            Toast.makeText(this, getString(R.string.all_correct_required_fields_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!checkValidInputs()) {
            Toast.makeText(this, getString(R.string.all_correct_fields_error), Toast.LENGTH_SHORT).show();
            return;
        }

        saveData();
    }

    private void saveData() {
        mAtelierRealmDB.store(Collections.singletonList(LocalDataInjector.getAtelierMapper().toEntity(mCurrentAtelier)));
        PrefUtils.storeAtelierKey(this, mCurrentAtelier.getId());

        RealmResults<PaymentMethodEntity> payments = LocalDataInjector.getPaymentMethodRealmDB(this).findByOwner(PrefUtils.getAtelierKey(this));
        if (payments.isEmpty()) {
            LocalDataInjector.getPaymentMethodRealmDB(this).store(
                    Lists.newArrayList(
                            new PaymentMethodEntity()
                                    .withCompanyId(mCurrentAtelier.getId())
                                    .withDescription("A vista")
                                    .withCode("AV")
                                    .withPaymentMethodId(UUID.randomUUID().toString()),
                            new PaymentMethodEntity()
                                    .withCompanyId(mCurrentAtelier.getId())
                                    .withDescription("A prazo")
                                    .withCode("PZ")
                                    .withPaymentMethodId(UUID.randomUUID().toString())));
        }

        Toast.makeText(getApplicationContext(), getString(R.string.all_saved_message), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void clearInputErrors() {
        mInputLayoutAtelierName.setError(null);
        mInputLayoutMainPhone.setError(null);
    }

    private boolean checkEmptyRequiredInputs() {
        final boolean emptyAtelierName = checkEmptyRequiredInput(mInputLayoutAtelierName);
        final boolean emptyMainPhone = checkEmptyRequiredInput(mInputLayoutMainPhone);

        return emptyAtelierName || emptyMainPhone;
    }

    private boolean checkValidInputs() {
        return checkValidMainPhoneInput();
    }

    private boolean checkEmptyRequiredInput(TextInputLayout inputLayout) {
        if (TextUtils.isEmpty(requireNonNull(inputLayout.getEditText()).getText())) {
            inputLayout.setError(getString(R.string.all_required_field_error));
            return true;
        }
        return false;
    }

    private boolean checkValidMainPhoneInput() {
        if (!TextUtils.isEmpty(mCurrentAtelier.getPhoneNumber())) {
            if (!ValidationUtils.isValidPhoneNumber(mCurrentAtelier.getPhoneNumber())) {
                mInputLayoutMainPhone.setError(getString(R.string.add_customer_invalid_phone_number));
                return false;
            }
        } else {
            mInputLayoutMainPhone.setError(getString(R.string.all_required_field_error));
            return false;
        }
        return true;
    }
}
