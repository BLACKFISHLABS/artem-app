package io.github.blackfishlabs.artem.ui.adapter;

import io.github.blackfishlabs.artem.domain.json.OrderItemJson;

public interface SelectOrderItemsCallbacks {

    void onAddOrderItemRequested(final OrderItemJson orderItem, final int position);

    void onRemoveOrderItemRequested(final OrderItemJson orderItem, final int position);

    void onChangeOrderItemQuantityRequested(final OrderItemJson orderItem, final float quantity, final int position);
}

