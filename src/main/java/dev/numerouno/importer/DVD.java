package dev.numerouno.importer;

import dev.numerouno.db.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DVD extends Product {
    private static final Logger LOGGER = LogManager.getLogger(Shop.class);

    private String format;
    private int runtime;
    private Integer regioncode;
    private List<Person> people = new ArrayList<>();

    public DVD(String asin) {
        super(asin);
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public Integer getRegioncode() {
        return regioncode;
    }

    public void setRegioncode(Integer regioncode) {
        this.regioncode = regioncode;
    }

    public List<Person> getPeople() {
        return people;
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }

    @Override
    public String toString() {
        return "DVD{" +
                "format='" + format + '\'' +
                ", runtime=" + runtime +
                ", regioncode=" + regioncode +
                ", people=" + people +
                "} " + super.toString();
    }

    @Override
    public void create(Database database, int shopId, IntegrityLogger il) throws IntegrityException, AlreadyExistsException {
        try {
            super.create(database, shopId, il);
        } catch (AlreadyExistsException e) {
            LOGGER.info("Product already exists in 'produkt' table. Proceeding with book-specific logic for ASIN {}", this.getAsin());
        } catch (IntegrityException e) {
            il.addProduct(e.toString(), this);
            LOGGER.error("Integrity issue for product {} – skipping full import", this.toString(), e);
            throw e; // Optional: Wieder hochwerfen, wenn das ein echter Fehler sein soll
        }
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM dvd WHERE produkt_id = ?", super.getDbId());
            if (rs.next()) {
                LOGGER.info("Found product as DVD in Database {}", this.toString());
                LOGGER.info("Updating...");
                try {
                    database.executeUpdate("UPDATE dvd SET format = ?, laufzeit = ?, region_code = ? WHERE produkt_id = ?",
                            this.format,
                            this.runtime,
                            this.regioncode,
                            super.getDbId()
                    );
                } catch (SQLException e) {
                    LOGGER.error("Error while updating DVD", e);
                }

            } else {
                LOGGER.info("DVD not Found, creating... {}", this.toString());
                try {
                    database.executeUpdate("INSERT INTO dvd (produkt_id, format, laufzeit, region_code) VALUES (?, ?, ?, ?)",
                            super.getDbId(),
                            this.format,
                            this.runtime,
                            this.regioncode
                    );
                } catch (SQLException e) {
                    LOGGER.error("Error while creating DVD {}", this.toString(), e);
                }
            }

            for (Person person : people) {
                person.create(database);
                try {
                    ResultSet am = database.executeQuery("SELECT * FROM dvd_beteiligte WHERE produkt_id = ? AND person_id = ?",
                            super.getDbId(),
                            person.getDbId()
                    );
                    if (am.next()) {
                        LOGGER.warn("Person-DVD-Relation is already in DB {}, {}", this.toString(), person.toString());
                    } else {
                        LOGGER.info("Creating Person-DVD-Relation {}, {}", this.toString(), person.toString());
                        try {
                            database.executeUpdate(
                                    "INSERT INTO cd_kuenstler (produkt_id, person_id) VALUES (?, ?)",
                                    this.getDbId(),
                                    person.getDbId()
                            );
                        } catch (SQLException e) {
                            LOGGER.error("Error while inserting into DVD-Person", e);
                        }
                    }
                } catch (SQLException e) {
                    LOGGER.error("Error while fetching DVD-Person-Relation", e);
                }

            }
        } catch (SQLException e) {
            LOGGER.error("Error while fetching or creating DVD", e);
        }
    }
}
