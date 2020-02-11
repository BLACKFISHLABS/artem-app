package io.github.blackfishlabs.artem.data.realm;

import io.github.blackfishlabs.artem.domain.entity.CustomerEntity;
import io.github.blackfishlabs.artem.domain.json.CustomerJson;

public class CustomerRealmMapper extends RealmMapper<CustomerJson, CustomerEntity> {

    @Override
    public CustomerEntity toEntity(CustomerJson object) {
        return new CustomerEntity()
                .withId(object.getId())
                .withName(object.getName())
                .withEmail(object.getEmail())
                .withAddress(object.getAddress())
                .withNumber(object.getNumber())
                .withNeighborhood(object.getNeighborhood())
                .withCity(object.getCity())
                .withPostalCode(object.getPostalCode())
                .withMainPhone(object.getMainPhone())
                .withComplement(object.getComplement())
                .withCompanyId(object.getCompanyId());
    }

    @Override
    public CustomerJson toViewObject(CustomerEntity entity) {
        return new CustomerJson()
                .withId(entity.getId())
                .withName(entity.getName())
                .withEmail(entity.getEmail())
                .withAddress(entity.getAddress())
                .withNumber(entity.getNumber())
                .withNeighborhood(entity.getNeighborhood())
                .withCity(entity.getCity())
                .withPostalCode(entity.getPostalCode())
                .withMainPhone(entity.getMainPhone())
                .withComplement(entity.getComplement())
                .withCompanyId(entity.getCompanyId());
    }
}
