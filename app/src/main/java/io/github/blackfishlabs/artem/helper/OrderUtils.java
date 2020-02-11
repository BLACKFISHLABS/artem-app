package io.github.blackfishlabs.artem.helper;

import androidx.annotation.ColorRes;

import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.domain.OrderStatus;

import static android.R.color.transparent;

public class OrderUtils {

    private OrderUtils() {
    }

    public static @ColorRes
    int getStatusColor(@OrderStatus final int status) {
        switch (status) {
            case OrderStatus.STATUS_CREATED:
            case OrderStatus.STATUS_MODIFIED: {
                return R.color.color_order_is_pending;
            }
            case OrderStatus.STATUS_CANCELLED: {
                return R.color.color_order_was_cancelled;
            }
            case OrderStatus.STATUS_INVOICED: {
                return R.color.color_order_invoiced;
            }
            default:
                return transparent;
        }
    }
}
