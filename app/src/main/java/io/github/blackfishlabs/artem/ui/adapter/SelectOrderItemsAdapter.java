package io.github.blackfishlabs.artem.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.domain.json.OrderItemJson;
import io.github.blackfishlabs.artem.domain.json.ProductJson;
import io.github.blackfishlabs.artem.helper.FormattingUtils;
import io.github.blackfishlabs.artem.helper.NumberUtils;
import io.github.blackfishlabs.artem.ui.common.BaseFilter;

import static java.util.Objects.requireNonNull;

public class SelectOrderItemsAdapter extends RecyclerView.Adapter<SelectOrderItemsViewHolder>
        implements Filterable {

    private final List<OrderItemJson> orderItems;
    private final SelectOrderItemsCallbacks itemsCallbacks;
    private final Context context;

    private List<OrderItemJson> originalOrderItems;

    private OrderItemListFilter filter;

    public SelectOrderItemsAdapter(final List<OrderItemJson> orderItems,
                                   final SelectOrderItemsCallbacks itemsCallbacks, final Context context) {
        this.orderItems = orderItems;
        this.itemsCallbacks = itemsCallbacks;
        this.context = context;
    }

    @Override
    public SelectOrderItemsViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_order_item, parent, false);
        return new SelectOrderItemsViewHolder(itemView, itemsCallbacks);
    }

    @Override
    public void onBindViewHolder(@NonNull final SelectOrderItemsViewHolder holder, final int position) {
        final OrderItemJson orderItem = orderItems.get(position);
        final ProductJson product = orderItem.getItem();

        holder.textViewProductName.setText(product.getDescription());

        holder.textViewProductPrice.setText(
                context.getString(R.string.select_order_items_template_product_price,
                        FormattingUtils.formatAsCurrency((product.getValue() == null) ? 0 : product.getValue().doubleValue())));

        holder.textViewItemTotal.setText(
                context.getString(R.string.select_order_items_template_item_total,
                        FormattingUtils.formatAsCurrency((orderItem.getSubTotal() == null) ? 0 : orderItem.getSubTotal().doubleValue())));

        holder.textViewQuantity.setText(
                FormattingUtils.formatAsNumber(NumberUtils.withDefaultValue(orderItem.getQuantity(), 0)));

        requireNonNull(holder.inputLayoutEditQuantity.getEditText()).setText(holder.textViewQuantity.getText());

        holder.itemView.setTag(orderItem);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new OrderItemListFilter();
        }
        return filter;
    }

    public boolean isEmptyList() {
        return getItemCount() == 0;
    }

    private class OrderItemListFilter extends BaseFilter<OrderItemJson> {

        OrderItemListFilter() {
            super(SelectOrderItemsAdapter.this, orderItems, originalOrderItems);
        }

        @Override
        protected String[] filterValues(final OrderItemJson orderItem) {
            final ProductJson product = orderItem.getItem();
            return new String[]{product.getDescription()};
        }
    }
}

