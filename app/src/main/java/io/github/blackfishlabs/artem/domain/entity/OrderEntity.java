package io.github.blackfishlabs.artem.domain.entity;

import com.google.gson.GsonBuilder;

import io.github.blackfishlabs.artem.domain.OrderStatus;
import io.github.blackfishlabs.artem.domain.OrderType;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass
public class OrderEntity implements RealmModel {

    public static final class Fields {
        public static final String ID = "id";
        public static final String CODE = "code";
        public static final String ISSUE_DATE = "issueDate";
        public static final String STATUS = "status";
        public static final String OWNER = "owner";
        public static final String CUSTOMER_NAME = "customer." + CustomerEntity.Fields.NAME;
    }

    @PrimaryKey
    private String id;
    private String code;
    @Required
    private Integer type;
    @Required
    private Long issueDate;
    private Double discount;
    private Float discountPercentage;
    private String observation;
    private CustomerEntity customer;
    private PaymentMethodEntity paymentMethod;
    private RealmList<OrderItemEntity> items;
    @Required
    private String owner;
    @Required
    private Integer status;
    private Double valueHour;
    private Integer amountValueHour;

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, OrderEntity.class);
    }

    public String getId() {
        return id;
    }

    public OrderEntity withId(final String id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public OrderEntity withCode(final String code) {
        this.code = code;
        return this;
    }

    @OrderType
    public Integer getType() {
        return type;
    }

    public OrderEntity withType(@OrderType final Integer type) {
        this.type = type;
        return this;
    }

    public Long getIssueDate() {
        return issueDate;
    }

    public OrderEntity withIssueDate(final Long issueDate) {
        this.issueDate = issueDate;
        return this;
    }

    public Double getDiscount() {
        return discount;
    }

    public OrderEntity withDiscount(final Double discount) {
        this.discount = discount;
        return this;
    }

    public Float getDiscountPercentage() {
        return discountPercentage;
    }

    public OrderEntity withDiscountPercentage(final Float discountPercentage) {
        this.discountPercentage = discountPercentage;
        return this;
    }

    public String getObservation() {
        return observation;
    }

    public OrderEntity withObservation(final String observation) {
        this.observation = observation;
        return this;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public OrderEntity withCustomer(final CustomerEntity customer) {
        this.customer = customer;
        return this;
    }

    public PaymentMethodEntity getPaymentMethod() {
        return paymentMethod;
    }

    public OrderEntity withPaymentMethod(final PaymentMethodEntity paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public RealmList<OrderItemEntity> getItems() {
        return items;
    }

    public OrderEntity withItems(final RealmList<OrderItemEntity> items) {
        this.items = items;
        return this;
    }

    public String getCompanyId() {
        return owner;
    }

    public OrderEntity withCompanyId(final String companyId) {
        this.owner = companyId;
        return this;
    }

    @OrderStatus
    public Integer getStatus() {
        return status;
    }

    public OrderEntity withStatus(@OrderStatus final Integer status) {
        if (status == null) {
            this.status = OrderStatus.STATUS_MODIFIED;
        } else {
            this.status = status;
        }
        return this;
    }

    public Double getValueHour() {
        return valueHour;
    }

    public OrderEntity withValueHour(final Double valueHour) {
        this.valueHour = valueHour;
        return this;
    }

    public Integer getAmountValueHour() {
        return amountValueHour;
    }

    public OrderEntity withAmountValueHour(final Integer amountValueHour) {
        this.amountValueHour = amountValueHour;
        return this;
    }
}
