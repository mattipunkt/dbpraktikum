package dev.numerouno.importer;

import dev.numerouno.db.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repräsentiert einen Verlag mit einem Namen und einer Datenbank-ID.
 * Diese Klasse ermöglicht das Anlegen eines Verlags in der Datenbank
 * und prüft, ob der Verlag bereits existiert.
 */
public class Verlag {
    private static final Logger LOGGER = LogManager.getLogger(Verlag.class);

    private String name;
    private int dbId;

    /**
     * Erstellt eine neue Instanz eines Verlags mit dem gegebenen Namen.
     *
     * @param name Der Name des Verlags.
     */
    public Verlag(String name) {
        this.name = name;
    }

    /**
     * Gibt den Namen des Verlags zurück.
     *
     * @return Der Name des Verlags.
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Namen des Verlags.
     *
     * @param name Der neue Name des Verlags.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gibt die Datenbank-ID des Verlags zurück.
     *
     * @return Die ID des Verlags in der Datenbank.
     */
    public int getDbId() {
        return dbId;
    }

    /**
     * Setzt die Datenbank-ID des Verlags.
     *
     * @param dbId Die neue Datenbank-ID.
     */
    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    /**
     * Legt den Verlag in der Datenbank an, falls dieser noch nicht existiert.
     * Falls ein Verlag mit dem gleichen Namen bereits existiert, wird
     * eine {@link AlreadyExistsException} geworfen.
     *
     * @param db Die Datenbankverbindung zum Ausführen der Abfragen.
     * @throws AlreadyExistsException wenn ein Verlag mit dem Namen bereits existiert.
     */
    public void create(Database db) throws AlreadyExistsException{
        try {
            ResultSet rs = db.executeQuery("SELECT verlag_id FROM verlag WHERE name = ?", this.name);
            if (rs.next()) {
                this.dbId = rs.getInt("verlag_id");
                throw new AlreadyExistsException("Verlag with name " + this.name + " already exists");
            } else {
                try {
                    this.dbId = db.executeUpdate("INSERT INTO verlag (name) VALUES (?)", this.name);
                } catch (SQLException e) {
                    LOGGER.error("Error creating verlag", e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
