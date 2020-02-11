package io.github.blackfishlabs.artem.domain.entity;

import com.google.gson.GsonBuilder;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass
public class OrderItemEntity implements RealmModel {

    public static final class Fields {
        public static final String ID = "id";
    }

    @PrimaryKey
    private String id;
    private ProductEntity item;
    @Required
    private Float quantity;
    @Required
    private Double subTotal;

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, OrderItemEntity.class);
    }

    public String getId() {
        return id;
    }

    public OrderItemEntity withId(final String id) {
        this.id = id;
        return this;
    }

    public ProductEntity getItem() {
        return item;
    }

    public OrderItemEntity withItem(final ProductEntity item) {
        this.item = item;
        return this;
    }

    public Float getQuantity() {
        return quantity;
    }

    public OrderItemEntity withQuantity(final Float quantity) {
        this.quantity = quantity;
        return this;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public OrderItemEntity withSubTotal(final Double subTotal) {
        this.subTotal = subTotal;
        return this;
    }
}
