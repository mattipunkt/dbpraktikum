package dev.numerouno.importer;

import dev.numerouno.db.Database;

/**
 * Address-Entity
 */
public class Address {
    private String street;
    private String plz;

    /**
     * Constructor initializes Address-Object
     * @param street Street-Address
     * @param plz Zip-Code
     */
    public Address(String street, String plz) {
        this.street = street;
        this.plz = plz;
    }

    /**
     * getter for street
     * @return street
     */
    public String getStreet() {
        return street;
    }

    /**
     * setter for street
     * @param street new Street
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * getter for zip code
     * @return zip code as String, because zip codes can start with 0
     */
    public String getPlz() {
        return plz;
    }

    /**
     * setter for zip code
     * @param plz new zip code
     */
    public void setPlz(String plz) {
        this.plz = plz;
    }

    /**
     * outputs address as string
     * @return address
     */
    public String toString() {
        return street + " " + plz;
    }
}
