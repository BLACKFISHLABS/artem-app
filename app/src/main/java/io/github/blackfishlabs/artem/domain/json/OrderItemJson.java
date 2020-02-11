package io.github.blackfishlabs.artem.domain.json;

import java.math.BigDecimal;

public class OrderItemJson {

    private String id;
    private ProductJson item;
    private Float quantity;
    private BigDecimal subTotal;
    private String tempId;

    public String getId() {
        return id;
    }

    public OrderItemJson withId(final String id) {
        this.id = id;
        return this;
    }

    public ProductJson getItem() {
        return item;
    }

    public OrderItemJson withItem(final ProductJson item) {
        this.item = item;
        return this;
    }

    public Float getQuantity() {
        return quantity;
    }

    public OrderItemJson withQuantity(final Float quantity) {
        this.quantity = quantity;
        return this;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public OrderItemJson withSubTotal(final BigDecimal subTotal) {
        this.subTotal = subTotal;
        return this;
    }

    public OrderItemJson addQuantity(final float quantity) {
        if (this.quantity == null) {
            this.quantity = quantity;
        } else {
            this.quantity += quantity;
        }
        calculateSubTotal();
        return this;
    }

    public OrderItemJson removeOneFromQuantity() {
        if (quantity != null && quantity >= 1) {
            --quantity;
            calculateSubTotal();
        }
        return this;
    }

    private void calculateSubTotal() {
        subTotal = item.getValue().multiply(new BigDecimal(this.quantity));
    }

    public String getTempId() {
        return tempId;
    }

    public OrderItemJson withTempId(final String tempId) {
        this.tempId = tempId;
        return this;
    }
}
