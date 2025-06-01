package dev.numerouno.importer;

import dev.numerouno.db.Database;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert ein Produkt mit zugehörigen Eigenschaften wie ASIN, Name, Bewertung, Preis usw.
 * Diese Klasse bietet Methoden zum Erstellen und Verwalten von Produkten in der Datenbank
 * sowie zur Pflege von Beziehungen zu ähnlichen Produkten und Filialen.
 */
public class Product {
    private static final Logger LOGGER = LogManager.getLogger(Product.class);

    private final String asin;
    private String name;
    private double rating;
    private Integer rank;
    private String image;
    private List<Product> similarProducts = new ArrayList<>();
    private String condition;
    private double price;
    private String ean;
    private int dbId = -1;

    /**
     * Erstellt ein neues Produkt mit der angegebenen ASIN.
     *
     * @param asin Die ASIN (Amazon Standard Identification Number) des Produkts.
     */
    public Product(String asin) {
        this.asin = asin;
    }

    /**
     * Gibt die ASIN des Produkts zurück.
     *
     * @return Die ASIN.
     */
    public String getAsin() {
        return asin;
    }

    /**
     * Gibt den Namen des Produkts zurück.
     *
     * @return Der Produktname.
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Namen des Produkts.
     *
     * @param name Der Produktname.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gibt die Bewertung (Rating) des Produkts zurück.
     *
     * @return Die Bewertung als double.
     */
    public double getRating() {
        return rating;
    }

    /**
     * Setzt die Bewertung (Rating) des Produkts.
     *
     * @param rating Die Bewertung.
     */
    public void setRating(double rating) {
        this.rating = rating;
    }


    /**
     * Gibt den Verkaufsrang (Rank) des Produkts zurück.
     *
     * @return Der Verkaufsrang als Integer, kann null sein.
     */
    public Integer getRank() {
        return rank;
    }

    /**
     * Setzt den Verkaufsrang (Rank) des Produkts.
     *
     * @param rank Der Verkaufsrang.
     */
    public void setRank(Integer rank) {
        this.rank = rank;
    }

    /**
     * Gibt den Link oder Pfad zum Bild des Produkts zurück.
     *
     * @return Der Bild-Link oder Pfad.
     */
    public String getImage() {
        return image;
    }

    /**
     * Setzt den Bild-Link oder Pfad des Produkts.
     *
     * @param image Der Bild-Link oder Pfad.
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Gibt die Liste der ähnlichen Produkte zurück.
     *
     * @return Liste ähnlicher Produkte.
     */
    public List<Product> getSimilarProducts() {
        return similarProducts;
    }

    /**
     * Setzt die Liste der ähnlichen Produkte.
     *
     * @param similarProducts Liste ähnlicher Produkte.
     */
    public void setSimilarProducts(List<Product> similarProducts) {
        this.similarProducts = similarProducts;
    }

    /**
     * Gibt den Zustand des Produkts zurück (z.B. neu, gebraucht).
     *
     * @return Der Zustand des Produkts.
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Setzt den Zustand des Produkts (z.B. neu, gebraucht).
     *
     * @param condition Der Zustand des Produkts.
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * Gibt den Preis des Produkts zurück.
     *
     * @return Der Preis als double.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Setzt den Preis des Produkts.
     *
     * @param price Der Preis.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gibt die EAN (European Article Number) des Produkts zurück.
     *
     * @return Die EAN als String.
     */
    public String getEan() {
        return ean;
    }

    /**
     * Setzt die EAN (European Article Number) des Produkts.
     *
     * @param ean Die EAN.
     */
    public void setEan(String ean) {
        this.ean = ean;
    }

    /**
     * Gibt die Datenbank-ID des Produkts zurück.
     *
     * @return Die Datenbank-ID, -1 falls noch nicht gespeichert.
     */
    public int getDbId() {
        return dbId;
    }


    /**
     * Setzt die Datenbank-ID des Produkts.
     *
     * @param dbId Die Datenbank-ID.
     */
    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    @Override
    public String toString() {
        return "Product{" +
                "asin='" + asin + '\'' +
                ", name='" + name + '\'' +
                ", rating=" + rating +
                ", rank=" + rank +
                ", image='" + image + '\'' +
                ", similarProducts=" + similarProducts.toString() +
                ", condition='" + condition + '\'' +
                ", price=" + price +
                ", ean='" + ean + '\'' +
                '}';
    }

