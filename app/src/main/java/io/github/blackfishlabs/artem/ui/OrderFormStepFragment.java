package io.github.blackfishlabs.artem.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.collect.Lists;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.data.LocalDataInjector;
import io.github.blackfishlabs.artem.domain.OrderStatus;
import io.github.blackfishlabs.artem.domain.OrderType;
import io.github.blackfishlabs.artem.domain.entity.PaymentMethodEntity;
import io.github.blackfishlabs.artem.domain.json.AtelierValuePerHourJson;
import io.github.blackfishlabs.artem.domain.json.CustomerJson;
import io.github.blackfishlabs.artem.domain.json.OrderItemJson;
import io.github.blackfishlabs.artem.domain.json.OrderJson;
import io.github.blackfishlabs.artem.domain.json.PaymentMethodJson;
import io.github.blackfishlabs.artem.helper.AndroidUtils;
import io.github.blackfishlabs.artem.helper.DateUtils;
import io.github.blackfishlabs.artem.helper.FormattingUtils;
import io.github.blackfishlabs.artem.helper.NumberUtils;
import io.github.blackfishlabs.artem.helper.PrefUtils;
import io.github.blackfishlabs.artem.helper.StringUtils;
import io.github.blackfishlabs.artem.ui.adapter.PaymentMethodsAdapter;
import io.github.blackfishlabs.artem.ui.common.BaseFragment;
import io.github.blackfishlabs.artem.ui.event.AddedOrderItemsEvent;
import io.github.blackfishlabs.artem.ui.event.SelectedCustomerEvent;
import io.github.blackfishlabs.artem.ui.event.SelectedOrderEvent;
import io.realm.RealmResults;

import static java.util.Objects.requireNonNull;

public class OrderFormStepFragment extends BaseFragment implements BlockingStep {

    @BindView(R.id.input_layout_issue_date)
    TextInputLayout mInputLayoutIssueDate;
    @BindView(R.id.input_layout_customer_name)
    TextInputLayout mInputLayoutCustomerName;
    @BindView(R.id.spinner_payment_method)
    Spinner mSpinnerPaymentMethods;
    @BindView(R.id.input_layout_total_items)
    TextInputLayout mInputLayoutTotalItems;
    @BindView(R.id.input_layout_discount_percentage)
    TextInputLayout mInputLayoutDiscountPercentage;
    @BindView(R.id.input_layout_total_order)
    TextInputLayout mInputLayoutTotalOrder;
    @BindView(R.id.input_layout_observation)
    TextInputLayout mInputLayoutObservation;
    @BindView(R.id.input_layout_total_hour_value)
    TextInputLayout mInputLayoutHourValue;
    @BindView(R.id.input_layout_total_hour_value_qnt)
    TextInputLayout mInputLayoutHourValueQnt;

    private PaymentMethodsAdapter mPaymentMethodsAdapter;
    private OrderJson mCurrentOrder;

    public static OrderFormStepFragment newInstance() {
        return new OrderFormStepFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventBus().register(this);
        loadCurrentOrder();
        loadPaymentMethods();
    }

    private void loadPaymentMethods() {
        RealmResults<PaymentMethodEntity> payments = LocalDataInjector.getPaymentMethodRealmDB(getContext()).findByOwner(PrefUtils.getAtelierKey(getContext()));
        List<PaymentMethodJson> paymentMethods = LocalDataInjector.getPaymentMethodMapper().toViewObjects(payments);

        mSpinnerPaymentMethods.setAdapter(mPaymentMethodsAdapter = new PaymentMethodsAdapter(requireNonNull(getContext()), paymentMethods));

        if (mCurrentOrder != null) {
            int mSelectedPaymentMethod = mPaymentMethodsAdapter.getPosition(mCurrentOrder.getPaymentMethod());
            if (mSelectedPaymentMethod != -1) {
                mSpinnerPaymentMethods.setSelection(mSelectedPaymentMethod + 1, true);
            }
        }
    }

