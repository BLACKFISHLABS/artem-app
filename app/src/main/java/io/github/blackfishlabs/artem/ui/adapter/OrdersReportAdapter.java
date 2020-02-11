package io.github.blackfishlabs.artem.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.domain.json.OrderJson;
import io.github.blackfishlabs.artem.helper.DrawableUtils;
import io.github.blackfishlabs.artem.helper.FormattingUtils;
import io.github.blackfishlabs.artem.helper.OrderUtils;

import static android.view.LayoutInflater.from;
import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;
import static io.github.blackfishlabs.artem.helper.StringUtils.isNullOrEmpty;

public class OrdersReportAdapter extends RecyclerView.Adapter<OrdersReportViewHolder> {

    private static final int FIRST_POSITION = 0;

    private final List<OrderJson> mOrders;

    public OrdersReportAdapter(final List<OrderJson> orders) {
        mOrders = orders;
    }

    @Override
    public OrdersReportViewHolder onCreateViewHolder(
            final ViewGroup parent, final int viewType) {
        View itemView = from(parent.getContext()).inflate(R.layout.list_item_order_report, parent, false);
        return new OrdersReportViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OrdersReportViewHolder holder, final int position) {
        final OrderJson order = mOrders.get(position);
        final Context context = holder.itemView.getContext();

        if (!isNullOrEmpty(order.getCode())) {
            holder.textViewOrderNumber
                    .setText(context.getString(R.string.order_list_template_text_order_number,
                            order.getCode()));
        } else {
            holder.textViewOrderNumber
                    .setText(context.getString(R.string.orders_report_text_no_order_number));
        }

        holder.textViewCustomerName
                .setText(context.getString(R.string.order_list_template_text_customer_name,
                        order.getCustomer().getName()));

        final double totalItems = order.getTotalOrder().doubleValue();
        holder.textViewTotalOrder
                .setText(context.getString(R.string.order_list_template_text_order_total,
                        FormattingUtils.formatAsCurrency(totalItems)));

        holder.textViewOrderDate
                .setText(context.getString(R.string.order_list_template_text_order_date,
                        FormattingUtils.formatAsDateTime(order.getIssueDate())));

        DrawableUtils.changeDrawableBackground(context, holder.viewOrderStatus.getBackground(),
                OrderUtils.getStatusColor(order.getStatus()));
    }

    @Override
    public int getItemCount() {
        return mOrders.size();
    }

    public int updateOrder(final OrderJson order) {
        if (order != null) {
            final int currentPosition = mOrders.indexOf(order);
            if (currentPosition == NO_POSITION) {
                mOrders.add(FIRST_POSITION, order);
                notifyItemInserted(FIRST_POSITION);
                return FIRST_POSITION;
            } else {
                mOrders.set(currentPosition, order);
                notifyItemChanged(currentPosition);
                return currentPosition;
            }
        }
        return NO_POSITION;
    }
}

