package io.github.blackfishlabs.artem.data.realm;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.List;

import io.github.blackfishlabs.artem.domain.entity.ProductEntity;
import io.realm.Realm;
import io.realm.RealmResults;

public class ProductRealmDB {

    private Context mContext;
    private Realm mRealm;

    @SuppressLint("StaticFieldLeak")
    private static ProductRealmDB INSTANCE;

    public static ProductRealmDB getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ProductRealmDB(context);
        }
        return INSTANCE;
    }

    private ProductRealmDB(Context context) {
        mContext = context;
        mRealm = Realm.getDefaultInstance();
    }

    public void store(final List<ProductEntity> obj) {
        mRealm.executeTransaction(realm -> realm.insertOrUpdate(obj));
    }

    public RealmResults<ProductEntity> getAll() {
        return mRealm.where(ProductEntity.class).findAll();
    }

    public RealmResults<ProductEntity> findByOwner(String id) {
        return mRealm.where(ProductEntity.class)
                .contains("owner", id)
                .findAll();
    }

    private RealmResults<ProductEntity> findByUUID(String id) {
        return mRealm.where(ProductEntity.class)
                .contains("id", id)
                .findAll();
    }

    public void delete(String id) {
        mRealm.executeTransaction(realm -> findByUUID(id).deleteAllFromRealm());
    }
}
