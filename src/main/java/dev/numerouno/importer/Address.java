package dev.numerouno.importer;

public class Address {
    private String street;
    private int plz;

    public Address(String street, int plz) {
        this.street = street;
        this.plz = plz;
    }


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getPlz() {
        return plz;
    }

    public void setPlz(int plz) {
        this.plz = plz;
    }
}
