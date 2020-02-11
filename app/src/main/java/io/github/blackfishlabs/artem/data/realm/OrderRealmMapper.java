package io.github.blackfishlabs.artem.data.realm;

import java.math.BigDecimal;

import io.github.blackfishlabs.artem.data.LocalDataInjector;
import io.github.blackfishlabs.artem.domain.entity.OrderEntity;
import io.github.blackfishlabs.artem.domain.entity.OrderItemEntity;
import io.github.blackfishlabs.artem.domain.json.OrderJson;
import io.realm.RealmList;

public class OrderRealmMapper extends RealmMapper<OrderJson, OrderEntity> {

    @Override
    public OrderEntity toEntity(OrderJson object) {
        return new OrderEntity()
                .withId(object.getId())
                .withCode(object.getCode())
                .withType(object.getType())
                .withIssueDate(object.getIssueDate())
                .withDiscount(object.getDiscount().doubleValue())
                .withDiscountPercentage(object.getDiscountPercentage())
                .withObservation(object.getObservation())
                .withCustomer(LocalDataInjector.getCustomerMapper().toEntity(object.getCustomer()))
                .withPaymentMethod(LocalDataInjector.getPaymentMethodMapper().toEntity(object.getPaymentMethod()))
                .withItems((RealmList<OrderItemEntity>) LocalDataInjector.getOrderItemMapper().toEntities(object.getItems()))
                .withCompanyId(object.getCompanyId())
                .withStatus(object.getStatus())
                .withValueHour(object.getValueHour().doubleValue())
                .withAmountValueHour(object.getAmountValueHour());
    }

    @Override
    public OrderJson toViewObject(OrderEntity entity) {
        return new OrderJson()
                .withId(entity.getId())
                .withCode(entity.getCode())
                .withType(entity.getType())
                .withIssueDate(entity.getIssueDate())
                .withDiscount(BigDecimal.valueOf(entity.getDiscount()))
                .withDiscountPercentage(entity.getDiscountPercentage())
                .withObservation(entity.getObservation())
                .withCustomer(LocalDataInjector.getCustomerMapper().toViewObject(entity.getCustomer()))
                .withPaymentMethod(LocalDataInjector.getPaymentMethodMapper().toViewObject(entity.getPaymentMethod()))
                .withItems(LocalDataInjector.getOrderItemMapper().toViewObjects(entity.getItems()))
                .withCompanyId(entity.getCompanyId())
                .withStatus(entity.getStatus())
                .withValueHour(BigDecimal.valueOf(entity.getValueHour()))
                .withAmountValueHour(entity.getAmountValueHour());
    }
}
