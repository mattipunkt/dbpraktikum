package dev.numerouno.importer;

import dev.numerouno.db.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Verlag {
    private static final Logger LOGGER = LogManager.getLogger(Verlag.class);

    private String name;
    private int dbId;

    public Verlag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

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
