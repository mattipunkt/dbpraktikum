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

public class Book extends Product {
    private static final Logger LOGGER = LogManager.getLogger(Book.class);

    private List<Verlag> verlag = new ArrayList<>();
    private int pages;
    private String releasedate;
    private String isbn;
    private List<Person> people = new ArrayList<>();
    private boolean audiobook = false;

    public boolean isAudiobook() {
        return audiobook;
    }

    public void setAudiobook(boolean audiobook) {
        this.audiobook = audiobook;
    }

    public Book(String asin) {
        super(asin);
    }

    public List<Verlag> getVerlag() {
        return verlag;
    }

    public void setVerlag(List<Verlag> verlag) {
        this.verlag = verlag;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getDate() {
        return releasedate;
    }

    public void setDate(String date) {
        this.releasedate = date;
    }

    private LocalDate getDateAsObject() {
        if (releasedate == null) {
            return null;
        }
        return LocalDate.parse(releasedate);
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public List<Person> getAuthors() {
        return people;
    }

    public void setAuthors(List<Person> authors) {
        this.people = authors;
    }



    @Override
    public void create(Database database, int shopId, IntegrityLogger il) throws IntegrityException, AlreadyExistsException{
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
                verlag.create(database);
                try {
                    ResultSet bv = database.executeQuery("SELECT * FROM buch_verlag WHERE produkt_id = ? AND verlag_id = ?", super.getDbId(), verlag.getDbId());
                    if (!bv.next()) {
                        LOGGER.info("Creating Book_Verlag-Relation for Book {} and Verlag {}", this.toString(), verlag.toString());
                        try {
                            database.executeUpdate("INSERT INTO buch_verlag (produkt_id, verlag_id) VALUES (?, ?)", super.getDbId(), verlag.getDbId());
                        } catch (SQLException e) {
                            LOGGER.error("Error while inserting Book_Verlag-Relation for Book {} and Verlag {}", this.toString(), verlag.toString());
                        }
                    } else {
                        LOGGER.warn("Book-Verlag-Relation already exists. Doing nothing...");
                    }
                } catch (SQLException e) {
                    LOGGER.error("Error while fetching BookVerlag-Relation", e);
                }

            }

            for (Person person : people) {
                person.create(database);
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
                            LOGGER.error("Error while inserting BuchAutor-Relation for Book {} and Person {}", this.toString(), person.toString(), e);
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
