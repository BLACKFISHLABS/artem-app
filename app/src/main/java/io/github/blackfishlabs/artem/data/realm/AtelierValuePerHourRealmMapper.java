package io.github.blackfishlabs.artem.data.realm;

import java.math.BigDecimal;

import io.github.blackfishlabs.artem.domain.entity.AtelierValuePerHourEntity;
import io.github.blackfishlabs.artem.domain.json.AtelierValuePerHourJson;

public class AtelierValuePerHourRealmMapper extends RealmMapper<AtelierValuePerHourJson, AtelierValuePerHourEntity> {

    @Override
    public AtelierValuePerHourEntity toEntity(AtelierValuePerHourJson object) {
        return new AtelierValuePerHourEntity()
                .withId(object.getId())
                .withMoneyFocus(object.getMoneyFocus().doubleValue())
                .withCompanyId(object.getCompanyId())
                .withValue(object.getValue().doubleValue())
                .withWeekDays(object.getWeekDays())
                .withWorkHour(object.getWorkHour())
                .withCompanyId(object.getCompanyId());
    }

    @Override
    public AtelierValuePerHourJson toViewObject(AtelierValuePerHourEntity entity) {
        return new AtelierValuePerHourJson()
                .withId(entity.getId())
                .withMoneyFocus(BigDecimal.valueOf(entity.getMoneyFocus()))
                .withCompanyId(entity.getCompanyId())
                .withValue(BigDecimal.valueOf(entity.getValue()))
                .withWeekDays(entity.getWeekDays())
                .withWorkHour(entity.getWorkHour())
                .withCompanyId(entity.getCompanyId());
    }
}
