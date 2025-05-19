-- Produkt-Entität
CREATE TABLE produkt (
    produkt_id INT PRIMARY KEY,
    titel VARCHAR(200) NOT NULL,
    rating FLOAT,
    bild VARCHAR(400),
    verkaufsrang INT
);

-- Person-Entität
CREATE TABLE person (
    person_id INT PRIMARY KEY,
    vorname VARCHAR(50),
    nachname VARCHAR(50),
    rolle VARCHAR(50),
    alias VARCHAR(50)
);

-- CD-Entität
CREATE TABLE cd (
    produkt_id INT PRIMARY KEY,
    erscheinungsdatum DATE,
    label VARCHAR(50),
    FOREIGN KEY (produkt_id) REFERENCES produkt(produkt_id)
);

CREATE TABLE cd_kuenstler (
    produkt_id INT,
    person_id INT,
    PRIMARY KEY (produkt_id, person_id),
    FOREIGN KEY (produkt_id) REFERENCES cd(produkt_id),
    FOREIGN KEY (person_id) REFERENCES person(person_id)
);


-- Musiktitel
CREATE TABLE musiktitel (
    titel_id INT,
    nr INT,
    name VARCHAR(200),
    produkt_id INT,
    PRIMARY KEY (titel_id, produkt_id),
    FOREIGN KEY (produkt_id) REFERENCES cd(produkt_id)
);

-- Buch
CREATE TABLE buch (
    produkt_id INT PRIMARY KEY,
    verlag VARCHAR(50),
    seitenzahl INT,
    erscheinungsdatum DATE,
    ISBN INT,
    FOREIGN KEY (produkt_id) REFERENCES produkt(produkt_id)
);

-- Buchautoren
CREATE TABLE buch_autor (
    produkt_id INT,
    person_id INT,
    PRIMARY KEY(produkt_id, person_id),
    FOREIGN KEY (produkt_id) REFERENCES buch(produkt_id),
    FOREIGN KEY (person_id) REFERENCES person(person_id)
);

-- DVD
CREATE TABLE dvd (
    produkt_id INT PRIMARY KEY,
    format VARCHAR(4),
    laufzeit TIME,
    region_code VARCHAR(1),
    FOREIGN KEY (produkt_id) REFERENCES produkt(produkt_id)
);

CREATE TABLE dvd_beteiligte (
    produkt_id INT,
    person_id INT,
    PRIMARY KEY (produkt_id, person_id),
    FOREIGN KEY (produkt_id) REFERENCES dvd(produkt_id),
    FOREIGN KEY (person_id) REFERENCES person(person_id)
);

CREATE TABLE aehnliche_produkte(
    produkt_id INT,
    aehnliches_produkt_id INT,
    PRIMARY KEY (produkt_id, aehnliches_produkt_id),
    FOREIGN KEY (produkt_id) REFERENCES produkt(produkt_id),
    FOREIGN KEY (aehnliches_produkt_id) REFERENCES produkt(produkt_id)
);

CREATE TABLE kategorie (
    kategorie_id INT PRIMARY KEY,
    name VARCHAR(50),
    unterkategorie INT,
    oberkategorie INT,
    FOREIGN KEY (unterkategorie) REFERENCES kategorie(kategorie_id),
    FOREIGN KEY (oberkategorie) REFERENCES kategorie(kategorie_id)
);

CREATE TABLE produkt_kategorie (
    kategorie_id INT,
    produkt_id INT,
    PRIMARY KEY (kategorie_id, produkt_id),
    FOREIGN KEY (kategorie_id) REFERENCES kategorie(kategorie_id),
    FOREIGN KEY (produkt_id) REFERENCES produkt(produkt_id)
);


CREATE TABLE kunde (
    kunde_id INT PRIMARY KEY,
    vorname VARCHAR(40),
    nachname VARCHAR(40),
    kontonummer INT,
    adresse_strasse VARCHAR(100),
    adresse_plz VARCHAR(5),
    adresse_ort VARCHAR(50)
);

CREATE TABLE filiale (
    filiale_id INT PRIMARY KEY,
    anschrift VARCHAR(200),
    name VARCHAR(50)
);

CREATE TABLE filial_produkte (
    filiale_id INT,
    produkt_id INT,
    preis INT,
    zustand VARCHAR(20),
    PRIMARY KEY (filiale_id, produkt_id),
    FOREIGN KEY (filiale_id) REFERENCES filiale(filiale_id),
    FOREIGN KEY (produkt_id) REFERENCES produkt(produkt_id)
);

CREATE TABLE bestellung (
    bestell_id INT PRIMARY KEY,
    kunde_id INT,
    zeit TIME,
    FOREIGN KEY (kunde_id) REFERENCES kunde(kunde_id)
);

CREATE TABLE bestellung_produkte (
    bestell_id INT,
    produkt_id INT,
    FOREIGN KEY (bestell_id) REFERENCES bestellung(bestell_id),
    PRIMARY KEY(bestell_id, produkt_id)
);

CREATE TABLE bewertung (
    kunde_id INT,
    produkt_id INT,
    rezension VARCHAR(1500),
    sterne INT,
    PRIMARY KEY (kunde_id, produkt_id),
    FOREIGN KEY (kunde_id) REFERENCES kunde(kunde_id),
    FOREIGN KEY (produkt_id) REFERENCES produkt(produkt_id)
)
