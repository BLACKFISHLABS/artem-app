package io.github.blackfishlabs.artem.domain.json;

import java.math.BigDecimal;

public class AtelierFixedExpenseJson {

    private String id;
    private String description;
    private BigDecimal value;
    private String owner;

    public String getId() {
        return id;
    }

    public AtelierFixedExpenseJson withId(final String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AtelierFixedExpenseJson withDescription(final String description) {
        this.description = description;
        return this;
    }

    public BigDecimal getValue() {
        return value;
    }

    public AtelierFixedExpenseJson withValue(final BigDecimal value) {
        this.value = value;
        return this;
    }

    public String getCompanyId() {
        return owner;
    }

    public AtelierFixedExpenseJson withCompanyId(final String companyId) {
        this.owner = companyId;
        return this;
    }
}
