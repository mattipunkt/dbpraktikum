package dev.numerouno.importer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import dev.numerouno.db.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Shop {
    private static final Logger LOGGER = LogManager.getLogger(Shop.class);

    private Address address;
    private String name;
    private int dbId;
    private List<Product> productList = new ArrayList<>();


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

    public int getDbId() {
        return dbId;
    }

    public void setId(int id) {
        this.dbId = id;
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

    public void create(Database database) {
        try {
            ResultSet shopSet = database.executeQuery("SELECT * FROM filiale WHERE anschrift = ?", getAddress().toString());
            if (shopSet.next()) {
                this.dbId = shopSet.getInt("filiale_id");
            } else {
                this.dbId = database.executeUpdate("INSERT INTO filiale (anschrift, name) VALUES (?, ?)", getAddress().toString(), this.getName());
            }
            createShopProducts(database);
        } catch (SQLException e) {
            LOGGER.error(e);
        }
    }

    private void createShopProducts(Database database) {
        for (Product product : productList) {
            product.create(database, this.dbId);
        }
    }

}