    private void loadCurrentOrder() {
        SelectedOrderEvent event = eventBus().getStickyEvent(SelectedOrderEvent.class);

        if (event != null) {
            mCurrentOrder = event.getOrder().withStatus(OrderStatus.STATUS_MODIFIED);

            requireNonNull(mInputLayoutIssueDate.getEditText()).setText(FormattingUtils.formatAsDateTime(mCurrentOrder.getIssueDate()));
            requireNonNull(mInputLayoutCustomerName.getEditText()).setText(mCurrentOrder.getCustomer().getName());
            requireNonNull(mInputLayoutTotalItems.getEditText()).setText(FormattingUtils.formatAsCurrency(mCurrentOrder.getTotalItems().doubleValue()));
            requireNonNull(mInputLayoutDiscountPercentage.getEditText()).setText(String.valueOf(mCurrentOrder.getDiscountPercentage()));
            requireNonNull(mInputLayoutTotalOrder.getEditText()).setText(FormattingUtils.formatAsCurrency(mCurrentOrder.getTotalOrder().doubleValue()));

            if (!StringUtils.isNullOrEmpty(mCurrentOrder.getObservation())) {
                requireNonNull(mInputLayoutObservation.getEditText()).setText(mCurrentOrder.getObservation());
            }
        } else {
            mCurrentOrder = new OrderJson()
                    .withId(UUID.randomUUID().toString())
                    .withType(OrderType.ORDER_TYPE_NORMAL)
                    .withCompanyId(PrefUtils.getAtelierKey(getContext()))
                    .withStatus(OrderStatus.STATUS_CREATED)
                    .withAmountValueHour(1)
                    .withDiscountPercentage(NumberUtils.toFloat(""))
                    .withDiscount(BigDecimal.ZERO)
                    .withItems(Lists.newArrayList())
                    .withCode("PED-".concat(AndroidUtils.randomAlphaNumeric(4)));
            setIssueDate(DateUtils.getCurrentDateTimeInMillis());
        }

        // Default
        if (hasAtelierValuePerHourInfo()) {
            AtelierValuePerHourJson atelierValuePerHourJson = loadAtelierValuePerHourInfo();
            mCurrentOrder.withValueHour(atelierValuePerHourJson.getValue());
        } else {
            mCurrentOrder.withValueHour(BigDecimal.ZERO);
        }

        requireNonNull(mInputLayoutHourValue.getEditText()).setText(FormattingUtils.formatAsCurrency(mCurrentOrder.getValueHour().doubleValue()));
        requireNonNull(mInputLayoutHourValueQnt.getEditText()).setText(String.valueOf(mCurrentOrder.getAmountValueHour()));
        requireNonNull(mInputLayoutDiscountPercentage.getEditText()).setText(String.valueOf(mCurrentOrder.getDiscountPercentage()));
    }

    private AtelierValuePerHourJson loadAtelierValuePerHourInfo() {
        return LocalDataInjector
                .getAtelierValuePerHourMapper()
                .toViewObject(LocalDataInjector
                        .getAtelierValuePerHourDB(getHostActivity())
                        .findByOwner(PrefUtils.getAtelierKey(getHostActivity())).first());
    }

    private boolean hasAtelierValuePerHourInfo() {
        return !LocalDataInjector.getAtelierValuePerHourDB(getHostActivity()).getAll().isEmpty();
    }

    private void setIssueDate(final long issueDate) {
        mCurrentOrder.withIssueDate(issueDate);
        requireNonNull(mInputLayoutIssueDate.getEditText()).setText(FormattingUtils.formatAsDateTime(issueDate));
    }

    @Override
    protected int provideContentViewResource() {
        return R.layout.fragment_order_form;
    }

