package io.github.blackfishlabs.artem.data.realm;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.List;

import io.github.blackfishlabs.artem.domain.entity.AtelierInvestmentEntity;
import io.realm.Realm;
import io.realm.RealmResults;

public class AtelierInvestmentRealmDB {

    private Context mContext;
    private Realm mRealm;

    @SuppressLint("StaticFieldLeak")
    private static AtelierInvestmentRealmDB INSTANCE;

    public static AtelierInvestmentRealmDB getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AtelierInvestmentRealmDB(context);
        }
        return INSTANCE;
    }

    private AtelierInvestmentRealmDB(Context context) {
        mContext = context;
        mRealm = Realm.getDefaultInstance();
    }

    public void store(final List<AtelierInvestmentEntity> obj) {
        mRealm.executeTransaction(realm -> realm.insertOrUpdate(obj));
    }

    public RealmResults<AtelierInvestmentEntity> getAll() {
        return mRealm.where(AtelierInvestmentEntity.class).findAll();
    }

    public RealmResults<AtelierInvestmentEntity> findByOwner(String id) {
        return mRealm.where(AtelierInvestmentEntity.class)
                .contains("owner", id)
                .findAll();
    }

    public RealmResults<AtelierInvestmentEntity> findByUUID(String id) {
        return mRealm.where(AtelierInvestmentEntity.class)
                .contains("id", id)
                .findAll();
    }

    public void delete(String id) {
        mRealm.executeTransaction(realm -> findByUUID(id).deleteAllFromRealm());
    }
}
