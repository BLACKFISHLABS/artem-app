package io.github.blackfishlabs.artem.domain.entity;

import com.google.gson.GsonBuilder;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass
public class AtelierValuePerHourEntity implements RealmModel {

    @PrimaryKey
    private String id;

    private Integer workHour;
    private Integer weekDays;
    private Double moneyFocus;
    private Double value;

    @Required
    private String owner;

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, AtelierValuePerHourEntity.class);
    }

    public String getId() {
        return id;
    }

    public AtelierValuePerHourEntity withId(final String id) {
        this.id = id;
        return this;
    }

    public Integer getWorkHour() {
        return workHour;
    }

    public AtelierValuePerHourEntity withWorkHour(final Integer workHour) {
        this.workHour = workHour;
        return this;
    }

    public Integer getWeekDays() {
        return weekDays;
    }

    public AtelierValuePerHourEntity withWeekDays(final Integer weekDays) {
        this.weekDays = weekDays;
        return this;
    }

    public Double getMoneyFocus() {
        return moneyFocus;
    }

    public AtelierValuePerHourEntity withMoneyFocus(final Double moneyFocus) {
        this.moneyFocus = moneyFocus;
        return this;
    }

    public Double getValue() {
        return value;
    }

    public AtelierValuePerHourEntity withValue(final Double value) {
        this.value = value;
        return this;
    }

    public String getCompanyId() {
        return owner;
    }

    public AtelierValuePerHourEntity withCompanyId(final String companyId) {
        this.owner = companyId;
        return this;
    }
}
