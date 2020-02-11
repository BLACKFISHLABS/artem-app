package io.github.blackfishlabs.artem.data.realm;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.List;

import io.github.blackfishlabs.artem.domain.entity.OrderItemEntity;
import io.realm.Realm;
import io.realm.RealmResults;

public class OrderItemRealmDB {

    private Context mContext;
    private Realm mRealm;

    @SuppressLint("StaticFieldLeak")
    private static OrderItemRealmDB INSTANCE;

    public static OrderItemRealmDB getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new OrderItemRealmDB(context);
        }
        return INSTANCE;
    }

    private OrderItemRealmDB(Context context) {
        mContext = context;
        mRealm = Realm.getDefaultInstance();
    }

    public void store(final List<OrderItemEntity> obj) {
        mRealm.executeTransaction(realm -> realm.insertOrUpdate(obj));
    }

    public RealmResults<OrderItemEntity> getAll() {
        return mRealm.where(OrderItemEntity.class).findAll();
    }

    public RealmResults<OrderItemEntity> findByUUID(String id) {
        return mRealm.where(OrderItemEntity.class)
                .contains("id", id)
                .findAll();
    }

    public void delete(String id) {
        mRealm.executeTransaction(realm -> findByUUID(id).deleteAllFromRealm());
    }
}
