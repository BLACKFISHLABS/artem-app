package io.github.blackfishlabs.artem.data.realm;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.List;

import io.github.blackfishlabs.artem.data.realm.interfaces.RealmResultsSpecification;
import io.github.blackfishlabs.artem.data.realm.interfaces.Specification;
import io.github.blackfishlabs.artem.domain.entity.OrderEntity;
import io.realm.Realm;
import io.realm.RealmResults;

public class OrderRealmDB {

    private Context mContext;
    private Realm mRealm;

    @SuppressLint("StaticFieldLeak")
    private static OrderRealmDB INSTANCE;

    public static OrderRealmDB getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new OrderRealmDB(context);
        }
        return INSTANCE;
    }

    private OrderRealmDB(Context context) {
        mContext = context;
        mRealm = Realm.getDefaultInstance();
    }

    public void store(final List<OrderEntity> obj) {
        mRealm.executeTransaction(realm -> realm.insertOrUpdate(obj));
    }

    public RealmResults<OrderEntity> getAll() {
        return mRealm.where(OrderEntity.class).findAll();
    }

    public RealmResults<OrderEntity> findByOwner(String id) {
        return mRealm.where(OrderEntity.class)
                .contains("owner", id)
                .findAll();
    }

    public RealmResults<OrderEntity> findByUUID(String id) {
        return mRealm.where(OrderEntity.class)
                .contains("id", id)
                .findAll();
    }

    public void delete(String id) {
        mRealm.executeTransaction(realm -> findByUUID(id).deleteAllFromRealm());
    }

    public RealmResults<OrderEntity> query(final Specification specification) {
        //noinspection unchecked
        RealmResultsSpecification<OrderEntity> spec = (RealmResultsSpecification<OrderEntity>) specification;
        return spec.toRealmResults(mRealm);
    }
}