    /**
     * Erstellt das Produkt in der Datenbank oder aktualisiert es, falls bereits vorhanden.
     * Außerdem wird die Beziehung zu einer Filiale und ähnlichen Produkten gepflegt.
     *
     * @param database Die Datenbankinstanz für SQL-Operationen.
     * @param shopId   Die ID der Filiale, in der das Produkt geführt wird. -1 falls keine Filiale.
     * @param il       Der IntegrityLogger zur Erfassung von Fehlern bei der Datenintegrität.
     * @throws IntegrityException    Wenn Inkonsistenzen bei bestehenden Datensätzen entdeckt werden.
     * @throws AlreadyExistsException Wenn versucht wird, doppelte Einträge anzulegen.
     */
    public void create(Database database, int shopId, IntegrityLogger il) throws IntegrityException, AlreadyExistsException {
        try {
            Double preis;
            if (this.price == -1) {
                preis = null;
            } else {
                preis = this.price;
            }
            ResultSet product = database.executeQuery("SELECT * FROM produkt WHERE asin = ?", this.asin);
            if (product.next()) {
                LOGGER.log(Level.INFO, "Fetched Product with ASIN {} ", this.asin);
                if (this.name == null) {
                    this.dbId = product.getInt("produkt_id");
                } else {
                    String titl = product.getString("titel");
                    boolean stringIsNull = titl == null || titl.isEmpty();
                    if (stringIsNull) {
                        try {
                            this.dbId = database.executeUpdate("UPDATE produkt SET titel = ?, rating = ?, bild = ?, verkaufsrang = ? WHERE asin = ?", this.name, this.rating, this.image, this.rank, this.asin);
                        } catch (SQLException e) {
                            LOGGER.error("Error while updating product {}", this.toString(), e);
                        }
                    } else if (titl.equals(this.name)) {
                        this.dbId = product.getInt("produkt_id");
                        LOGGER.warn("Product already exists. Skipping...");
                        throw new AlreadyExistsException("Product already exists");
                    } else {
                        throw new IntegrityException("Product already present in Database, however the existing title is not null or empty. This incident will be reported...");
                    }
                }
            } else {
                LOGGER.log(Level.INFO, "Could not fetch product with ASIN {}", this.asin);
                LOGGER.log(Level.INFO, "Creating new product with ASIN {}", this.asin);
                try {
                    this.dbId = database.executeUpdate("INSERT INTO produkt (asin, titel, rating, bild, verkaufsrang) VALUES (?, ?, ?, ?, ?)", this.asin, this.name, this.rating, this.image, this.rank);
                } catch (SQLException e) {
                    LOGGER.error("Error while inserting product {}", this.toString(), e);
                }
            }
            if (dbId != -1 && shopId != -1) {
                LOGGER.log(Level.DEBUG, "Importing Product into FilialProdukte relation");
                try {
                    database.executeUpdate("INSERT INTO filial_produkte (filiale_id, produkt_id, preis, zustand) VALUES (?, ?, ?, ?)", shopId, this.dbId, preis, this.condition);
                } catch (SQLException e) {
                    if (e.getSQLState().equals("23505")) {
                        throw new AlreadyExistsException("Relation already exists in FilialProdukte");
                    } else {
                        LOGGER.error("Error while inserting Produkte to FilialProdukte", e);
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "Could not fetch or create existing Product {}, {}", this.toString(), e);
        }

        for (Product similarProduct : similarProducts) {
            System.out.println("adding to similars: " + similarProduct.toString());
            try {
                similarProduct.create(database, il);
                try {
                    ResultSet rs = database.executeQuery("SELECT * FROM aehnliche_produkte WHERE produkt_id = ? AND aehnliches_produkt_id = ?", this.dbId, similarProduct.getDbId());
                    if(rs.next()) {
                        throw new AlreadyExistsException("Relation already exists in AehnlicheProdukte");
                    } else {
                        try {
                            database.executeUpdate("INSERT INTO aehnliche_produkte (produkt_id, aehnliches_produkt_id) VALUES (?, ?)", this.dbId, similarProduct.getDbId());
                        } catch (SQLException e) {
                            LOGGER.error("Error while inserting Produkte to AehnlicheProdukte", e);
                        }
                    }
                } catch (SQLException e) {
                    LOGGER.error("Error while fetching or creating aehnliches_produkt");
                }
            } catch (AlreadyExistsException e) {
                il.addError(IntegrityLogger.ErrorType.DUPLICATE_ENTRY, "Relation already exists: Aehnliche Produkte" + this.toString()+ similarProduct.toString());
            }
        }



    }

    /**
     * Erstellt das Produkt in der Datenbank ohne Filialbezug.
     * Intern wird {@link #create(Database, int, IntegrityLogger)} mit shopId = -1 aufgerufen.
     *
     * @param database Die Datenbankinstanz für SQL-Operationen.
     * @param il       Der IntegrityLogger zur Erfassung von Fehlern bei der Datenintegrität.
     */
    public void create(Database database, IntegrityLogger il) {
        this.create(database, -1,il);
    }
}
