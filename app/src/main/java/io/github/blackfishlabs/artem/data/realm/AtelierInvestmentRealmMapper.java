package io.github.blackfishlabs.artem.data.realm;

import java.math.BigDecimal;

import io.github.blackfishlabs.artem.domain.entity.AtelierInvestmentEntity;
import io.github.blackfishlabs.artem.domain.json.AtelierInvestmentJson;

public class AtelierInvestmentRealmMapper extends RealmMapper<AtelierInvestmentJson, AtelierInvestmentEntity> {

    @Override
    public AtelierInvestmentEntity toEntity(AtelierInvestmentJson object) {
        return new AtelierInvestmentEntity()
                .withId(object.getId())
                .withDescription(object.getDescription())
                .withCompanyId(object.getCompanyId())
                .withValue(object.getValue().doubleValue());
    }

    @Override
    public AtelierInvestmentJson toViewObject(AtelierInvestmentEntity entity) {
        return new AtelierInvestmentJson()
                .withId(entity.getId())
                .withDescription(entity.getDescription())
                .withCompanyId(entity.getCompanyId())
                .withValue(BigDecimal.valueOf(entity.getValue()));
    }
}
