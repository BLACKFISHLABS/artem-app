package io.github.blackfishlabs.artem.data.realm;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class RealmMigrationImpl implements RealmMigration {

    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, final long newVersion) {
        final RealmSchema realmSchema = realm.getSchema();
    }
}