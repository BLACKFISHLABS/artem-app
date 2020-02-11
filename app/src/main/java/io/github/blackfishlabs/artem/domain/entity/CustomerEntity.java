package io.github.blackfishlabs.artem.domain.entity;

import com.google.gson.GsonBuilder;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass
public class CustomerEntity implements RealmModel {

    @PrimaryKey
    private String id;

    private String name;
    private String email;
    private String address;
    private String number;
    private String neighborhood;
    private String city;
    private String postalCode;
    private String mainPhone;
    private String complement;
    private String cpfOrCnpj;

    @Required
    private String owner;

    public static final class Fields {
        public static final String ID = "id";
        public static final String NAME = "name";
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, CustomerEntity.class);
    }

    public String getId() {
        return id;
    }

    public CustomerEntity withId(final String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CustomerEntity withName(final String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public CustomerEntity withEmail(final String email) {
        this.email = email;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public CustomerEntity withAddress(final String address) {
        this.address = address;
        return this;
    }

    public String getNumber() {
        return number;
    }

    public CustomerEntity withNumber(final String number) {
        this.number = number;
        return this;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public CustomerEntity withNeighborhood(final String neighborhood) {
        this.neighborhood = neighborhood;
        return this;
    }

    public String getCity() {
        return city;
    }

    public CustomerEntity withCity(final String city) {
        this.city = city;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public CustomerEntity withPostalCode(final String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getMainPhone() {
        return mainPhone;
    }

    public CustomerEntity withMainPhone(final String mainPhone) {
        this.mainPhone = mainPhone;
        return this;
    }

    public String getComplement() {
        return complement;
    }

    public CustomerEntity withComplement(final String complement) {
        this.complement = complement;
        return this;
    }

    public String getCompanyId() {
        return owner;
    }

    public CustomerEntity withCompanyId(final String companyId) {
        this.owner = companyId;
        return this;
    }

    public String getCpfOrCnpj() {
        return cpfOrCnpj;
    }

    public CustomerEntity withCpfOrCnpjfinal(String cpfOrCnpj) {
        this.cpfOrCnpj = cpfOrCnpj;
        return this;
    }


}
