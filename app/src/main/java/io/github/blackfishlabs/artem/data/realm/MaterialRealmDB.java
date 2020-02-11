package io.github.blackfishlabs.artem.data.realm;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.List;

import io.github.blackfishlabs.artem.domain.entity.MaterialEntity;
import io.realm.Realm;
import io.realm.RealmResults;

public class MaterialRealmDB {

    private Context mContext;
    private Realm mRealm;

    @SuppressLint("StaticFieldLeak")
    private static MaterialRealmDB INSTANCE;

    public static MaterialRealmDB getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new MaterialRealmDB(context);
        }
        return INSTANCE;
    }

    private MaterialRealmDB(Context context) {
        mContext = context;
        mRealm = Realm.getDefaultInstance();
    }

    public void store(final List<MaterialEntity> obj) {
        mRealm.executeTransaction(realm -> realm.insertOrUpdate(obj));
    }

    public RealmResults<MaterialEntity> getAll() {
        return mRealm.where(MaterialEntity.class).findAll();
    }

    public RealmResults<MaterialEntity> findByOwner(String id) {
        return mRealm.where(MaterialEntity.class)
                .contains("owner", id)
                .findAll();
    }

    public RealmResults<MaterialEntity> findByUUID(String id) {
        return mRealm.where(MaterialEntity.class)
                .contains("id", id)
                .findAll();
    }

    public void delete(String id) {
        mRealm.executeTransaction(realm -> findByUUID(id).deleteAllFromRealm());
    }
}
