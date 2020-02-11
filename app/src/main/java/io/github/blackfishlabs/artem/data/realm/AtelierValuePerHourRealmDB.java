package io.github.blackfishlabs.artem.data.realm;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.List;

import io.github.blackfishlabs.artem.domain.entity.AtelierValuePerHourEntity;
import io.realm.Realm;
import io.realm.RealmResults;

public class AtelierValuePerHourRealmDB {

    private Context mContext;
    private Realm mRealm;

    @SuppressLint("StaticFieldLeak")
    private static AtelierValuePerHourRealmDB INSTANCE;

    public static AtelierValuePerHourRealmDB getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AtelierValuePerHourRealmDB(context);
        }
        return INSTANCE;
    }

    private AtelierValuePerHourRealmDB(Context context) {
        mContext = context;
        mRealm = Realm.getDefaultInstance();
    }

    public void store(final List<AtelierValuePerHourEntity> obj) {
        mRealm.executeTransaction(realm -> realm.insertOrUpdate(obj));
    }

    public RealmResults<AtelierValuePerHourEntity> getAll() {
        return mRealm.where(AtelierValuePerHourEntity.class).findAll();
    }

    public RealmResults<AtelierValuePerHourEntity> findByOwner(String id) {
        return mRealm.where(AtelierValuePerHourEntity.class)
                .contains("owner", id)
                .findAll();
    }
}
