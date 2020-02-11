package io.github.blackfishlabs.artem.data.realm;

import java.math.BigDecimal;

import io.github.blackfishlabs.artem.domain.entity.MaterialEntity;
import io.github.blackfishlabs.artem.domain.json.MaterialJson;

public class MaterialRealmMapper extends RealmMapper<MaterialJson, MaterialEntity> {

    @Override
    public MaterialEntity toEntity(MaterialJson object) {
        return new MaterialEntity()
                .withDescription(object.getDescription())
                .withId(object.getId())
                .withValue(object.getValue().doubleValue());
    }

    @Override
    public MaterialJson toViewObject(MaterialEntity entity) {
        return new MaterialJson()
                .withDescription(entity.getDescription())
                .withId(entity.getId())
                .withValue(BigDecimal.valueOf(entity.getValue()));
    }
}
