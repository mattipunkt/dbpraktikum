package dev.numerouno.importer;

import dev.numerouno.db.Database;

public class Address {
    private String street;
    private String plz;

    public Address(String street, String plz) {
        this.street = street;
        this.plz = plz;
    }


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String toString() {
        return street + " " + plz;
    }
}
