package io.github.blackfishlabs.artem.data.realm;

import io.github.blackfishlabs.artem.domain.entity.PaymentMethodEntity;
import io.github.blackfishlabs.artem.domain.json.PaymentMethodJson;

public class PaymentMethodRealmMapper extends RealmMapper<PaymentMethodJson, PaymentMethodEntity> {

    @Override
    public PaymentMethodEntity toEntity(PaymentMethodJson object) {
        return new PaymentMethodEntity()
                .withPaymentMethodId(object.getPaymentMethodId())
                .withCode(object.getCode())
                .withDescription(object.getDescription())
                .withCompanyId(object.getCompanyId());
    }

    @Override
    public PaymentMethodJson toViewObject(PaymentMethodEntity entity) {
        return new PaymentMethodJson()
                .withPaymentMethodId(entity.getPaymentMethodId())
                .withCode(entity.getCode())
                .withDescription(entity.getDescription())
                .withCompanyId(entity.getCompanyId());
    }
}
