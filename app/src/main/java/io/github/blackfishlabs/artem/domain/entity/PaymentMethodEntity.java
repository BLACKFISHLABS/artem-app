package io.github.blackfishlabs.artem.domain.entity;

import com.google.gson.GsonBuilder;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass
public class PaymentMethodEntity implements RealmModel {

    public static final class Fields {
        public static final String PAYMENT_METHOD_ID = "id";
        public static final String DESCRIPTION = "description";
    }

    @PrimaryKey
    private String id;
    private String code;
    @Required
    private String description;
    @Required
    private String owner;

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, PaymentMethodEntity.class);
    }

    public String getPaymentMethodId() {
        return id;
    }

    public PaymentMethodEntity withPaymentMethodId(final String paymentMethodId) {
        this.id = paymentMethodId;
        return this;
    }

    public String getCode() {
        return code;
    }

    public PaymentMethodEntity withCode(final String code) {
        this.code = code;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PaymentMethodEntity withDescription(final String description) {
        this.description = description;
        return this;
    }

    public String getCompanyId() {
        return owner;
    }

    public PaymentMethodEntity withCompanyId(final String companyId) {
        this.owner = companyId;
        return this;
    }
}

