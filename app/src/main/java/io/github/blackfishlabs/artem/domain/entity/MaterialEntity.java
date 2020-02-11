package io.github.blackfishlabs.artem.domain.entity;

import com.google.gson.GsonBuilder;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class MaterialEntity implements RealmModel {

    @PrimaryKey
    private String id;

    private String description;
    private Double value;

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, MaterialEntity.class);
    }

    public String getId() {
        return id;
    }

    public MaterialEntity withId(final String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public MaterialEntity withDescription(final String description) {
        this.description = description;
        return this;
    }

    public Double getValue() {
        return value;
    }

    public MaterialEntity withValue(final Double value) {
        this.value = value;
        return this;
    }
}
