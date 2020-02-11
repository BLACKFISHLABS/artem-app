package io.github.blackfishlabs.artem.data.realm;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.List;

import io.github.blackfishlabs.artem.domain.entity.PaymentMethodEntity;
import io.realm.Realm;
import io.realm.RealmResults;

public class PaymentMethodRealmDB {

    private Context mContext;
    private Realm mRealm;

    @SuppressLint("StaticFieldLeak")
    private static PaymentMethodRealmDB INSTANCE;

    public static PaymentMethodRealmDB getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new PaymentMethodRealmDB(context);
        }
        return INSTANCE;
    }

    private PaymentMethodRealmDB(Context context) {
        mContext = context;
        mRealm = Realm.getDefaultInstance();
    }

    public void store(final List<PaymentMethodEntity> obj) {
        mRealm.executeTransaction(realm -> realm.insertOrUpdate(obj));
    }

    public RealmResults<PaymentMethodEntity> getAll() {
        return mRealm.where(PaymentMethodEntity.class).findAll();
    }

    public RealmResults<PaymentMethodEntity> findByOwner(String id) {
        return mRealm.where(PaymentMethodEntity.class)
                .contains("owner", id)
                .findAll();
    }

    public RealmResults<PaymentMethodEntity> findByUUID(String id) {
        return mRealm.where(PaymentMethodEntity.class)
                .contains("id", id)
                .findAll();
    }

    public void delete(String id) {
        mRealm.executeTransaction(realm -> findByUUID(id).deleteAllFromRealm());
    }
}
