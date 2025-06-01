package dev.numerouno.importer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.event.ExceptionEvent;
import dev.numerouno.db.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Repräsentiert eine Filiale (Shop) mit Namen, Adresse und einer Liste von Produkten.
 * Ermöglicht das Erstellen der Filiale und ihrer Produkte in der Datenbank.
 */
public class Shop {
    private static final Logger LOGGER = LogManager.getLogger(Shop.class);

    private Address address;
    private String name;
    private int dbId;
    private List<Product> productList = new ArrayList<>();



    /**
     * Konstruktor für eine Shop-Instanz mit Name und Adresse.
     *
     * @param name    Der Name der Filiale.
     * @param address Die Adresse der Filiale.
     */
    Shop(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    /**
     * Gibt die Liste der Produkte dieser Filiale zurück.
     *
     * @return Liste der Produkte.
     */
    public List<Product> getProductList() {
        return productList;
    }

    /**
     * Setzt die Produktliste der Filiale.
     *
     * @param productList Liste der Produkte.
     */
    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    /**
     * Gibt die Datenbank-ID der Filiale zurück.
     *
     * @return Datenbank-ID.
     */
    public int getDbId() {
        return dbId;
    }


    /**
     * Setzt die Datenbank-ID der Filiale.
     *
     * @param id Datenbank-ID.
     */
    public void setId(int id) {
        this.dbId = id;
    }

    /**
     * Gibt den Namen der Filiale zurück.
     *
     * @return Name der Filiale.
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Namen der Filiale.
     *
     * @param name Name der Filiale.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gibt die Adresse der Filiale zurück.
     *
     * @return Adresse der Filiale.
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Setzt die Adresse der Filiale.
     *
     * @param address Adresse der Filiale.
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Erstellt die Filiale in der Datenbank, falls sie noch nicht existiert, und speichert die Datenbank-ID.
     * Anschließend werden die Produkte dieser Filiale in der Datenbank angelegt.
     *
     * @param database Die Datenbankverbindung.
     * @param il       Der IntegrityLogger zur Protokollierung von Fehlern.
     */
    public void create(Database database, IntegrityLogger il) {
        try {
            ResultSet shopSet = database.executeQuery("SELECT * FROM filiale WHERE anschrift = ?", getAddress().toString());
            if (shopSet.next()) {
                this.dbId = shopSet.getInt("filiale_id");
            } else {
                this.dbId = database.executeUpdate("INSERT INTO filiale (anschrift, name) VALUES (?, ?)", getAddress().toString(), this.getName());
            }
            createShopProducts(database, il);
        } catch (SQLException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Legt alle Produkte der Filiale in der Datenbank an.
     * Fehler beim Anlegen einzelner Produkte werden protokolliert und behandelt.
     *
     * @param database Die Datenbankverbindung.
     * @param il       Der IntegrityLogger zur Protokollierung von Fehlern.
     */
    private void createShopProducts(Database database, IntegrityLogger il) {
        for (Product product : productList) {
            try {
                product.create(database, this.dbId, il);
            } catch (IntegrityException e) {
                il.addError(IntegrityLogger.ErrorType.INTEGRITY_CONFLICT, e + product.toString());
                LOGGER.error("Could not create Product {}, {}", e, product.toString());
            } catch (AlreadyExistsException f) {
                il.addError(IntegrityLogger.ErrorType.DUPLICATE_ENTRY, f + product.toString());
                LOGGER.info("Product {} already exists... Skipping", product.toString());
            } catch (Exception e) {
                LOGGER.error(e);
            }

        }
    }
}
