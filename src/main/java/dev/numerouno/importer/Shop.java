package dev.numerouno.importer;

import java.util.ArrayList;
import java.util.List;

public class Shop {
    private Address address;
    private String name;
    private String id;

    Shop(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    private List<Product> productList = new ArrayList<>();

}
