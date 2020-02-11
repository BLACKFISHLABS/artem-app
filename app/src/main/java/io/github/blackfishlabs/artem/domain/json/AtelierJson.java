package io.github.blackfishlabs.artem.domain.json;

public class AtelierJson {

    private String id;
    private String name;
    private String phoneNumber;
    private String cnpj;
    private String firebaseId;

    public String getId() {
        return id;
    }

    public AtelierJson withId(final String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public AtelierJson withName(final String name) {
        this.name = name;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public AtelierJson withPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getCnpj() {
        return cnpj;
    }

    public AtelierJson withCnpj(final String cnpj) {
        this.cnpj = cnpj;
        return this;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public AtelierJson withFirebaseId(final String firebaseId) {
        this.firebaseId = firebaseId;
        return this;
    }

}
