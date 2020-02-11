package io.github.blackfishlabs.artem.domain.entity;

import com.google.gson.GsonBuilder;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass
public class ProductEntity implements RealmModel {

    @PrimaryKey
    private String id;

    private String description;
    private Double value;
    private String annotations;
    private Float markup;
    private RealmList<MaterialEntity> items;
    @Required
    private String owner;

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, ProductEntity.class);
    }

    public String getId() {
        return id;
    }

    public ProductEntity withId(final String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ProductEntity withDescription(final String description) {
        this.description = description;
        return this;
    }

    public Double getValue() {
        return value;
    }

    public ProductEntity withValue(final Double value) {
        this.value = value;
        return this;
    }

    public String getAnnotations() {
        return annotations;
    }

    public ProductEntity withAnnotations(final String annotations) {
        this.annotations = annotations;
        return this;
    }

    public Float getMarkup() {
        return markup;
    }

    public ProductEntity withMarkup(final Float markup) {
        this.markup = markup;
        return this;
    }

    public RealmList<MaterialEntity> getItems() {
        return items;
    }

    public ProductEntity withItems(final RealmList<MaterialEntity> items) {
        this.items = items;
        return this;
    }

    public String getCompanyId() {
        return owner;
    }

    public ProductEntity withCompanyId(final String companyId) {
        this.owner = companyId;
        return this;
    }
}
