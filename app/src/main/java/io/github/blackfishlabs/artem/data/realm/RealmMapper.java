package io.github.blackfishlabs.artem.data.realm;

import java.util.ArrayList;
import java.util.List;

import io.github.blackfishlabs.artem.data.realm.interfaces.Mapper;
import io.realm.RealmList;
import io.realm.RealmModel;

public abstract class RealmMapper<T, E extends RealmModel> implements Mapper<T, E> {

    @Override
    public List<T> toViewObjects(List<E> entities) {
        final List<T> objects = new ArrayList<>();

        for (E entity : entities) {
            objects.add(toViewObject(entity));
        }

        return objects;
    }

    @Override
    public RealmList<E> toEntities(List<T> objects) {
        final RealmList<E> entities = new RealmList<>();

        for (T object : objects) {
            entities.add(toEntity(object));
        }

        return entities;
    }
}

