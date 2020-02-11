package io.github.blackfishlabs.artem.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.List;

import io.github.blackfishlabs.artem.domain.json.PaymentMethodJson;
import io.github.blackfishlabs.artem.ui.widget.SingleTextViewAdapter;

import static java.util.Objects.requireNonNull;

public class PaymentMethodsAdapter extends SingleTextViewAdapter<PaymentMethodJson> {

    public PaymentMethodsAdapter(@NonNull final Context context, @NonNull final List<PaymentMethodJson> list) {
        super(context, list);
    }

    @Override
    protected String getText(final int position) {
        if (position >= 0 && position < getCount()) {
            return requireNonNull(getItem(position)).getDescription();
        } else {
            return "";
        }
    }
}