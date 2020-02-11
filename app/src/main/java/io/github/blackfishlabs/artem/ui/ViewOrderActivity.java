package io.github.blackfishlabs.artem.ui;

import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.domain.OrderStatus;
import io.github.blackfishlabs.artem.domain.json.OrderJson;
import io.github.blackfishlabs.artem.helper.FormattingUtils;
import io.github.blackfishlabs.artem.ui.common.BaseActivity;
import io.github.blackfishlabs.artem.ui.event.SelectedOrderEvent;

import static android.text.TextUtils.isEmpty;
import static java.util.Objects.requireNonNull;

public class ViewOrderActivity extends BaseActivity {

    @BindView(R.id.edit_text_issue_date)
    EditText editTextIssueDate;
    @BindView(R.id.edit_text_customer_name)
    EditText editTextCustomerName;
    @BindView(R.id.edit_text_total_items)
    EditText editTextTotalItems;
    @BindView(R.id.input_layout_payment_method)
    TextInputLayout inputLayoutPaymentMethod;
    @BindView(R.id.edit_text_discount_percentage)
    EditText editTextDiscountPercentage;
    @BindView(R.id.edit_text_total_order)
    EditText editTextTotalOrder;
    @BindView(R.id.edit_text_observation)
    EditText editTextObservation;
    @BindView(R.id.edit_text_total_hour_value)
    EditText editTextTotalHour;
    @BindView(R.id.edit_text_total_hour_value_qnt)
    EditText editTextTotalHourQnt;

    @Override
    protected int provideContentViewResource() {
        return R.layout.activity_view_order;
    }

    @Override
    protected void onCreate(@Nullable final Bundle inState) {
        super.onCreate(inState);
        setAsSubActivity();
        loadCurrentOrder();
    }

    private void loadCurrentOrder() {
        SelectedOrderEvent event = eventBus().getStickyEvent(SelectedOrderEvent.class);
        if (event != null) {
            OrderJson order = event.getOrder();

            requireNonNull(getSupportActionBar()).setTitle(getString(R.string.title_activity_view_order, order.getCode()));

            switch (order.getStatus()) {
                case OrderStatus.STATUS_CANCELLED: {
                    getSupportActionBar().setSubtitle(R.string.order_status_cancelled);
                    break;
                }
                case OrderStatus.STATUS_INVOICED: {
                    getSupportActionBar().setSubtitle(R.string.order_status_invoiced);
                    break;
                }
            }

            editTextIssueDate.setText(FormattingUtils.formatAsDateTime(order.getIssueDate()));
            editTextCustomerName.setText(order.getCustomer().getName());
            editTextTotalItems.setText(FormattingUtils.formatAsCurrency(order.getTotalItems().doubleValue()));
            editTextTotalHour.setText(FormattingUtils.formatAsCurrency(order.getValueHour().doubleValue()));
            editTextTotalHourQnt.setText(String.valueOf(order.getAmountValueHour()));
            requireNonNull(inputLayoutPaymentMethod.getEditText()).setText(order.getPaymentMethod().getDescription());

            editTextDiscountPercentage.setText(FormattingUtils.formatAsPercent(order.getDiscountPercentage()));
            editTextTotalOrder.setText(FormattingUtils.formatAsCurrency(order.getTotalOrder().doubleValue()));

            if (!isEmpty(order.getObservation())) {
                editTextObservation.setText(order.getObservation());
            }

            eventBus().removeStickyEvent(SelectedOrderEvent.class);
        }
    }
}

