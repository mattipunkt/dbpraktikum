package dev.numerouno.importer;

import dev.numerouno.db.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Repräsentiert ein CD-Produkt, das über ASIN identifiziert wird.
 * Diese Klasse erweitert die {@link Product}-Klasse und enthält spezifische Eigenschaften und
 * Logik für CDs, wie z. B. Musik-Titel, Künstler, Label und Veröffentlichungsdatum.
 *
 * Die CD-Instanz kann in eine Datenbank importiert oder aktualisiert werden.
 */
public class CD extends Product {

    private static final Logger LOGGER = LogManager.getLogger(CD.class);

    private List<Label> label = new ArrayList<>();
    private String date;
    private List<MusicTitle> titles = new ArrayList<>();
    private List<Person> artists = new ArrayList<>();


    /**
     * Konstruktor zur Initialisierung einer CD anhand ihrer ASIN.
     *
     * @param asin Amazon Standard Identification Number
     */
    public CD(String asin) {
        super(asin);
    }

    /**
     * Gibt die Liste der Musiktitel dieser CD zurück.
     *
     * @return Liste von {@link MusicTitle}
     */
    public List<MusicTitle> getTitles() {
        return titles;
    }


    /**
     * Gibt die Liste der Labels zurück, die mit dieser CD verbunden sind.
     *
     * @return Liste von {@link Label}
     */
    public List<Label> getLabel() {
        return label;
    }


    /**
     * Setzt die Liste der Labels für diese CD.
     *
     * @param label Liste von {@link Label}
     */
    public void setLabel(List<Label> label) {
        this.label = label;
    }

    /**
     * Gibt das Veröffentlichungsdatum der CD im String-Format zurück.
     *
     * @return Veröffentlichungsdatum als String
     */
    public String getDate() {
        return date;
    }


    /**
     * Setzt das Veröffentlichungsdatum dieser CD.
     *
     * @param date Veröffentlichungsdatum im ISO-Format (yyyy-MM-dd)
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Parsed das gespeicherte Datum als {@link LocalDate}.
     *
     * @return {@link LocalDate} Objekt oder {@code null}, falls kein Datum gesetzt
     */
    private LocalDate parseDate() {
        if (date == null) {
            return null;
        }
        return LocalDate.parse(date);
    }

    /**
     * Gibt die Liste der Künstler (Personen) zurück, die mit dieser CD assoziiert sind.
     *
     * @return Liste von {@link Person}
     */
    public List<Person> getArtists() {
        return artists;
    }

    /**
     * Setzt die Liste der Künstler (Personen) für diese CD.
     *
     * @param artists Liste von {@link Person}
     */
    public void setArtists(List<Person> artists) {
        this.artists = artists;
    }

    /**
     * Setzt die Liste der Musiktitel für diese CD.
     *
     * @param titles Liste von {@link MusicTitle}
     */
    public void setTitles(List<MusicTitle> titles) {
        this.titles = titles;
    }

