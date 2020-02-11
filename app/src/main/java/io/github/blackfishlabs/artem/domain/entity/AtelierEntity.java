package io.github.blackfishlabs.artem.domain.entity;

import com.google.gson.GsonBuilder;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class AtelierEntity implements RealmModel {

    @PrimaryKey
    private String id;

    private String name;
    private String phoneNumber;
    private String cnpj;
    private String firebaseId;

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, AtelierEntity.class);
    }

    public String getId() {
        return id;
    }

    public AtelierEntity withId(final String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public AtelierEntity withName(final String name) {
        this.name = name;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public AtelierEntity withPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getCnpj() {
        return cnpj;
    }

    public AtelierEntity withCnpj(final String cnpj) {
        this.cnpj = cnpj;
        return this;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public AtelierEntity withFirebaseId(final String firebaseId) {
        this.firebaseId = firebaseId;
        return this;
    }


}
