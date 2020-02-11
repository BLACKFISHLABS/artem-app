package io.github.blackfishlabs.artem.data.realm;

import java.math.BigDecimal;

import io.github.blackfishlabs.artem.data.LocalDataInjector;
import io.github.blackfishlabs.artem.domain.entity.MaterialEntity;
import io.github.blackfishlabs.artem.domain.entity.ProductEntity;
import io.github.blackfishlabs.artem.domain.json.ProductJson;
import io.realm.RealmList;

public class ProductRealmMapper extends RealmMapper<ProductJson, ProductEntity> {

    @Override
    public ProductEntity toEntity(ProductJson object) {
        return new ProductEntity()
                .withItems((RealmList<MaterialEntity>) LocalDataInjector.getMaterialMapper().toEntities(object.getItems()))
                .withId(object.getId())
                .withDescription(object.getDescription())
                .withValue(object.getValue().doubleValue())
                .withCompanyId(object.getCompanyId())
                .withAnnotations(object.getAnnotations())
                .withMarkup(object.getMarkup());
    }

    @Override
    public ProductJson toViewObject(ProductEntity entity) {
        return new ProductJson()
                .withItems(LocalDataInjector.getMaterialMapper().toViewObjects(entity.getItems()))
                .withId(entity.getId())
                .withDescription(entity.getDescription())
                .withValue(BigDecimal.valueOf(entity.getValue()))
                .withCompanyId(entity.getCompanyId())
                .withAnnotations(entity.getAnnotations())
                .withMarkup(entity.getMarkup());
    }
}