    /**
     * Erstellt oder aktualisiert die Datenbankeinträge für dieses CD-Objekt,
     * einschließlich Produktdetails, CD-spezifischer Daten, Titel, Künstler- und Label-Relationen.
     *
     * Fehler und Integritätskonflikte werden im {@link IntegrityLogger} dokumentiert.
     *
     * @param database Datenbankverbindung
     * @param shopId ID des Shops
     * @param il Logger für Integritätsfehler
     * @throws IntegrityException bei schwerwiegenden Integritätsproblemen
     * @throws AlreadyExistsException wenn das Produkt bereits existiert
     */
    @Override
    public void create(Database database, int shopId, IntegrityLogger il) throws IntegrityException, AlreadyExistsException {
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
            ResultSet rs = database.executeQuery("SELECT * FROM cd WHERE produkt_id = ?", super.getDbId());
            if (rs.next()) {
                LOGGER.info("Found product as CD in Database {}", this.toString());
                LOGGER.info("Updating...");
                try {
                    database.executeUpdate("UPDATE cd SET erscheinungsdatum = ? WHERE produkt_id = ?",
                            this.parseDate(),
                            super.getDbId()
                    );
                } catch (SQLException e) {
                    LOGGER.error("Error while updating CD", e);
                }

            } else {
                LOGGER.info("CD not Found, creating... {}", this.toString());
                try {
                    database.executeUpdate("INSERT INTO cd (produkt_id, erscheinungsdatum) VALUES (?, ?)",
                            super.getDbId(),
                            this.parseDate()
                    );
                } catch (SQLException e) {
                    LOGGER.error("Error while creating CD", e);
                }
            }

            for (MusicTitle title : titles) {
                title.create(database, this.getDbId());
            }

            for (Person artist : artists) {
                try {
                    artist.create(database);
                } catch (AlreadyExistsException e) {
                    il.addError(IntegrityLogger.ErrorType.DUPLICATE_ENTRY, e + this.toString() + artist.toString());
                }
                ResultSet am = database.executeQuery("SELECT * FROM cd_kuenstler WHERE produkt_id = ? AND person_id = ?",
                        super.getDbId(),
                        artist.getDbId()
                );
                if (am.next()) {
                    il.addError(IntegrityLogger.ErrorType.DUPLICATE_ENTRY, "ArtistCD-Relation already exists" + this.toString() + artist.toString());
                    LOGGER.warn("Artist-CD-Relation is already in DB {}, {}", this.toString(), artist.toString());
                } else {
                    LOGGER.info("Creating Artist-CD-Relation {}, {}", this.toString(), artist.toString());
                    try {
                        database.executeUpdate(
                                "INSERT INTO cd_kuenstler (produkt_id, person_id) VALUES (?, ?)",
                                this.getDbId(),
                                artist.getDbId()
                        );
                    } catch (SQLException e) {
                        if (e.getSQLState().equals("23505")) {
                            il.addError(IntegrityLogger.ErrorType.DUPLICATE_ENTRY, "ArtistCD-Relation already exists" + this.toString() + artist.toString());
                        } else {

                        }
                        LOGGER.error("Error while creating CDKuenstler-Relation", e);
                    }

                }
            }

            for (Label label : label) {
                try {
                    label.create(database);
                } catch (AlreadyExistsException e) {
                    il.addError(IntegrityLogger.ErrorType.DUPLICATE_ENTRY, e + this.toString());
                }
                try {
                    ResultSet lm = database.executeQuery(
                            "SELECT * FROM cd_label WHERE produkt_id = ? AND label_id = ?",
                            this.getDbId(),
                            label.getDbId()
                    );
                    if (lm.next()) {
                        il.addError(IntegrityLogger.ErrorType.DUPLICATE_ENTRY, "LabelCD-Relation already exists" + this.toString() + label.toString());
                        LOGGER.warn("CD-Label Relation already in DB {}, {}", this.toString(), label.toString());
                    } else {
                        LOGGER.info("Creating CD-Label-Relation {}, {}", this.toString(), label.toString());
                        try {
                            database.executeUpdate("INSERT INTO cd_label (produkt_id, label_id) VALUES (?,?)",
                                    this.getDbId(),
                                    label.getDbId()
                            );
                        } catch (SQLException e) {
                            if (e.getSQLState().equals("23505")) {
                                LOGGER.warn("CD-Label-Relation already exists" + this.toString() + label.toString());
                                il.addError(IntegrityLogger.ErrorType.DUPLICATE_ENTRY, "ArtistCD-Relation already exists" + this.toString() + label.toString());
                            } else {
                                il.addError(IntegrityLogger.ErrorType.SYNTAX_ERROR, "Error while inserting into cd_label " + this.toString() + label.toString());
                            }
                        }
                    }
                }
                catch (SQLException e) {
                    LOGGER.error("Error while creating CD-Label-Relation", e);
                }
            }
        } catch (SQLException e) {
                LOGGER.error("Error while fetching or creating CD", e);
        }
    }

    /**
     * Gibt eine String-Repräsentation dieses CD-Objekts zurück.
     *
     * @return String mit Details zu Label, Datum, Titeln und Künstlern
     */
    @Override
    public String toString() {
        return "CD{" +
                "label=" + label +
                ", date='" + date + '\'' +
                ", titles=" + titles +
                ", artists=" + artists +
                "} " + super.toString();
    }
}
