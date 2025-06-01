package dev.numerouno.importer;

import dev.numerouno.db.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse {@code DVD} repräsentiert ein DVD-Produkt und erweitert die allgemeine {@link Product}-Klasse.
 * Sie enthält zusätzliche Informationen wie Format, Laufzeit, Regioncode und beteiligte Personen.
 *
 * Diese Klasse übernimmt auch die Persistierung der DVD-spezifischen Daten sowie der beteiligten Personen
 * in der Datenbank, einschließlich der Zuordnungstabellen.
 */
public class DVD extends Product {
    private static final Logger LOGGER = LogManager.getLogger(Shop.class);

    private String format;
    private int runtime;
    private Integer regioncode;
    private List<Person> people = new ArrayList<>();

    /**
     * Konstruktor zur Erstellung einer neuen DVD mit gegebener ASIN.
     *
     * @param asin Die ASIN (Amazon Standard Identification Number) des Produkts.
     */
    public DVD(String asin) {
        super(asin);
    }

    /**
     * Gibt das Format der DVD zurück (z.B. "Blu-ray", "DVD").
     *
     * @return Das Format der DVD.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Setzt das Format der DVD.
     *
     * @param format Das neue Format.
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Gibt die Laufzeit der DVD in Minuten zurück.
     *
     * @return Die Laufzeit.
     */
    public int getRuntime() {
        return runtime;
    }

    /**
     * Setzt die Laufzeit der DVD.
     *
     * @param runtime Die neue Laufzeit in Minuten.
     */
    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    /**
     * Gibt den Regioncode der DVD zurück.
     *
     * @return Der Regioncode oder {@code null}, wenn keiner gesetzt ist.
     */
    public Integer getRegioncode() {
        return regioncode;
    }

    /**
     * Setzt den Regioncode der DVD.
     *
     * @param regioncode Der neue Regioncode.
     */
    public void setRegioncode(Integer regioncode) {
        this.regioncode = regioncode;
    }

    /**
     * Gibt die Liste der an der DVD beteiligten Personen zurück.
     *
     * @return Liste von {@link Person}-Objekten.
     */
    public List<Person> getPeople() {
        return people;
    }

    /**
     * Setzt die Liste der beteiligten Personen.
     *
     * @param people Neue Liste von Personen.
     */
    public void setPeople(List<Person> people) {
        this.people = people;
    }

    /**
     * Gibt eine textuelle Repräsentation der DVD inklusive ihrer Eigenschaften zurück.
     *
     * @return String-Repräsentation der DVD.
     */
    @Override
    public String toString() {
        return "DVD{" +
                "format='" + format + '\'' +
                ", runtime=" + runtime +
                ", regioncode=" + regioncode +
                ", people=" + people +
                "} " + super.toString();
    }


    /**
     * Persistiert das DVD-Objekt in der Datenbank. Dabei wird geprüft, ob der Eintrag bereits existiert
     * und ggf. aktualisiert. Auch beteiligte Personen sowie deren Beziehungen zur DVD werden gespeichert.
     *
     * @param database Die Datenbankverbindung.
     * @param shopId   Die ID des Shops, zu dem das Produkt gehört.
     * @param il       Der {@link IntegrityLogger}, um Konsistenzfehler zu protokollieren.
     * @throws IntegrityException        Bei logischen Inkonsistenzen in den Daten.
     * @throws AlreadyExistsException    Wenn das Produkt bereits existiert.
     */
    @Override
    public void create(Database database, int shopId, IntegrityLogger il) throws IntegrityException, AlreadyExistsException {
        try {
            super.create(database, shopId, il);
        } catch (AlreadyExistsException e) {
            il.addError(IntegrityLogger.ErrorType.DUPLICATE_ENTRY, e + this.toString());
            LOGGER.info("Product already exists in 'produkt' table. Proceeding with book-specific logic for ASIN {}", this.getAsin());
        } catch (IntegrityException e) {
            il.addError(IntegrityLogger.ErrorType.INTEGRITY_CONFLICT, e.toString() +  this.toString());
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
                try {
                    person.create(database);
                } catch (AlreadyExistsException e) {
                    il.addError(IntegrityLogger.ErrorType.DUPLICATE_ENTRY, e + this.toString() + person.toString());
                }
                try {
                    ResultSet am = database.executeQuery("SELECT * FROM dvd_beteiligte WHERE produkt_id = ? AND person_id = ?",
                            super.getDbId(),
                            person.getDbId()
                    );
                    if (am.next()) {
                        il.addError(IntegrityLogger.ErrorType.DUPLICATE_ENTRY, "PersonDVD-Relation already exists" + this.toString() + person.toString());
                        LOGGER.warn("Person-DVD-Relation is already in DB {}, {}", this.toString(), person.toString());
                    } else {
                        LOGGER.info("Creating Person-DVD-Relation {}, {}", this.toString(), person.toString());
                        try {
                            database.executeUpdate(
                                    "INSERT INTO dvd_beteiligte (produkt_id, person_id) VALUES (?, ?)",
                                    this.getDbId(),
                                    person.getDbId()
                            );
                        } catch (SQLException e) {
                            if (e.getSQLState().equals("23505")) {
                                LOGGER.warn("Person-DVD-Relation already exists {}, {}", this.toString(), person.toString());
                                il.addError(IntegrityLogger.ErrorType.DUPLICATE_ENTRY, "PersonDVD-Relation already exists" + this.toString() + person.toString());
                            } else {
                                LOGGER.error("Error while inserting into DVD-Person", e);

                            }
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
