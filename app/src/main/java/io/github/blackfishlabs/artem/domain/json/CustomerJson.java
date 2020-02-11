package io.github.blackfishlabs.artem.domain.json;

public class CustomerJson {

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

    private String owner;

    public String getId() {
        return id;
    }

    public CustomerJson withId(final String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CustomerJson withName(final String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public CustomerJson withEmail(final String email) {
        this.email = email;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public CustomerJson withAddress(final String address) {
        this.address = address;
        return this;
    }

    public String getNumber() {
        return number;
    }

    public CustomerJson withNumber(final String number) {
        this.number = number;
        return this;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public CustomerJson withNeighborhood(final String neighborhood) {
        this.neighborhood = neighborhood;
        return this;
    }

    public String getCity() {
        return city;
    }

    public CustomerJson withCity(final String city) {
        this.city = city;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public CustomerJson withPostalCode(final String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getMainPhone() {
        return mainPhone;
    }

    public CustomerJson withMainPhone(final String mainPhone) {
        this.mainPhone = mainPhone;
        return this;
    }

    public String getComplement() {
        return complement;
    }

    public CustomerJson withComplement(final String complement) {
        this.complement = complement;
        return this;
    }

    public String getCompanyId() {
        return owner;
    }

    public CustomerJson withCompanyId(final String companyId) {
        this.owner = companyId;
        return this;
    }
}
