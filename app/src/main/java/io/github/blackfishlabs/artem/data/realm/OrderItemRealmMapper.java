package io.github.blackfishlabs.artem.data.realm;

import java.math.BigDecimal;

import io.github.blackfishlabs.artem.data.LocalDataInjector;
import io.github.blackfishlabs.artem.domain.entity.OrderItemEntity;
import io.github.blackfishlabs.artem.domain.json.OrderItemJson;

public class OrderItemRealmMapper extends RealmMapper<OrderItemJson, OrderItemEntity> {

    @Override
    public OrderItemEntity toEntity(OrderItemJson object) {
        return new OrderItemEntity()
                .withId(object.getTempId())
                .withItem(LocalDataInjector.getProductMapper().toEntity(object.getItem()))
                .withQuantity(object.getQuantity())
                .withSubTotal(object.getSubTotal().doubleValue());
    }

    @Override
    public OrderItemJson toViewObject(OrderItemEntity entity) {
        return new OrderItemJson()
                .withId(entity.getId())
                .withItem(LocalDataInjector.getProductMapper().toViewObject(entity.getItem()))
                .withQuantity(entity.getQuantity())
                .withSubTotal(BigDecimal.valueOf(entity.getSubTotal()));
    }
}
