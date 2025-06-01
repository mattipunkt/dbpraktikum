package dev.numerouno.importer;

import dev.numerouno.db.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert ein Buchprodukt, das zusätzliche Informationen wie ISBN, Seitenzahl, Erscheinungsdatum,
 * Verlag(e), Autor(en) und ob es sich um ein Hörbuch handelt, enthält.
 *
 * Diese Klasse erweitert die {@link Product}-Klasse und implementiert die Methode {@code create}, um
 * Buch-spezifische Daten in die Datenbank zu importieren.
 */
public class Book extends Product {
    private static final Logger LOGGER = LogManager.getLogger(Book.class);

    private List<Verlag> verlag = new ArrayList<>();
    private int pages;
    private String releasedate;
    private String isbn;
    private List<Person> people = new ArrayList<>();
    private boolean audiobook = false;

    /**
     * Gibt an, ob das Buch ein Hörbuch ist.
     *
     * @return {@code true}, wenn es sich um ein Hörbuch handelt, sonst {@code false}
     */
    public boolean isAudiobook() {
        return audiobook;
    }

    /**
     * Setzt den Wert, ob es sich bei dem Buch um ein Hörbuch handelt.
     *
     * @param audiobook {@code true}, wenn es ein Hörbuch ist, sonst {@code false}
     */
    public void setAudiobook(boolean audiobook) {
        this.audiobook = audiobook;
    }

    /**
     * Konstruktor für ein Buch mit der gegebenen ASIN.
     *
     * @param asin Die ASIN (Amazon Standard Identification Number) des Buches
     */
    public Book(String asin) {
        super(asin);
    }

    /**
     * Gibt die Liste der zugehörigen Verlage zurück.
     *
     * @return Liste von {@link Verlag} Objekten
     */
    public List<Verlag> getVerlag() {
        return verlag;
    }

    /**
     * Setzt die Liste der Verlage.
     *
     * @param verlag Liste von {@link Verlag} Objekten
     */
    public void setVerlag(List<Verlag> verlag) {
        this.verlag = verlag;
    }

    /**
     * Gibt die Seitenanzahl des Buches zurück.
     *
     * @return Seitenanzahl
     */
    public int getPages() {
        return pages;
    }


    /**
     * Setzt die Seitenanzahl des Buches.
     *
     * @param pages Seitenanzahl
     */
    public void setPages(int pages) {
        this.pages = pages;
    }

    /**
     * Gibt das Erscheinungsdatum des Buches als String zurück.
     *
     * @return Erscheinungsdatum im ISO-Format (yyyy-MM-dd)
     */
    public String getDate() {
        return releasedate;
    }

    /**
     * Setzt das Erscheinungsdatum des Buches.
     *
     * @param date Erscheinungsdatum im ISO-Format (yyyy-MM-dd)
     */
    public void setDate(String date) {
        this.releasedate = date;
    }

    /**
     * Gibt das Erscheinungsdatum als {@link LocalDate}-Objekt zurück.
     *
     * @return Erscheinungsdatum als {@link LocalDate} oder {@code null}, wenn nicht gesetzt
     */
    private LocalDate getDateAsObject() {
        if (releasedate == null) {
            return null;
        }
        return LocalDate.parse(releasedate);
    }

    /**
     * Gibt die ISBN des Buches zurück.
     *
     * @return ISBN als String
     */
    public String getIsbn() {
        return isbn;
    }


    /**
     * Setzt die ISBN des Buches.
     *
     * @param isbn ISBN als String
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gibt die Liste der Autoren zurück.
     *
     * @return Liste von {@link Person} Objekten
     */
    public List<Person> getAuthors() {
        return people;
    }

    /**
     * Setzt die Liste der Autoren.
     *
     * @param authors Liste von {@link Person} Objekten
     */
    public void setAuthors(List<Person> authors) {
        this.people = authors;
    }


