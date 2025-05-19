package dev.numerouno.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class Database {
    private Connection connection;

    public Database() {
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/mediastore",
                    "numerouno",
                    "sir9w6%odk"
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
    kategorie_id   INT,
    name           VARCHAR(50),
    unterkategorie INT,
    oberkategorie  INT,
    primary key (kategorie_id),
    foreign key (unterkategorie) references kategorie,
    foreign key (oberkategorie) references kategorie
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
);

create table if not exists bestellung_produkte
(
    bestell_id INT,
    produkt_id INT,
    primary key (bestell_id, produkt_id),
    foreign key (bestell_id) references bestellung
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

create table if not exists buch_autor
(
    produkt_id INT,
    person_id  INT,
    primary key (produkt_id, person_id),
    foreign key (person_id) references person,
    foreign key (person_id) references person
);

create table if not exists cd_kuenstler
(
    produkt_id INT,
    person_id  INT,
    primary key (produkt_id, person_id),
    foreign key (person_id) references person,
    foreign key (person_id) references person
);

create table if not exists dvd_beteiligte
(
    produkt_id INT,
    person_id  INT,
    primary key (produkt_id, person_id),
    foreign key (person_id) references person,
    foreign key (person_id) references person
);

create table if not exists produkt
(
    produkt_id   INT,
    titel        VARCHAR(200) not null,
    rating       FLOAT,
    bild         VARCHAR(400),
    verkaufsrang INT,
    primary key (produkt_id)
);

create table if not exists aehnliche_produkte
(
    produkt_id            INT,
    aehnliches_produkt_id INT,
    primary key (produkt_id, aehnliches_produkt_id),
    foreign key (aehnliches_produkt_id) references produkt,
    foreign key (aehnliches_produkt_id) references produkt
);

create table if not exists bewertung
(
    kunde_id   INT,
    produkt_id INT,
    rezension  VARCHAR(1500),
    sterne     INT,
    primary key (kunde_id, produkt_id),
    foreign key (produkt_id) references produkt,
    foreign key (produkt_id) references produkt
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
);

create table if not exists cd
(
    produkt_id        INT,
    erscheinungsdatum DATE,
    label             VARCHAR(50),
    primary key (produkt_id),
    foreign key (produkt_id) references produkt
);

create table if not exists dvd
(
    produkt_id  INT,
    format      VARCHAR(4),
    laufzeit    TIME,
    region_code VARCHAR(1),
    primary key (produkt_id),
    foreign key (produkt_id) references produkt
);

create table if not exists filial_produkte
(
    filiale_id INT,
    produkt_id INT,
    preis      INT,
    zustand    VARCHAR(20),
    primary key (filiale_id, produkt_id),
    foreign key (produkt_id) references produkt,
    foreign key (produkt_id) references produkt
);

create table if not exists musiktitel
(
    titel_id   INT,
    nr         INT,
    name       VARCHAR(200),
    produkt_id INT,
    primary key (titel_id, produkt_id),
    foreign key (produkt_id) references cd
);

create table if not exists produkt_kategorie
(
    kategorie_id INT,
    produkt_id   INT,
    primary key (kategorie_id, produkt_id),
    foreign key (produkt_id) references produkt,
    foreign key (produkt_id) references produkt
);

       
                """;
        try  {
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

}