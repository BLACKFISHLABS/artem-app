package io.github.blackfishlabs.artem.data.realm;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.List;

import io.github.blackfishlabs.artem.domain.entity.AtelierEntity;
import io.realm.Realm;
import io.realm.RealmResults;

public class AtelierRealmDB {

    private Context mContext;
    private Realm mRealm;

    @SuppressLint("StaticFieldLeak")
    private static AtelierRealmDB INSTANCE;

    public static AtelierRealmDB getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AtelierRealmDB(context);
        }
        return INSTANCE;
    }

    private AtelierRealmDB(Context context) {
        mContext = context;
        mRealm = Realm.getDefaultInstance();
    }

    public void store(final List<AtelierEntity> obj) {
        mRealm.executeTransaction(realm -> realm.insertOrUpdate(obj));
    }

    public RealmResults<AtelierEntity> getAll() {
        return mRealm.where(AtelierEntity.class).findAll();
    }

    public RealmResults<AtelierEntity> findById(String id) {
        return mRealm.where(AtelierEntity.class)
                .contains("id", id)
                .findAll();
    }

//    public void deleteTable() {
//        mRealm.executeTransaction(realm -> realm.delete(AtelierEntity.class));
//    }
//
//    public void closeDB() {
//        mRealm.close();
//    }
}