    @UiThread
    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {

    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
        LocalDataInjector.getOrderRealmDB(getContext()).store(Collections.singletonList(LocalDataInjector.getOrderMapper().toEntity(mCurrentOrder)));
        Toast.makeText(getContext(), getString(R.string.add_order_saved_successfully), Toast.LENGTH_LONG).show();
        callback.complete();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    @Override
    public VerificationError verifyStep() {
        clearInputErrors();

        if (!checkDiscountValue()) {
            return new VerificationError(getString(R.string.all_correct_fields_error));
        }

        return null;
    }

    private boolean checkDiscountValue() {
        final String discountPercentageStr = mInputLayoutDiscountPercentage.getEditText().getText().toString();

        if (TextUtils.isEmpty(discountPercentageStr)) {
            return true;
        }

        final double discountPercentage = NumberUtils.toDouble(discountPercentageStr);

        if (discountPercentage == 0) {
            return true;
        }

        final int selectedPaymentMethodPosition = mSpinnerPaymentMethods.getSelectedItemPosition();

        if (selectedPaymentMethodPosition == AdapterView.INVALID_POSITION) {
            return true;
        }

        return true;
    }

    private void clearInputErrors() {
        mInputLayoutDiscountPercentage.setError(null);
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {
        Snackbar.make(requireNonNull(getView()), error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
    }

    @Subscribe(priority = 2)
    public void onSelectedCustomer(SelectedCustomerEvent event) {
        setCustomer(event.getCustomer());
    }

    @Subscribe
    public void onAddedOrderItems(AddedOrderItemsEvent event) {
        setOrderItems(event.getOrderItems());
        calculateDiscount();
    }

    private void setCustomer(final CustomerJson customer) {
        mCurrentOrder.withCustomer(customer);
        requireNonNull(mInputLayoutCustomerName.getEditText()).setText(customer.getName());
    }

    private void setOrderItems(final List<OrderItemJson> orderItems) {
        mCurrentOrder.withItems(orderItems);
        requireNonNull(mInputLayoutTotalItems.getEditText()).setText(FormattingUtils.formatAsCurrency(mCurrentOrder.getTotalItems().doubleValue()));
    }

    private void calculateDiscount() {
        final float discountPercentage = mCurrentOrder.getDiscountPercentage();
        if (discountPercentage > 0.0) {
            final BigDecimal discount = (mCurrentOrder.getTotalItems().multiply(BigDecimal.valueOf(discountPercentage)).divide(BigDecimal.valueOf(100), RoundingMode.UP));
            mCurrentOrder.withDiscount(discount);
        }

        requireNonNull(mInputLayoutTotalOrder.getEditText()).setText(FormattingUtils.formatAsCurrency(mCurrentOrder.getTotalOrder().doubleValue()));
    }

    @OnTextChanged(R.id.edit_text_discount_percentage)
    void onEditTextDiscountChanged(CharSequence text) {
        setDiscountPercentage(text.toString());
        calculateDiscount();
    }

    @OnTextChanged(R.id.edit_text_total_hour_value_qnt)
    void onEditTextHourQntChanged(CharSequence text) {
        mCurrentOrder.withAmountValueHour(NumberUtils.toInt(text.toString()));
        calculateDiscount();
    }

    private void setDiscountPercentage(final String discountStr) {
        mCurrentOrder.withDiscountPercentage(NumberUtils.toFloat(discountStr));
    }

    @OnItemSelected(R.id.spinner_payment_method)
    void onSpinnerPaymentMethodItemSelected(int position) {
        if (position == AdapterView.INVALID_POSITION) {
            setPaymentMethod(null);
        } else {
            final PaymentMethodJson paymentMethod = mPaymentMethodsAdapter.getItem(position);
            if (paymentMethod != null) {
                setPaymentMethod(paymentMethod);
            }
        }
    }

    private void setPaymentMethod(final PaymentMethodJson paymentMethod) {
        mCurrentOrder.withPaymentMethod(paymentMethod);
    }

    @OnTextChanged(R.id.edit_text_observation)
    void onEditTextObservationChanged(CharSequence text) {
        mCurrentOrder.withObservation(text.toString());
    }

    @Override
    public void onDestroyView() {
        eventBus().removeStickyEvent(SelectedOrderEvent.class);
        eventBus().unregister(this);
        super.onDestroyView();
    }
}
