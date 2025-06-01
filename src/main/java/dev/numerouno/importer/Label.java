package dev.numerouno.importer;

import dev.numerouno.db.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Die Klasse {@code Label} repräsentiert ein Label-Objekt mit einem Namen und einer Datenbank-ID.
 * Sie bietet Methoden zur Erstellung eines Labels in einer Datenbank.
 */
public class Label {
    int dbId;
    String name;


    /**
     * Konstruktor, der ein neues {@code Label} mit dem gegebenen Namen erstellt.
     *
     * @param label Der Name des Labels.
     */
    public Label(String label) {
        this.name = label;
    }


    /**
     * Gibt die Datenbank-ID des Labels zurück.
     *
     * @return Die Datenbank-ID.
     */
    public int getDbId() {
        return dbId;
    }

    /**
     * Setzt die Datenbank-ID des Labels.
     *
     * @param dbId Die neue Datenbank-ID.
     */
    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    /**
     * Gibt den Namen des Labels zurück.
     *
     * @return Der Name des Labels.
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Namen des Labels.
     *
     * @param name Der neue Name des Labels.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Erstellt das Label in der Datenbank, falls es noch nicht existiert.
     * <p>
     * Wenn bereits ein Label mit dem gleichen Namen vorhanden ist, wird dessen ID übernommen.
     * Andernfalls wird ein neues Label in der Datenbank angelegt.
     * </p>
     *
     * @param database Die Datenbankverbindung, über die die Operation ausgeführt wird.
     * @throws AlreadyExistsException Wenn das Label bereits vorhanden ist (eindeutiger Verstoß).
     * @throws IntegrityException     Wenn ein Integritätsfehler auftritt.
     */
    public void create(Database database) throws AlreadyExistsException, IntegrityException {
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM label WHERE name = ?", name);
            if (rs.next()) {
                this.dbId = rs.getInt("label_id");
            } else {
                try {
                    this.dbId = database.executeUpdate("INSERT INTO label (name) VALUES (?)", name);
                } catch (SQLException e) {
                    if (e.getSQLState().equals("23505")) {
                        throw new AlreadyExistsException("Label already exists ");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
