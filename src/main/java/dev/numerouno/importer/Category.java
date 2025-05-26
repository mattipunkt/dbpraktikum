package dev.numerouno.importer;

import dev.numerouno.db.Database;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


class Category {
    private static final Logger LOGGER = LogManager.getLogger(Category.class);

    private final String name;
    Category parent;
    List<Category> children = new ArrayList<>();
    List<Product> items = new ArrayList<>();
    private int dbId;

    Category(String name, Category parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public int getDbId() {
        if (dbId == 0) {
            LOGGER.warn("dbId is 0 for category: {}", name);
        }
        return dbId;
    }

    public void create(Database db) {
        try {
            // Prüfen, ob die Kategorie bereits existiert
            ResultSet result = db.executeQuery("SELECT kategorie_id FROM kategorie WHERE name = ?", name);
            LOGGER.log(Level.DEBUG, "Check if category exists: {}", name);

            if (!result.next()) { // Kategorie existiert nicht
                if (parent == null) {
                    LOGGER.log(Level.DEBUG, "Creating new category: {}", name);
                    this.dbId = db.executeUpdate("INSERT INTO kategorie (name) VALUES (?)", name);
                } else {
                    LOGGER.log(Level.DEBUG, "Searching for parent: {}", parent.getName());
                    ResultSet parentResult = db.executeQuery("SELECT kategorie_id FROM kategorie WHERE name = ?", parent.getName());
                    if (parentResult.next()) {
                        int parentId = parent.getDbId();
                        this.dbId = db.executeUpdate("INSERT INTO kategorie (name, oberkategorie) VALUES (?, ?)", name, parentId);
                    } else {
                        LOGGER.log(Level.ERROR, "Parent category not found: {}", parent.getName());
                        return;
                    }
                }
            } else {            // Kategorie existiert schon
               //  LOGGER.log(Level.DEBUG, "Category already exists: {}", name);
                if (parent == null) {  // Oberkategorie ist nicht vorhanden -> finde Kategorie-ID über Null
                    LOGGER.log(Level.DEBUG, "No parent, searching for Category: {}", name);
                    ResultSet categoryQuery = db.executeQuery("SELECT kategorie_id FROM kategorie WHERE name = ? AND oberkategorie IS NULL", name);
                    if (categoryQuery.next()) {
                        this.dbId = categoryQuery.getInt("kategorie_id");
                        LOGGER.log(Level.DEBUG, "Found category without parent: {} with id {}", name, dbId);
                    } else {
                        LOGGER.log(Level.DEBUG, "Category not found: {}, creating it", name);
                        this.dbId = db.executeUpdate("INSERT INTO kategorie (name) VALUES (?)", name);
                    }
                } else { // Oberkategorie vorhanden -> Suche Kategorie anhand Namen und Oberkategorie
                    LOGGER.log(Level.DEBUG, "Category already exists with parent: {}", name);
                    if (parent.getDbId() == 0) {
                        LOGGER.log(Level.ERROR, "Parent category {} has invalid dbId (0)", parent.getName());
                        return;
                    }
                    ResultSet categoryQuery = db.executeQuery("SELECT kategorie_id FROM kategorie WHERE name = ? AND oberkategorie = ?", name, parent.getDbId());
                    if (categoryQuery.next()) {
                        this.dbId = categoryQuery.getInt("kategorie_id");
                        LOGGER.log(Level.DEBUG, "Found category with parent: {} with id {}, parent id {}", name, dbId, parent.getDbId());
                    } else {
                        LOGGER.log(Level.WARN, "Category {} not found with parent id {}, creating new category", name, parent.getDbId());
                        this.dbId = db.executeUpdate("INSERT INTO kategorie (name, oberkategorie) VALUES (?, ?)", name, parent.getDbId());
                    }
                }
            }
            // Unterkategorien rekursiv erstellen
            for (Category child : children) {
                child.create(db);
                int childId = child.getDbId();
                ResultSet check = db.executeQuery(
                        "SELECT 1 FROM unterkategorie WHERE kategorie_id = ? AND unterkategorie_id = ?", dbId, childId);

                if (!check.next()) {
                    db.executeUpdate("INSERT INTO unterkategorie (kategorie_id, unterkategorie_id) VALUES (?, ?)", dbId, childId);
                } else {
                    LOGGER.log(Level.DEBUG, "Unterkategorie-Beziehung bereits vorhanden: ({}, {})", dbId, childId);
                }
            }

            // Produkte in Kategorien eintragen
            for (Product product : items) {
                int updateKey = 0;
                // Produkt existiert
                ResultSet check = db.executeQuery("SELECT produkt_id FROM produkt WHERE asin = ?", product.getAsin());
                if (check.next()) {
                    updateKey = check.getInt("produkt_id");
                } else {
                    updateKey = db.executeUpdate("INSERT INTO produkt (asin) VALUES (?)", product.getAsin());
                }
                db.executeUpdate("INSERT INTO produkt_kategorie (kategorie_id, produkt_id) VALUES (?, ?)", dbId, updateKey);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARN, "Could not process category creation for: {}", name, e);
        }
    }
}
