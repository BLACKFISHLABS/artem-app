package io.github.blackfishlabs.artem.ui;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.data.LocalDataInjector;
import io.github.blackfishlabs.artem.domain.entity.AtelierFixedExpenseEntity;
import io.github.blackfishlabs.artem.domain.entity.AtelierValuePerHourEntity;
import io.github.blackfishlabs.artem.domain.json.MaterialJson;
import io.github.blackfishlabs.artem.domain.json.ProductJson;
import io.github.blackfishlabs.artem.helper.Constants;
import io.github.blackfishlabs.artem.helper.MoneyTextWatcher;
import io.github.blackfishlabs.artem.helper.NumberUtils;
import io.github.blackfishlabs.artem.helper.PrefUtils;
import io.github.blackfishlabs.artem.ui.common.BaseFragment;
import io.github.blackfishlabs.artem.ui.event.AddedMaterialItemsEvent;
import io.github.blackfishlabs.artem.ui.event.SelectedProductEvent;
import io.realm.RealmResults;

import static java.util.Objects.requireNonNull;

public class FinishProductStepFragment extends BaseFragment implements BlockingStep {

    @BindView(R.id.price)
    TextView price;

    private ProductJson mCurrentProduct;

    public static FinishProductStepFragment newInstance() {
        return new FinishProductStepFragment();
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {

    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
        LocalDataInjector.getProductRealmDB(getContext()).store(
                Collections.singletonList(
                        LocalDataInjector.getProductMapper().toEntity(mCurrentProduct)));
        Toast.makeText(getContext(), getString(R.string.add_product_saved_successfully), Toast.LENGTH_LONG).show();
        callback.complete();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    @Override
    public VerificationError verifyStep() {
        return null;
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
        return R.layout.fragment_finish_add_product;
    }

    @Override
    public void onStart() {
        super.onStart();
        eventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        eventBus().unregister(this);
    }

    @Subscribe
    public void onSelectedProductEvent(SelectedProductEvent event) {
        mCurrentProduct = event.getProduct();
    }

    @Subscribe
    public void onAddedItems(AddedMaterialItemsEvent event) {
        setItems(event.getItems());
    }

    private void setItems(List<MaterialJson> items) {
        mCurrentProduct.withItems(items);
        mCurrentProduct.withValue(calculate());
        mCurrentProduct.withMarkup(NumberUtils.toFloat(String.valueOf(mCurrentProduct.getMarkup())));

        update();
    }

    private void update() {
        price.setText(NumberFormat.getCurrencyInstance(
                Constants.PT_BR_DEFAULT_LOCALE).format(mCurrentProduct.getValue()));
    }

    private BigDecimal calculate() {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal sumExpenses = BigDecimal.ZERO;
        BigDecimal resumeExpensesPerDay = BigDecimal.ZERO;

        for (MaterialJson item : mCurrentProduct.getItems()) {
            total = total.add(item.getValue());
        }

        RealmResults<AtelierFixedExpenseEntity> fixedExpenses = LocalDataInjector
                .getAtelierFixedExpenseRealmDB(getContext())
                .findByOwner(PrefUtils.getAtelierKey(getContext()));
        RealmResults<AtelierValuePerHourEntity> workDays = LocalDataInjector
                .getAtelierValuePerHourDB(getContext())
                .findByOwner(PrefUtils.getAtelierKey(getContext()));
        if (!workDays.isEmpty() && !fixedExpenses.isEmpty()) {
            for (AtelierFixedExpenseEntity fixed : fixedExpenses) {
                sumExpenses = sumExpenses.add(BigDecimal.valueOf(fixed.getValue()));
            }

            AtelierValuePerHourEntity first = workDays.first();
            resumeExpensesPerDay = (sumExpenses.divide(BigDecimal.valueOf(requireNonNull(first).getWeekDays() * first.getWorkHour()), RoundingMode.UP));
        }

        return total.add(resumeExpensesPerDay).setScale(0, BigDecimal.ROUND_UP);
    }

    @OnClick(R.id.edit_price)
    void onClickAddMaterial() {
        showDialog();
    }

    private void showDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
        View view = layoutInflaterAndroid.inflate(R.layout.price_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getContext());
        alertDialogBuilderUserInput.setView(view);

        final TextInputLayout inputValue = view.findViewById(R.id.input_layout_dialog_value);
        final TextInputEditText inputEditValue = view.findViewById(R.id.edit_text_dialog_value);
        final TableRow valueCalculated = view.findViewById(R.id.calculated_value);
        valueCalculated.setVisibility(View.VISIBLE);
        requireNonNull(inputValue.getEditText()).addTextChangedListener(new MoneyTextWatcher(inputValue.getEditText()));

        final TextInputLayout inputValueMarkup = view.findViewById(R.id.input_layout_dialog_markup);
        final TextInputEditText inputEditValueMarkup = view.findViewById(R.id.edit_text_dialog_markup);

        final TextInputLayout inputValueChange = view.findViewById(R.id.input_layout_dialog_value_change);
        final TableRow manualValue = view.findViewById(R.id.manual_value);
        manualValue.setVisibility(View.GONE);
        requireNonNull(inputValueChange.getEditText()).addTextChangedListener(new MoneyTextWatcher(inputValueChange.getEditText()));

        inputEditValue.setOnLongClickListener(v -> {
            manualValue.setVisibility(View.VISIBLE);
            valueCalculated.setVisibility(View.GONE);

            return true;
        });

        requireNonNull(inputValue.getEditText()).setText(NumberUtils.currencyToString(mCurrentProduct.getValue()));
        requireNonNull(inputValueChange.getEditText()).setText(NumberUtils.currencyToString(mCurrentProduct.getValue()));

        requireNonNull(inputValueMarkup.getEditText()).setText(String.valueOf(mCurrentProduct.getMarkup()));

        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(getString(R.string.lbl_edit_price_product_title));

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("atualizar", (dialogBox, id) -> {
                })
                .setNegativeButton("cancelar", (dialogBox, id) -> dialogBox.cancel());

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (NumberUtils.toCurrencyBigDecimal(inputValueChange.getEditText().getText().toString()).signum() <= 0) {
                Toast.makeText(getContext(), "Digite um valor!", Toast.LENGTH_SHORT).show();
                return;
            }

            double percentage = NumberUtils.toDouble(requireNonNull(inputEditValueMarkup.getText()).toString());
            if (percentage > 0 && valueCalculated.getVisibility() == View.VISIBLE) {
                BigDecimal total = mCurrentProduct.getValue()
                        .add(
                                (mCurrentProduct.getValue().multiply(BigDecimal.valueOf(percentage)))
                                        .divide(BigDecimal.valueOf(100), RoundingMode.UP));

                requireNonNull(inputValueChange.getEditText()).setText(NumberUtils.currencyToString(total));
                requireNonNull(inputValue.getEditText()).setText(NumberUtils.currencyToString(total));
            }

            mCurrentProduct.withValue(NumberUtils
                    .toCurrencyBigDecimal(inputValueChange.getEditText().getText().toString()));
            mCurrentProduct.withMarkup(NumberUtils
                    .toFloat(inputValueMarkup.getEditText().getText().toString()));

            update();
            alertDialog.dismiss();
        });
    }
}
