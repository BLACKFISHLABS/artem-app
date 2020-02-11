package io.github.blackfishlabs.artem.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.data.LocalDataInjector;
import io.github.blackfishlabs.artem.data.realm.AtelierValuePerHourRealmDB;
import io.github.blackfishlabs.artem.domain.json.AtelierValuePerHourJson;
import io.github.blackfishlabs.artem.helper.Constants;
import io.github.blackfishlabs.artem.helper.MoneyTextWatcher;
import io.github.blackfishlabs.artem.helper.NumberUtils;
import io.github.blackfishlabs.artem.helper.PrefUtils;
import io.github.blackfishlabs.artem.ui.common.BaseActivity;

import static java.util.Objects.requireNonNull;

public class EditInfoValuePerHourActivity extends BaseActivity {

    @BindView(R.id.input_layout_work_hour)
    TextInputLayout mInputLayoutWorkHour;
    @BindView(R.id.input_layout_week_days)
    TextInputLayout mInputLayoutWeekDays;
    @BindView(R.id.input_layout_money_focus)
    TextInputLayout mInputLayoutMoneyFocus;
    @BindView(R.id.calculated)
    TextView mCalculated;

    private AtelierValuePerHourJson mCurrentValuePerHourJson;
    private AtelierValuePerHourRealmDB mAtelierValuePerHourRealmDB;

    public static final String TAG = EditInfoValuePerHourActivity.class.getName();

    @Override
    protected int provideContentViewResource() {
        return R.layout.activity_edit_info_valueperhour;
    }

    @Override
    protected void onCreate(@Nullable Bundle inState) {
        super.onCreate(inState);
        setAsSubActivity();

        if (PrefUtils.getAtelierKey(this) == null) {
            Toast.makeText(this, "Cadastre seu Ateliê antes de calcular o valor da sua hora de trabalho!", Toast.LENGTH_LONG).show();
            finish();
        }

        requireNonNull(mInputLayoutMoneyFocus.getEditText()).addTextChangedListener(new MoneyTextWatcher(mInputLayoutMoneyFocus.getEditText()));

        mAtelierValuePerHourRealmDB = LocalDataInjector.getAtelierValuePerHourDB(this);

        if (mAtelierValuePerHourRealmDB.getAll().isEmpty()) {
            mCurrentValuePerHourJson = new AtelierValuePerHourJson()
                    .withId(UUID.randomUUID().toString())
                    .withCompanyId(PrefUtils.getAtelierKey(this))
                    .withWorkHour(NumberUtils.toInt(""))
                    .withWeekDays(NumberUtils.toInt(""))
                    .withMoneyFocus(BigDecimal.ZERO)
                    .withValue(BigDecimal.ZERO);

            populate();
        } else {
            mCurrentValuePerHourJson = LocalDataInjector
                    .getAtelierValuePerHourMapper()
                    .toViewObject(mAtelierValuePerHourRealmDB
                            .findByOwner(PrefUtils.getAtelierKey(this)).first());
            populate();
        }
    }

    private void populate() {
        requireNonNull(mInputLayoutWorkHour.getEditText()).setText(String.valueOf(mCurrentValuePerHourJson.getWorkHour()));
        requireNonNull(mInputLayoutWeekDays.getEditText()).setText(String.valueOf(mCurrentValuePerHourJson.getWeekDays()));
        requireNonNull(mInputLayoutMoneyFocus.getEditText()).setText(NumberUtils.currencyToString(mCurrentValuePerHourJson.getMoneyFocus()));
    }

    @OnTextChanged(R.id.edit_text_work_hour)
    void onEditTextWorkHourChanged(CharSequence text) {
        mCurrentValuePerHourJson.withWorkHour(NumberUtils.toInt(text.toString()));
    }

    @OnTextChanged(R.id.edit_text_week_days)
    void onEditTextWeekDaysChanged(CharSequence text) {
        mCurrentValuePerHourJson.withWeekDays(NumberUtils.toInt(text.toString()));
    }

    @OnTextChanged(R.id.edit_text_money_focus)
    void onEditTextMoneyFocusChanged(CharSequence text) {
        mCurrentValuePerHourJson.withMoneyFocus(NumberUtils.toCurrencyBigDecimal(text.toString()));
        calculate();
    }

    private void setCalculatedValue() {
        String valorString = NumberFormat.getCurrencyInstance(Constants.PT_BR_DEFAULT_LOCALE).format(mCurrentValuePerHourJson.getValue());
        mCalculated.setText(valorString);
    }

    private void calculate() {
        Integer hourPerMonth = (mCurrentValuePerHourJson.getWorkHour() * mCurrentValuePerHourJson.getWeekDays()) * 4;

        if (hourPerMonth > 0) {
            BigDecimal total = mCurrentValuePerHourJson.getMoneyFocus().divide(BigDecimal.valueOf(hourPerMonth), RoundingMode.UP);
            mCurrentValuePerHourJson.withValue(total);
        } else {
            mCurrentValuePerHourJson.withValue(BigDecimal.ZERO);
        }

        setCalculatedValue();
    }

    @OnClick(R.id.save_value)
    void onSaveClick(View view) {
        saveData();
    }

    private void saveData() {
        mAtelierValuePerHourRealmDB.store(Collections.singletonList(LocalDataInjector.getAtelierValuePerHourMapper().toEntity(mCurrentValuePerHourJson)));
        Toast.makeText(getApplicationContext(), getString(R.string.all_saved_message), Toast.LENGTH_SHORT).show();
        finish();
    }
}
