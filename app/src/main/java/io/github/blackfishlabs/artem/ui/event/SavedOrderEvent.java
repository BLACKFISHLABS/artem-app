package io.github.blackfishlabs.artem.ui.event;

import io.github.blackfishlabs.artem.domain.json.OrderJson;

public class SavedOrderEvent {

    private final OrderJson mOrder;

    private SavedOrderEvent(final OrderJson order) {
        mOrder = order;
    }

    static SavedOrderEvent newEvent(final OrderJson order) {
        return new SavedOrderEvent(order);
    }

    public OrderJson getOrder() {
        return mOrder;
    }
}