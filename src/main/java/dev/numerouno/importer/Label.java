package dev.numerouno.importer;

import dev.numerouno.db.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Label {
    int dbId;
    String name;

    public Label(String label) {
        this.name = label;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void create(Database database) {
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM label WHERE name = ?", name);
            if (rs.next()) {
                this.dbId = rs.getInt("label_id");
            } else {
                this.dbId = database.executeUpdate("INSERT INTO label (name) VALUES (?)", name);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
