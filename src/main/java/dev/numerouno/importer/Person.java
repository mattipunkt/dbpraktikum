package dev.numerouno.importer;

import dev.numerouno.db.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Person {
    private static final Logger LOGGER = LogManager.getLogger(Person.class);


    private String name;
    private String role;

    private int dbId;

    public Person(String name, String role) {
        this.name = name;
        this.role = role;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public void create(Database db) {
        try {
            ResultSet rs = db.executeQuery("SELECT * FROM person WHERE name = ? AND rolle = ?", name, role);
            if (rs.next()) {
                this.dbId = rs.getInt("person_id");
                LOGGER.info("Person {} already exists", name);
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

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", dbId=" + dbId +
                '}';
    }
}
