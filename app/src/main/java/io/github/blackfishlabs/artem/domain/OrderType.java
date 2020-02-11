package io.github.blackfishlabs.artem.domain;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({OrderType.ORDER_TYPE_NORMAL, OrderType.ORDER_TYPE_RETURNED})
public @interface OrderType {
    int ORDER_TYPE_NORMAL = 0;
    int ORDER_TYPE_RETURNED = 1;
}