    /**
     * Erstellt oder aktualisiert den Buchdatensatz in der Datenbank. Es wird geprüft, ob der Datensatz
     * bereits existiert, und entsprechend entweder ein Update oder ein Insert durchgeführt.
     * Zusätzlich werden Relationen zu Verlagen und Autoren gepflegt.
     *
     * @param database Datenbankverbindung
     * @param shopId   ID des Shops
     * @param il       Logger für Integritätsfehler
     * @throws IntegrityException wenn ein Integritätskonflikt auftritt
     * @throws AlreadyExistsException wenn das Produkt bereits existiert
     */
    @Override
    public void create(Database database, int shopId, IntegrityLogger il) throws IntegrityException, AlreadyExistsException{
        try {
            super.create(database, shopId, il);
        } catch (AlreadyExistsException e) {
            il.addError(IntegrityLogger.ErrorType.DUPLICATE_ENTRY, e + this.toString());
            LOGGER.info("Product already exists in 'produkt' table. Proceeding with book-specific logic for ASIN {}", this.getAsin());
        } catch (IntegrityException e) {
            il.addError(IntegrityLogger.ErrorType.INTEGRITY_CONFLICT, e + this.toString());
            LOGGER.error("Integrity issue for product {} – skipping full import", this.toString(), e);
            throw e; // Optional: Wieder hochwerfen, wenn das ein echter Fehler sein soll
        }
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM buch WHERE produkt_id = ?", super.getDbId());
            if (rs.next()) {
                LOGGER.warn("Book already exists. Updating... Book: {}", this.toString());
                try {
                    database.executeUpdate("UPDATE buch SET seitenzahl = ?, erscheinungsdatum = ? WHERE produkt_id = ?",
                            this.pages,
                            this.getDateAsObject(),
                            this.getDbId()
                    );
                } catch (SQLException e) {
                    LOGGER.error("Error while updating book", e);
                }
            } else {
                LOGGER.info("Book does not exist. Creating new book... {}", this.toString());
                try {
                    database.executeUpdate("INSERT INTO buch (produkt_id, seitenzahl, erscheinungsdatum, isbn) VALUES (?, ?, ?, ?)",
                            super.getDbId(),
                            this.pages,
                            this.getDateAsObject(),
                            this.isbn
                    );
                } catch (SQLException e) {
                    LOGGER.error("Error while creating new book", e);
                }

            }

            // buch_verlag-relation
            for (Verlag verlag : verlag) {
                try {
                    verlag.create(database);
                } catch (AlreadyExistsException e) {
                    il.addError(IntegrityLogger.ErrorType.DUPLICATE_ENTRY, e + this.toString() + verlag.toString());
                }
                try {
                    ResultSet bv = database.executeQuery("SELECT * FROM buch_verlag WHERE produkt_id = ? AND verlag_id = ?", super.getDbId(), verlag.getDbId());
                    if (!bv.next()) {
                        LOGGER.info("Creating Book_Verlag-Relation for Book {} and Verlag {}", this.toString(), verlag.toString());
                        try {
                            database.executeUpdate("INSERT INTO buch_verlag (produkt_id, verlag_id) VALUES (?, ?)", super.getDbId(), verlag.getDbId());
                        } catch (SQLException e) {
                            if (e.getSQLState().equals("23505")) {
                                il.addError(IntegrityLogger.ErrorType.DUPLICATE_ENTRY, "BookVerlag-Relation already exists" + this.toString() + verlag.toString());
                                LOGGER.warn("BookVerlag-Relation for Book {} and Verlag {} already exists", this.toString(), verlag.toString());
                            } else {
                                LOGGER.error("Error while inserting Book_Verlag-Relation for Book {} and Verlag {}", this.toString(), verlag.toString());
                            }
                        }
                    } else {
                        LOGGER.warn("Book-Verlag-Relation already exists. Doing nothing...");
                    }
                } catch (SQLException e) {
                    LOGGER.error("Error while fetching BookVerlag-Relation", e);
                }

            }

            for (Person person : people) {
                try {
                    person.create(database);
                } catch (AlreadyExistsException e) {
                    il.addError(IntegrityLogger.ErrorType.DUPLICATE_ENTRY, e + this.toString() + person.toString());
                }
                try {
                    ResultSet bp = database.executeQuery("SELECT * FROM buch_autor WHERE produkt_id = ? AND person_id = ?", super.getDbId(), person.getDbId());
                    if (!bp.next()) {
                        LOGGER.info("Creating Book-Author-Relation for Book {} and Person {}", this.toString(), person.toString());
                        try {
                            database.executeUpdate(
                                    "INSERT INTO buch_autor (produkt_id, person_id) VALUES (?, ?)",
                                    super.getDbId(),
                                    person.getDbId()
                            );
                        } catch (SQLException e) {
                            if (e.getSQLState().equals("23505")) {
                                il.addError(IntegrityLogger.ErrorType.DUPLICATE_ENTRY, "BookPerson-Relation already exists" + this.toString() + verlag.toString());
                                LOGGER.warn("BookPerson-Relation for Book {} and Verlag {} already exists", this.toString(), verlag.toString());
                            } else {
                                LOGGER.error("Error while inserting BuchAutor-Relation for Book {} and Person {}", this.toString(), person.toString(), e);
                            }
                        }
                    } else {
                        LOGGER.warn("Book-Author-Relation already exists. Doing nothing...");
                    }
                } catch (SQLException e) {
                    LOGGER.error("Error while fetching Book-Author-Relation", e);
                }
            }
        } catch (SQLException e) {
            LOGGER.info("Error while creating Book with Product_id: " + getDbId(), e);
        }
    }

    /**
     * Gibt eine lesbare Darstellung des Buchobjekts zurück, einschließlich aller relevanten Felder.
     *
     * @return String-Repräsentation des Buchs
     */
    @Override
    public String toString() {
        return "Book{" +
                "people=" + people +
                ", audiobook=" + audiobook +
                ", isbn='" + isbn + '\'' +
                ", releasedate='" + releasedate + '\'' +
                ", pages=" + pages +
                ", verlag=" + verlag +
                "} " + super.toString();
    }
}
