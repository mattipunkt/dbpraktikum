package dev.numerouno.importer;

import dev.numerouno.db.Database;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Product {
    private static final Logger LOGGER = LogManager.getLogger(Product.class);

    private final String asin;
    private String name;
    private double rating;
    private int rank;
    private String image;
    private List<Product> similarProducts = new ArrayList<>();
    private String condition;
    private double price;
    private String ean;
    private int dbId = -1;

    public Product(String asin) {
        this.asin = asin;
    }

    public String getAsin() {
        return asin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }



    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Product> getSimilarProducts() {
        return similarProducts;
    }

    public void setSimilarProducts(List<Product> similarProducts) {
        this.similarProducts = similarProducts;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public int getDbId() {
        return dbId;
    }

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

    public void create(Database database, int shopId) throws IntegrityException, AlreadyExistsException {
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
                } else {
                    throw new IntegrityException("Product already present in Database, however the existing title is not null or empty. This incident will be reported...");
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
    }

    public void create(Database database) {
        this.create(database, -1);
    }
}
