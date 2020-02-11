package io.github.blackfishlabs.artem.ui.event;

import io.github.blackfishlabs.artem.domain.json.OrderJson;

public class SelectedOrderEvent {

    private final OrderJson mOrder;

    private SelectedOrderEvent(final OrderJson order) {
        mOrder = order;
    }

    public static SelectedOrderEvent selectOrder(final OrderJson order) {
        return new SelectedOrderEvent(order);
    }

    public OrderJson getOrder() {
        return mOrder;
    }
}
