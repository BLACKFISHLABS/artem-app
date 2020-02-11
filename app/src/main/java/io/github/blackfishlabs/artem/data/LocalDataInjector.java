package io.github.blackfishlabs.artem.data;

import android.content.Context;

import io.github.blackfishlabs.artem.data.realm.AtelierFixedExpenseRealmDB;
import io.github.blackfishlabs.artem.data.realm.AtelierFixedExpenseRealmMapper;
import io.github.blackfishlabs.artem.data.realm.AtelierInvestmentRealmDB;
import io.github.blackfishlabs.artem.data.realm.AtelierInvestmentRealmMapper;
import io.github.blackfishlabs.artem.data.realm.AtelierRealmDB;
import io.github.blackfishlabs.artem.data.realm.AtelierRealmMapper;
import io.github.blackfishlabs.artem.data.realm.AtelierValuePerHourRealmDB;
import io.github.blackfishlabs.artem.data.realm.AtelierValuePerHourRealmMapper;
import io.github.blackfishlabs.artem.data.realm.CustomerRealmDB;
import io.github.blackfishlabs.artem.data.realm.CustomerRealmMapper;
import io.github.blackfishlabs.artem.data.realm.MaterialRealmDB;
import io.github.blackfishlabs.artem.data.realm.MaterialRealmMapper;
import io.github.blackfishlabs.artem.data.realm.OrderItemRealmDB;
import io.github.blackfishlabs.artem.data.realm.OrderItemRealmMapper;
import io.github.blackfishlabs.artem.data.realm.OrderRealmDB;
import io.github.blackfishlabs.artem.data.realm.OrderRealmMapper;
import io.github.blackfishlabs.artem.data.realm.PaymentMethodRealmDB;
import io.github.blackfishlabs.artem.data.realm.PaymentMethodRealmMapper;
import io.github.blackfishlabs.artem.data.realm.ProductRealmDB;
import io.github.blackfishlabs.artem.data.realm.ProductRealmMapper;
import io.github.blackfishlabs.artem.data.realm.interfaces.Mapper;
import io.github.blackfishlabs.artem.domain.entity.AtelierEntity;
import io.github.blackfishlabs.artem.domain.entity.AtelierFixedExpenseEntity;
import io.github.blackfishlabs.artem.domain.entity.AtelierInvestmentEntity;
import io.github.blackfishlabs.artem.domain.entity.AtelierValuePerHourEntity;
import io.github.blackfishlabs.artem.domain.entity.CustomerEntity;
import io.github.blackfishlabs.artem.domain.entity.MaterialEntity;
import io.github.blackfishlabs.artem.domain.entity.OrderEntity;
import io.github.blackfishlabs.artem.domain.entity.OrderItemEntity;
import io.github.blackfishlabs.artem.domain.entity.PaymentMethodEntity;
import io.github.blackfishlabs.artem.domain.entity.ProductEntity;
import io.github.blackfishlabs.artem.domain.json.AtelierFixedExpenseJson;
import io.github.blackfishlabs.artem.domain.json.AtelierInvestmentJson;
import io.github.blackfishlabs.artem.domain.json.AtelierJson;
import io.github.blackfishlabs.artem.domain.json.AtelierValuePerHourJson;
import io.github.blackfishlabs.artem.domain.json.CustomerJson;
import io.github.blackfishlabs.artem.domain.json.MaterialJson;
import io.github.blackfishlabs.artem.domain.json.OrderItemJson;
import io.github.blackfishlabs.artem.domain.json.OrderJson;
import io.github.blackfishlabs.artem.domain.json.PaymentMethodJson;
import io.github.blackfishlabs.artem.domain.json.ProductJson;

public class LocalDataInjector {

    private LocalDataInjector() {/* No instances */}

    /**
     * Atelier
     */
    private static Mapper<AtelierJson, AtelierEntity> atelierMapper = null;

    public static Mapper<AtelierJson, AtelierEntity> getAtelierMapper() {
        if (atelierMapper == null) {
            atelierMapper = new AtelierRealmMapper();
        }
        return atelierMapper;
    }

    public static AtelierRealmDB getAtelierRealmDB(Context context) {
        return AtelierRealmDB.getInstance(context);
    }

    /**
     * Atelier Value Per Hour
     */
    private static Mapper<AtelierValuePerHourJson, AtelierValuePerHourEntity> atelierValuePerHourMapper = null;

