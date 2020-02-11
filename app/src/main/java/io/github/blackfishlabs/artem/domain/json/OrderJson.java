package io.github.blackfishlabs.artem.domain.json;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

import io.github.blackfishlabs.artem.domain.OrderStatus;
import io.github.blackfishlabs.artem.domain.OrderType;

public class OrderJson {

    private String id;
    private String code;
    private Integer type;
    private Long issueDate;
    private BigDecimal discount;
    private Float discountPercentage;
    private String observation;
    private CustomerJson customer;
    private PaymentMethodJson paymentMethod;
    private List<OrderItemJson> items = Lists.newArrayList();
    private String owner;
    private Integer status;
    private BigDecimal valueHour;
    private Integer amountValueHour;

    public String getId() {
        return id;
    }

    public OrderJson withId(final String id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public OrderJson withCode(final String code) {
        this.code = code;
        return this;
    }

    @OrderType
    public Integer getType() {
        return type;
    }

    public OrderJson withType(@OrderType final Integer type) {
        this.type = type;
        return this;
    }

    public Long getIssueDate() {
        return issueDate;
    }

    public OrderJson withIssueDate(final Long issueDate) {
        this.issueDate = issueDate;
        return this;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public OrderJson withDiscount(final BigDecimal discount) {
        this.discount = discount;
        return this;
    }

    public Float getDiscountPercentage() {
        return discountPercentage;
    }

    public OrderJson withDiscountPercentage(final Float discountPercentage) {
        this.discountPercentage = discountPercentage;
        return this;
    }

    public String getObservation() {
        return observation;
    }

    public OrderJson withObservation(final String observation) {
        this.observation = observation;
        return this;
    }

    public CustomerJson getCustomer() {
        return customer;
    }

    public OrderJson withCustomer(final CustomerJson customer) {
        this.customer = customer;
        return this;
    }

    public PaymentMethodJson getPaymentMethod() {
        return paymentMethod;
    }

    public OrderJson withPaymentMethod(final PaymentMethodJson paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public List<OrderItemJson> getItems() {
        return items;
    }

    public OrderJson withItems(final List<OrderItemJson> items) {
        this.items = items;
        return this;
    }

    public String getCompanyId() {
        return owner;
    }

    public OrderJson withCompanyId(final String companyId) {
        this.owner = companyId;
        return this;
    }

    @OrderStatus
    public Integer getStatus() {
        return status;
    }

    public OrderJson withStatus(@OrderStatus final Integer status) {
        if (status == null) {
            this.status = OrderStatus.STATUS_MODIFIED;
        } else {
            this.status = status;
        }
        return this;
    }

    public BigDecimal getTotalItems() {
        BigDecimal totalItems = BigDecimal.ZERO;
        for (OrderItemJson item : getItems())
            totalItems = totalItems.add(item.getSubTotal());
        return totalItems;
    }

    public BigDecimal getTotalOrder() {
        return getTotalItems().add(getValueHour().multiply(BigDecimal.valueOf(getAmountValueHour()))).subtract(getDiscount());
    }

    public boolean isStatusCreatedOrModified() {
        return status == OrderStatus.STATUS_CREATED || status == OrderStatus.STATUS_MODIFIED;
    }

    public boolean isStatusNotCreatedAndModified() {
        return status != OrderStatus.STATUS_CREATED && status != OrderStatus.STATUS_MODIFIED;
    }

    public boolean isStatusInvoice() {
        return status == OrderStatus.STATUS_INVOICED;
    }

    public boolean isStatusCanceled() {
        return status == OrderStatus.STATUS_CANCELLED;
    }

    public BigDecimal getValueHour() {
        return valueHour;
    }

    public OrderJson withValueHour(final BigDecimal valueHour) {
        this.valueHour = valueHour;
        return this;
    }

    public Integer getAmountValueHour() {
        return amountValueHour;
    }

    public OrderJson withAmountValueHour(final Integer amountValueHour) {
        this.amountValueHour = amountValueHour;
        return this;
    }

}
