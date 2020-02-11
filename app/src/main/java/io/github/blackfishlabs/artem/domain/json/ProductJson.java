package io.github.blackfishlabs.artem.domain.json;

import java.math.BigDecimal;
import java.util.List;

public class ProductJson {

    private String id;
    private String description;
    private BigDecimal value;
    private String annotations;
    private Float markup;
    private List<MaterialJson> items;
    private String owner;

    public String getId() {
        return id;
    }

    public ProductJson withId(final String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ProductJson withDescription(final String description) {
        this.description = description;
        return this;
    }

    public BigDecimal getValue() {
        return value;
    }

    public ProductJson withValue(final BigDecimal value) {
        this.value = value;
        return this;
    }

    public String getAnnotations() {
        return annotations;
    }

    public ProductJson withAnnotations(final String annotations) {
        this.annotations = annotations;
        return this;
    }

    public Float getMarkup() {
        return markup;
    }

    public ProductJson withMarkup(final Float markup) {
        this.markup = markup;
        return this;
    }

    public List<MaterialJson> getItems() {
        return items;
    }

    public ProductJson withItems(final List<MaterialJson> items) {
        this.items = items;
        return this;
    }

    public String getCompanyId() {
        return owner;
    }

    public ProductJson withCompanyId(final String companyId) {
        this.owner = companyId;
        return this;
    }
}
