package io.github.blackfishlabs.artem.data.realm;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.List;

import io.github.blackfishlabs.artem.domain.entity.AtelierFixedExpenseEntity;
import io.realm.Realm;
import io.realm.RealmResults;

public class AtelierFixedExpenseRealmDB {

    private Context mContext;
    private Realm mRealm;

    @SuppressLint("StaticFieldLeak")
    private static AtelierFixedExpenseRealmDB INSTANCE;

    public static AtelierFixedExpenseRealmDB getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AtelierFixedExpenseRealmDB(context);
        }
        return INSTANCE;
    }

    private AtelierFixedExpenseRealmDB(Context context) {
        mContext = context;
        mRealm = Realm.getDefaultInstance();
    }

    public void store(final List<AtelierFixedExpenseEntity> obj) {
        mRealm.executeTransaction(realm -> realm.insertOrUpdate(obj));
    }

    public RealmResults<AtelierFixedExpenseEntity> getAll() {
        return mRealm.where(AtelierFixedExpenseEntity.class).findAll();
    }

    public RealmResults<AtelierFixedExpenseEntity> findByOwner(String id) {
        return mRealm.where(AtelierFixedExpenseEntity.class)
                .contains("owner", id)
                .findAll();
    }

    private RealmResults<AtelierFixedExpenseEntity> findByUUID(String id) {
        return mRealm.where(AtelierFixedExpenseEntity.class)
                .contains("id", id)
                .findAll();
    }

    public void delete(String id) {
        mRealm.executeTransaction(realm -> findByUUID(id).deleteAllFromRealm());
    }
}