    public static Mapper<AtelierValuePerHourJson, AtelierValuePerHourEntity> getAtelierValuePerHourMapper() {
        if (atelierValuePerHourMapper == null) {
            atelierValuePerHourMapper = new AtelierValuePerHourRealmMapper();
        }
        return atelierValuePerHourMapper;
    }

    public static AtelierValuePerHourRealmDB getAtelierValuePerHourDB(Context context) {
        return AtelierValuePerHourRealmDB.getInstance(context);
    }

    /**
     * Atelier Investment
     */
    private static Mapper<AtelierInvestmentJson, AtelierInvestmentEntity> atelierInvestmentMapper = null;

    public static Mapper<AtelierInvestmentJson, AtelierInvestmentEntity> getAtelierInvestmentMapper() {
        if (atelierInvestmentMapper == null) {
            atelierInvestmentMapper = new AtelierInvestmentRealmMapper();
        }
        return atelierInvestmentMapper;
    }

    public static AtelierInvestmentRealmDB getAtelierInvestmentRealmDB(Context context) {
        return AtelierInvestmentRealmDB.getInstance(context);
    }

    /**
     * Atelier Fixed Expenses
     */
    private static Mapper<AtelierFixedExpenseJson, AtelierFixedExpenseEntity> atelierFixedExpenseMapper = null;

    public static Mapper<AtelierFixedExpenseJson, AtelierFixedExpenseEntity> getAtelierFixedExpenseMapper() {
        if (atelierFixedExpenseMapper == null) {
            atelierFixedExpenseMapper = new AtelierFixedExpenseRealmMapper();
        }
        return atelierFixedExpenseMapper;
    }

    public static AtelierFixedExpenseRealmDB getAtelierFixedExpenseRealmDB(Context context) {
        return AtelierFixedExpenseRealmDB.getInstance(context);
    }

    /**
     * Customers
     */
    private static Mapper<CustomerJson, CustomerEntity> customerMapper = null;

    public static Mapper<CustomerJson, CustomerEntity> getCustomerMapper() {
        if (customerMapper == null) {
            customerMapper = new CustomerRealmMapper();
        }
        return customerMapper;
    }

    public static CustomerRealmDB getCustomerRealmDB(Context context) {
        return CustomerRealmDB.getInstance(context);
    }

    /**
     * Products
     */
    private static Mapper<ProductJson, ProductEntity> productMapper = null;

    public static Mapper<ProductJson, ProductEntity> getProductMapper() {
        if (productMapper == null) {
            productMapper = new ProductRealmMapper();
        }
        return productMapper;
    }

    public static ProductRealmDB getProductRealmDB(Context context) {
        return ProductRealmDB.getInstance(context);
    }

    /**
     * Materials of Products
     */
    private static Mapper<MaterialJson, MaterialEntity> materialMapper = null;

    public static Mapper<MaterialJson, MaterialEntity> getMaterialMapper() {
        if (materialMapper == null) {
            materialMapper = new MaterialRealmMapper();
        }
        return materialMapper;
    }

    public static MaterialRealmDB getMaterialRealmDB(Context context) {
        return MaterialRealmDB.getInstance(context);
    }

    /**
     * Payment
     */
    private static Mapper<PaymentMethodJson, PaymentMethodEntity> paymentMethodMapper = null;

    public static Mapper<PaymentMethodJson, PaymentMethodEntity> getPaymentMethodMapper() {
        if (paymentMethodMapper == null) {
            paymentMethodMapper = new PaymentMethodRealmMapper();
        }
        return paymentMethodMapper;
    }

    public static PaymentMethodRealmDB getPaymentMethodRealmDB(Context context) {
        return PaymentMethodRealmDB.getInstance(context);
    }

    /**
     * Order
     */
    private static Mapper<OrderJson, OrderEntity> orderMapper = null;

    public static Mapper<OrderJson, OrderEntity> getOrderMapper() {
        if (orderMapper == null) {
            orderMapper = new OrderRealmMapper();
        }
        return orderMapper;
    }

    public static OrderRealmDB getOrderRealmDB(Context context) {
        return OrderRealmDB.getInstance(context);
    }

    /**
     * Order Item
     */
    private static Mapper<OrderItemJson, OrderItemEntity> orderItemMapper = null;

    public static Mapper<OrderItemJson, OrderItemEntity> getOrderItemMapper() {
        if (orderItemMapper == null) {
            orderItemMapper = new OrderItemRealmMapper();
        }
        return orderItemMapper;
    }

    public static OrderItemRealmDB getOrderItemRealmDB(Context context) {
        return OrderItemRealmDB.getInstance(context);
    }
}