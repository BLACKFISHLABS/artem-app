package io.github.blackfishlabs.artem.ui.event;

import io.github.blackfishlabs.artem.domain.json.ProductJson;

public class SelectedProductEvent {

    private final ProductJson mProduct;

    private SelectedProductEvent(final ProductJson product) {
        mProduct = product;
    }

    public static SelectedProductEvent selectProduct(final ProductJson product) {
        return new SelectedProductEvent(product);
    }

    public ProductJson getProduct() {
        return mProduct;
    }
}
