package io.github.blackfishlabs.artem.ui.event;

import java.util.List;

import io.github.blackfishlabs.artem.domain.json.OrderItemJson;

public class AddedOrderItemsEvent {

    private final List<OrderItemJson> mOrderItems;

    private AddedOrderItemsEvent(final List<OrderItemJson> orderItems) {
        mOrderItems = orderItems;
    }

    public static AddedOrderItemsEvent newEvent(final List<OrderItemJson> orderItems) {
        return new AddedOrderItemsEvent(orderItems);
    }

    public List<OrderItemJson> getOrderItems() {
        return mOrderItems;
    }
}
