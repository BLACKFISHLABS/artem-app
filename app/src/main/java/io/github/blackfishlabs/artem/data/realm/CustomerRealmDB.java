package io.github.blackfishlabs.artem.data.realm;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.List;

import io.github.blackfishlabs.artem.domain.entity.CustomerEntity;
import io.realm.Realm;
import io.realm.RealmResults;

public class CustomerRealmDB {

    private Context mContext;
    private Realm mRealm;

    @SuppressLint("StaticFieldLeak")
    private static CustomerRealmDB INSTANCE;

    public static CustomerRealmDB getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CustomerRealmDB(context);
        }
        return INSTANCE;
    }

    private CustomerRealmDB(Context context) {
        mContext = context;
        mRealm = Realm.getDefaultInstance();
    }

    public void store(final List<CustomerEntity> obj) {
        mRealm.executeTransaction(realm -> realm.insertOrUpdate(obj));
    }

    public RealmResults<CustomerEntity> getAll() {
        return mRealm.where(CustomerEntity.class).findAll();
    }

    public RealmResults<CustomerEntity> findByOwner(String id) {
        return mRealm.where(CustomerEntity.class)
                .contains("owner", id)
                .findAll();
    }

    private RealmResults<CustomerEntity> findByUUID(String id) {
        return mRealm.where(CustomerEntity.class)
                .contains("id", id)
                .findAll();
    }

    public void delete(String id) {
        mRealm.executeTransaction(realm -> findByUUID(id).deleteAllFromRealm());
    }
}
