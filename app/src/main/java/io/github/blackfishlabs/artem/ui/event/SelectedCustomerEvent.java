package io.github.blackfishlabs.artem.ui.event;

import io.github.blackfishlabs.artem.domain.json.CustomerJson;

public class SelectedCustomerEvent {

    private final CustomerJson mCustomer;

    private SelectedCustomerEvent(final CustomerJson customer) {
        mCustomer = customer;
    }

    public static SelectedCustomerEvent selectCustomer(final CustomerJson customer) {
        return new SelectedCustomerEvent(customer);
    }

    public CustomerJson getCustomer() {
        return mCustomer;
    }
}
