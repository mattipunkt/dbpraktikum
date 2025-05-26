package dev.numerouno.db;

import dev.numerouno.importer.XmlImporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;


public class Database {
    private Connection connection;
    private static final Logger LOGGER = LogManager.getLogger(Database.class);


    public Database() {
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/mediastore",
                    "numerouno",
                    "sir9w6%odk"
            );
        } catch (SQLException e) {

        }


        String sql = """
create table if not exists filiale
(
    filiale_id INT,
    anschrift  VARCHAR(200),
    name       VARCHAR(50),
    primary key (filiale_id)
);

create table if not exists kategorie
(
    kategorie_id  serial,
    name          VARCHAR(100),
    oberkategorie INT,
    primary key (kategorie_id),
    foreign key (oberkategorie) references kategorie
        on delete cascade
);

create table if not exists kunde
(
    kunde_id        INT,
    vorname         VARCHAR(40),
    nachname        VARCHAR(40),
    kontonummer     INT,
    adresse_strasse VARCHAR(100),
    adresse_plz     VARCHAR(5),
    adresse_ort     VARCHAR(50),
    primary key (kunde_id)
);

create table if not exists bestellung
(
    bestell_id INT,
    kunde_id   INT,
    zeit       TIME,
    primary key (bestell_id),
    foreign key (kunde_id) references kunde
        on delete cascade
);

create table if not exists bestellung_produkte
(
    bestell_id INT,
    produkt_id INT,
    primary key (bestell_id, produkt_id),
    foreign key (bestell_id) references bestellung
        on delete cascade
);

create table if not exists person
(
    person_id INT,
    vorname   VARCHAR(50),
    nachname  VARCHAR(50),
    rolle     VARCHAR(50),
    alias     VARCHAR(50),
    primary key (person_id)
);

create table if not exists produkt
(
    produkt_id   serial,
    asin         VARCHAR(50) not null,
    titel        VARCHAR(200),
    rating       FLOAT,
    bild         VARCHAR(400),
    verkaufsrang INT,
    primary key (produkt_id),
    unique (asin)
);

create table if not exists aehnliche_produkte
(
    produkt_id            INT,
    aehnliches_produkt_id INT,
    primary key (produkt_id, aehnliches_produkt_id),
    foreign key (produkt_id) references produkt,
    foreign key (aehnliches_produkt_id) references produkt
        on delete cascade
);

create table if not exists bewertung
(
    kunde_id        INT,
    produkt_id      INT,
    rezension       VARCHAR(1500),
    zusammenfassung VARCHAR(1500),
    sterne          INT,
    hilfreich       INT,
    datum           TIME,
    primary key (kunde_id, produkt_id),
    foreign key (kunde_id) references kunde,
    foreign key (produkt_id) references produkt
        on delete cascade
);

create table if not exists buch
(
    produkt_id        INT,
    verlag            VARCHAR(50),
    seitenzahl        INT,
    erscheinungsdatum DATE,
    ISBN              INT,
    primary key (produkt_id),
    foreign key (produkt_id) references produkt
        on delete cascade
);

create table if not exists buch_autor
(
    produkt_id INT,
    person_id  INT,
    primary key (produkt_id, person_id),
    foreign key (produkt_id) references buch,
    foreign key (person_id) references person
        on delete cascade
);

create table if not exists cd
(
    produkt_id        INT,
    erscheinungsdatum DATE,
    label             VARCHAR(50),
    primary key (produkt_id),
    foreign key (produkt_id) references produkt
        on delete cascade
);

create table if not exists cd_kuenstler
(
    produkt_id INT,
    person_id  INT,
    primary key (produkt_id, person_id),
    foreign key (produkt_id) references cd,
    foreign key (person_id) references person
        on delete cascade
);

create table if not exists dvd
(
    produkt_id  INT,
    format      VARCHAR(4),
    laufzeit    TIME,
    region_code VARCHAR(1),
    primary key (produkt_id),
    foreign key (produkt_id) references produkt
        on delete cascade
);

create table if not exists dvd_beteiligte
(
    produkt_id INT,
    person_id  INT,
    primary key (produkt_id, person_id),
    foreign key (produkt_id) references dvd,
    foreign key (person_id) references person
        on delete cascade
);

create table if not exists filial_produkte
(
    filiale_id INT,
    produkt_id INT,
    preis      INT,
    zustand    VARCHAR(20),
    primary key (filiale_id, produkt_id),
    foreign key (filiale_id) references filiale,
    foreign key (produkt_id) references produkt
        on delete cascade
);

create table if not exists musiktitel
(
    titel_id   INT,
    nr         INT,
    name       VARCHAR(200),
    produkt_id INT,
    primary key (titel_id, produkt_id),
    foreign key (produkt_id) references cd
        on delete cascade
);

create table if not exists produkt_kategorie
(
    kategorie_id INT,
    produkt_id   INT,
    primary key (kategorie_id, produkt_id),
    foreign key (kategorie_id) references kategorie,
    foreign key (produkt_id) references produkt
        on delete cascade
);

create table if not exists unterkategorie
(
    kategorie_id      INT,
    unterkategorie_id INT,
    primary key (kategorie_id, unterkategorie_id),
    foreign key (kategorie_id) references kategorie,
    foreign key (unterkategorie_id) references kategorie
        on delete cascade
);


                """;
        try  {
            assert this.connection != null;
            connection.setAutoCommit(false);
            var stmt = this.connection.createStatement();
            String[] phrases = sql.split(";");
            for (String phrase : phrases) {
                stmt.addBatch(phrase);
                stmt.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }


    public ResultSet executeQuery(String query, Object... params) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt.executeQuery();
    }

    public int executeUpdate(String query, Object... params) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            int affectedRows = stmt.executeUpdate();
            connection.commit();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return affectedRows;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Database connection closed");
            } catch (SQLException e) {
                LOGGER.error("Failed to close database connection", e);
            }
        }
    }

}