package io.github.blackfishlabs.artem.domain;

public class OrderChartData {

    private final String name;

    private final float amount;

    private OrderChartData(String name, float amount) {
        this.name = name;
        this.amount = amount;
    }

    public static OrderChartData create(String name, float amount) {
        return new OrderChartData(name, amount);
    }

    public String getName() {
        return name;
    }

    public float getAmount() {
        return amount;
    }
}
