package io.github.blackfishlabs.artem.domain.json;

import java.math.BigDecimal;

public class AtelierInvestmentJson {

    private String id;
    private String description;
    private BigDecimal value;
    private String owner;

    public String getId() {
        return id;
    }

    public AtelierInvestmentJson withId(final String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AtelierInvestmentJson withDescription(final String description) {
        this.description = description;
        return this;
    }

    public BigDecimal getValue() {
        return value;
    }

    public AtelierInvestmentJson withValue(final BigDecimal value) {
        this.value = value;
        return this;
    }

    public String getCompanyId() {
        return owner;
    }

    public AtelierInvestmentJson withCompanyId(final String companyId) {
        this.owner = companyId;
        return this;
    }

}
