package io.github.blackfishlabs.artem.domain.json;

public class PaymentMethodJson {

    private String id;
    private String code;
    private String description;
    private String owner;

    public String getPaymentMethodId() {
        return id;
    }

    public PaymentMethodJson withPaymentMethodId(final String paymentMethodId) {
        this.id = paymentMethodId;
        return this;
    }

    public String getCode() {
        return code;
    }

    public PaymentMethodJson withCode(final String code) {
        this.code = code;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PaymentMethodJson withDescription(final String description) {
        this.description = description;
        return this;
    }

    public String getCompanyId() {
        return owner;
    }

    public PaymentMethodJson withCompanyId(final String companyId) {
        this.owner = companyId;
        return this;
    }
}
