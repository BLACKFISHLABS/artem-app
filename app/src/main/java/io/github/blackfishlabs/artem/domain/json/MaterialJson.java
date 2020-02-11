package io.github.blackfishlabs.artem.domain.json;

import java.math.BigDecimal;

public class MaterialJson {

    private String id;
    private String description;
    private BigDecimal value;

    public String getId() {
        return id;
    }

    public MaterialJson withId(final String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public MaterialJson withDescription(final String description) {
        this.description = description;
        return this;
    }

    public BigDecimal getValue() {
        return value;
    }

    public MaterialJson withValue(final BigDecimal value) {
        this.value = value;
        return this;
    }
}
