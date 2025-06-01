package dev.numerouno.importer;

import dev.numerouno.db.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repräsentiert einen Musiktitel und ermöglicht das Erstellen eines Titels in der Datenbank.
 */
public class MusicTitle {
    private static final Logger LOGGER = LogManager.getLogger(MusicTitle.class);

    private String title;
    private int dbId;

    /**
     * Erstellt eine neue Instanz eines Musiktitels mit dem angegebenen Titelnamen.
     *
     * @param title Der Name des Musiktitels.
     */
    public MusicTitle(String title) {
        this.title = title;
    }



    /**
     * Fügt den Musiktitel in die Datenbank ein, sofern er für das gegebene Produkt noch nicht existiert.
     * Wenn der Titel bereits existiert, wird die ID aus der Datenbank übernommen.
     *
     * @param database    Die Datenbankverbindung, die zum Ausführen von Abfragen verwendet wird.
     * @param productDbId Die ID des Produkts, zu dem der Musiktitel gehört.
     */
    public void create(Database database, int productDbId) {
        try {
            ResultSet rs = database.executeQuery(
                    "SELECT * from musiktitel WHERE produkt_id = ? AND name = ?",
                    productDbId,
                    title
            );
            if (rs.next()) {
                LOGGER.info("Title already exists, skipping. {}", this.toString());
                this.dbId = rs.getInt("titel_id");
            } else {
                try {
                    this.dbId = database.executeUpdate("INSERT INTO musiktitel (name, produkt_id) VALUES (?, ?)", title, productDbId);
                } catch (SQLException e) {
                    LOGGER.error("Error while inserting musiktitel", e);
                }
            }
        } catch (SQLException s) {
            LOGGER.error(s);
        }
    }
}
