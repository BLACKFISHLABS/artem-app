package io.github.blackfishlabs.artem.data.realm;

import java.math.BigDecimal;

import io.github.blackfishlabs.artem.domain.entity.AtelierFixedExpenseEntity;
import io.github.blackfishlabs.artem.domain.json.AtelierFixedExpenseJson;

public class AtelierFixedExpenseRealmMapper extends RealmMapper<AtelierFixedExpenseJson, AtelierFixedExpenseEntity> {

    @Override
    public AtelierFixedExpenseEntity toEntity(AtelierFixedExpenseJson object) {
        return new AtelierFixedExpenseEntity()
                .withDescription(object.getDescription())
                .withId(object.getId())
                .withCompanyId(object.getCompanyId())
                .withValue(object.getValue().doubleValue());
    }

    @Override
    public AtelierFixedExpenseJson toViewObject(AtelierFixedExpenseEntity entity) {
        return new AtelierFixedExpenseJson()
                .withDescription(entity.getDescription())
                .withId(entity.getId())
                .withCompanyId(entity.getCompanyId())
                .withValue(BigDecimal.valueOf(entity.getValue()));
    }
}
