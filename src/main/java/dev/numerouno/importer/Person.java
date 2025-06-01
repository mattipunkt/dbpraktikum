package dev.numerouno.importer;

import dev.numerouno.db.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repräsentiert eine Person mit einem Namen und einer Rolle.
 * Diese Klasse ermöglicht das Anlegen und Abrufen von Person-Daten aus einer Datenbank.
 */
public class Person {
    private static final Logger LOGGER = LogManager.getLogger(Person.class);


    private String name;
    private String role;

    private int dbId;

    /**
     * Erstellt eine neue Person mit Name und Rolle.
     *
     * @param name Name der Person
     * @param role Rolle der Person
     */
    public Person(String name, String role) {
        this.name = name;
        this.role = role;
    }


    /**
     * Liefert den Namen der Person.
     *
     * @return Name der Person
     */
    public String getName() {
        return name;
    }


    /**
     * Setzt den Namen der Person.
     *
     * @param name Neuer Name der Person
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Liefert die Rolle der Person.
     *
     * @return Rolle der Person
     */
    public String getRole() {
        return role;
    }

    /**
     * Setzt die Rolle der Person.
     *
     * @param role Neue Rolle der Person
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Liefert die Datenbank-ID der Person.
     *
     * @return Datenbank-ID
     */
    public int getDbId() {
        return dbId;
    }

    /**
     * Setzt die Datenbank-ID der Person.
     *
     * @param dbId Neue Datenbank-ID
     */
    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

     /**
     * Erstellt die Person in der Datenbank.
     * Falls die Person bereits existiert, wird eine {@link AlreadyExistsException} geworfen.
     *
     * @param db Datenbank-Verbindung
     * @throws AlreadyExistsException falls die Person bereits in der Datenbank existiert
     */
    public void create(Database db) throws AlreadyExistsException{
        try {
            ResultSet rs = db.executeQuery("SELECT * FROM person WHERE name = ? AND rolle = ?", name, role);
            if (rs.next()) {
                this.dbId = rs.getInt("person_id");
                LOGGER.info("Person {} already exists", name);
                throw new AlreadyExistsException("Person " + name + " already exists");
            } else {
                LOGGER.info("Creating person {}", name);
                try {
                    this.dbId = db.executeUpdate("INSERT INTO person (name, rolle) VALUES (?, ?)", name, role);
                } catch (SQLException e) {
                    LOGGER.error("Error creating person", e);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Could not fetch person", e);
        }
    }

    /**
     * Liefert eine String-Repräsentation der Person.
     *
     * @return String mit Namen, Rolle und Datenbank-ID der Person
     */
    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", dbId=" + dbId +
                '}';
    }
}
