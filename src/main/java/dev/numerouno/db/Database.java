package dev.numerouno.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;


/**
 * Database-Class as a wrapper for PostgreSQL-Database and SQL-Queries
 */
public class Database {
    /**
     * connection to the database
     */
    private Connection connection;

    /**
     * logger for loggin errors
     */
    private static final Logger LOGGER = LogManager.getLogger(Database.class);

    /**
     * Constructor initializes database
     */
    public Database() {
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/mediastore",
                    "numerouno",
                    "sir9w6%odk"
            );
        } catch (SQLException e) {
            LOGGER.error("Could not establish DatabaseConnection", e);
            System.exit(1);
        }


        String sql = """
create table if not exists filiale
(
    filiale_id serial,
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
    kunde_id        serial,
    gast            BOOLEAN,
    vorname         VARCHAR(40),
    nachname        VARCHAR(40),
    username        VARCHAR(100),
    kontonummer     INT,
    adresse_strasse VARCHAR(100),
    adresse_plz     VARCHAR(5),
    adresse_ort     VARCHAR(50),
    primary key (kunde_id),
    unique (username)
);

create table if not exists bestellung
(
    bestell_id serial,
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

create table if not exists label
(
    label_id serial,
    name     VARCHAR(100),
    primary key (label_id)
);

create table if not exists person
(
    person_id serial,
    name      VARCHAR(150),
    rolle     VARCHAR(50),
    alias     VARCHAR(50),
    primary key (person_id)
);

create table if not exists produkt
(
    produkt_id   serial,
    asin         VARCHAR(50) not null,
    titel        VARCHAR(300),
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
    foreign key (produkt_id) references produkt
        on delete cascade,
    foreign key (aehnliches_produkt_id) references produkt
        on delete cascade
);

create table if not exists bewertung
(
    kunde_id        INT,
    produkt_id      INT,
    rezension       VARCHAR(10000),
    zusammenfassung VARCHAR(15000),
    sterne          INT,
    hilfreich       INT,
    datum           DATE,
    primary key (kunde_id, produkt_id),
    foreign key (kunde_id) references kunde
        on delete cascade,
    foreign key (produkt_id) references produkt
        on delete cascade
);

create table if not exists buch
(
    produkt_id        serial,
    seitenzahl        INT,
    erscheinungsdatum DATE,
    ISBN              VARCHAR(30),
    primary key (produkt_id),
    foreign key (produkt_id) references produkt
        on delete cascade
);

create table if not exists buch_autor
(
    produkt_id INT,
    person_id  INT,
    primary key (produkt_id, person_id),
    foreign key (produkt_id) references buch
        on delete cascade,
    foreign key (person_id) references person
        on delete cascade
);

create table if not exists cd
(
    produkt_id        serial,
    erscheinungsdatum DATE,
    primary key (produkt_id),
    foreign key (produkt_id) references produkt
        on delete cascade
);

create table if not exists cd_kuenstler
(
    produkt_id INT,
    person_id  INT,
    primary key (produkt_id, person_id),
    foreign key (produkt_id) references cd
        on delete cascade,
    foreign key (person_id) references person
        on delete cascade
);

create table if not exists cd_label
(
    label_id   INT,
    produkt_id INT,
    primary key (produkt_id, label_id),
    foreign key (label_id) references label
        on delete cascade,
    foreign key (produkt_id) references cd
        on delete cascade
);

create table if not exists dvd
(
    produkt_id  serial,
    format      VARCHAR(60),
    laufzeit    INT,
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
    foreign key (produkt_id) references dvd
        on delete cascade,
    foreign key (person_id) references person
        on delete cascade
);

create table if not exists filial_produkte
(
    filiale_id INT,
    produkt_id INT,
    preis      DOUBLE PRECISION,
    zustand    VARCHAR(20),
    primary key (filiale_id, produkt_id),
    foreign key (filiale_id) references filiale
        on delete cascade,
    foreign key (produkt_id) references produkt
        on delete cascade
);

create table if not exists musiktitel
(
    titel_id   serial,
    nr         INT,
    name       VARCHAR(200),
    produkt_id INT,
    primary key (titel_id),
    foreign key (produkt_id) references cd
        on delete cascade
);

create table if not exists produkt_kategorie
(
    kategorie_id INT,
    produkt_id   INT,
    primary key (kategorie_id, produkt_id),
    foreign key (kategorie_id) references kategorie
        on delete cascade,
    foreign key (produkt_id) references produkt
        on delete cascade
);

create table if not exists unterkategorie
(
    kategorie_id      INT,
    unterkategorie_id INT,
    primary key (kategorie_id, unterkategorie_id),
    foreign key (kategorie_id) references kategorie
        on delete cascade,
    foreign key (unterkategorie_id) references kategorie
        on delete cascade
);

create table if not exists verlag
(
    verlag_id serial,
    name      VARCHAR(100),
    primary key (verlag_id)
);

create table if not exists buch_verlag
(
    verlag_id  INT,
    produkt_id INT,
    primary key (produkt_id, verlag_id),
    foreign key (verlag_id) references verlag
        on delete cascade,
    foreign key (produkt_id) references buch
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

    /**
     * function to get connection
     * @return Connection of Database, never really needed
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * executes SQL query
     * @param query SQL-query that should be executed
     * @param params paramters to prevent sql injection
     * @return ResultSet with results from query
     * @throws SQLException error from sql execution
     */
    public ResultSet executeQuery(String query, Object... params) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt.executeQuery();
    }

    /**
     * function for updating or inserting into the database
     * @param query sql query
     * @param params parameters to prevent sql injection and to preserve type safety (sort of)
     * @return the database id that was updated or inserted
     * @throws SQLException error on execution
     */
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

    /**
     * closes database connection
     */
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