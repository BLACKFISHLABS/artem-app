package io.github.blackfishlabs.artem.domain.json;

import java.math.BigDecimal;

public class AtelierValuePerHourJson {

    private String id;
    private Integer workHour;
    private Integer weekDays;
    private BigDecimal moneyFocus;
    private BigDecimal value;
    private String owner;

    public String getId() {
        return id;
    }

    public AtelierValuePerHourJson withId(final String id) {
        this.id = id;
        return this;
    }

    public Integer getWorkHour() {
        return workHour;
    }

    public AtelierValuePerHourJson withWorkHour(final Integer workHour) {
        this.workHour = workHour;
        return this;
    }

    public Integer getWeekDays() {
        return weekDays;
    }

    public AtelierValuePerHourJson withWeekDays(final Integer weekDays) {
        this.weekDays = weekDays;
        return this;
    }

    public BigDecimal getMoneyFocus() {
        return moneyFocus;
    }

    public AtelierValuePerHourJson withMoneyFocus(final BigDecimal moneyFocus) {
        this.moneyFocus = moneyFocus;
        return this;
    }

    public BigDecimal getValue() {
        return value;
    }

    public AtelierValuePerHourJson withValue(final BigDecimal value) {
        this.value = value;
        return this;
    }

    public String getCompanyId() {
        return owner;
    }

    public AtelierValuePerHourJson withCompanyId(final String companyId) {
        this.owner = companyId;
        return this;
    }
}
