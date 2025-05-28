package dev.numerouno.importer;

import dev.numerouno.db.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MusicTitle {
    private static final Logger LOGGER = LogManager.getLogger(MusicTitle.class);

    private String title;
    private int dbId;

    public MusicTitle(String title) {
        this.title = title;
    }


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
