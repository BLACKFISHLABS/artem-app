package io.github.blackfishlabs.artem.data.realm;

import io.github.blackfishlabs.artem.domain.entity.AtelierEntity;
import io.github.blackfishlabs.artem.domain.json.AtelierJson;

public class AtelierRealmMapper extends RealmMapper<AtelierJson, AtelierEntity> {

    @Override
    public AtelierEntity toEntity(AtelierJson object) {
        return new AtelierEntity()
                .withId(object.getId())
                .withName(object.getName())
                .withCnpj(object.getCnpj())
                .withPhoneNumber(object.getPhoneNumber())
                .withFirebaseId(object.getFirebaseId());
    }

    @Override
    public AtelierJson toViewObject(AtelierEntity entity) {
        return new AtelierJson()
                .withId(entity.getId())
                .withName(entity.getName())
                .withCnpj(entity.getCnpj())
                .withPhoneNumber(entity.getPhoneNumber())
                .withFirebaseId(entity.getFirebaseId());
    }
}
