package io.github.blackfishlabs.artem.data.realm.interfaces;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;

public interface RealmResultsSpecification<E extends RealmModel> extends Specification {

    RealmResults<E> toRealmResults(Realm realm);
}

