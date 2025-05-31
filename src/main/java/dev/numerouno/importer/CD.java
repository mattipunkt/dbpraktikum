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

public class CD extends Product {

    private static final Logger LOGGER = LogManager.getLogger(CD.class);

    private List<Label> label = new ArrayList<>();
    private String date;
    private List<MusicTitle> titles = new ArrayList<>();
    private List<Person> artists = new ArrayList<>();

    public CD(String asin) {
        super(asin);
    }

    public List<MusicTitle> getTitles() {
        return titles;
    }

    public List<Label> getLabel() {
        return label;
    }

    public void setLabel(List<Label> label) {
        this.label = label;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private LocalDate parseDate() {
        if (date == null) {
            return null;
        }
        return LocalDate.parse(date);
    }

    public List<Person> getArtists() {
        return artists;
    }

    public void setArtists(List<Person> artists) {
        this.artists = artists;
    }

    public void setTitles(List<MusicTitle> titles) {
        this.titles = titles;
    }

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
