package io.github.blackfishlabs.artem.domain.entity;

import com.google.gson.GsonBuilder;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass
public class AtelierFixedExpenseEntity implements RealmModel {

    @PrimaryKey
    private String id;

    private String description;
    private Double value;

    @Required
    private String owner;

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, AtelierFixedExpenseEntity.class);
    }

    public String getId() {
        return id;
    }

    public AtelierFixedExpenseEntity withId(final String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AtelierFixedExpenseEntity withDescription(final String description) {
        this.description = description;
        return this;
    }

    public Double getValue() {
        return value;
    }

    public AtelierFixedExpenseEntity withValue(final Double value) {
        this.value = value;
        return this;
    }

    public String getCompanyId() {
        return owner;
    }

    public AtelierFixedExpenseEntity withCompanyId(final String companyId) {
        this.owner = companyId;
        return this;
    }
}
