package dev.numerodos;

import dev.numerouno.db.Database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Queries {
    public Queries(Database db) throws SQLException
    //, NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        /**
         for (int i = 1; i <= 12; i++) {
         Method method = Queries.class.getMethod("query" + i, Connection.class);
         ResultSet rs = (ResultSet) method.invoke(null, db);
         String result = resultSetToString(rs);
         writeInFile(i, result);
         }
         **/
        ResultSet rs1 = query1(db);
        String result1 = resultSetToString(rs1);
        writeInFile(1, result1);

        ResultSet rs2 = query2(db);
        String result2 = resultSetToString(rs2);
        writeInFile(2, result2);

        ResultSet rs3 = query3(db);
        String result3 = resultSetToString(rs3);
        writeInFile(3, result3);

        ResultSet rs4 = query4(db);
        String result4 = resultSetToString(rs4);
        writeInFile(4, result4);

        ResultSet rs5 = query5(db);
        String result5 = resultSetToString(rs5);
        writeInFile(5, result5);

        ResultSet rs6 = query6(db);
        String result6 = resultSetToString(rs6);
        writeInFile(6, result6);

        ResultSet rs7 = query7(db);
        String result7 = resultSetToString(rs7);
        writeInFile(7, result7);

        ResultSet rs8 = query8(db);
        String result8 = resultSetToString(rs8);
        writeInFile(8, result8);

        ResultSet rs9 = query9(db);
        String result9 = resultSetToString(rs9);
        writeInFile(9, result9);

        ResultSet rs10 = query10(db);
        String result10 = resultSetToString(rs10);
        writeInFile(10, result10);

        ResultSet rs11 = query11(db);
        String result11 = resultSetToString(rs11);
        writeInFile(11, result11);

        ResultSet rs12 = query12(db);
        String result12 = resultSetToString(rs12);
        writeInFile(12, result12);
    }

    private void writeInFile(int query, String result) {
        File file = new File("Ergebnisse");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write("Query " + query + ".");
            writer.newLine();
            writer.write(result);
            writer.newLine();
            writer.close();
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    private String resultSetToString(ResultSet rs) {
        StringBuilder sb = new StringBuilder();
        try {
            var meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            // Spaltennamen
            for (int i = 1; i <= columnCount; i++) {
                sb.append(meta.getColumnName(i));
                if (i < columnCount) sb.append(", ");
            }
            sb.append(System.lineSeparator());

            // Zeilen
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    sb.append(value != null ? value : "NULL");
                    if (i < columnCount) sb.append(", ");
                }
                sb.append(System.lineSeparator());
            }

        } catch (SQLException e) {
            System.err.println("Fehler beim Lesen des ResultSets: " + e.getMessage());
        }

        return sb.toString();
    }

    private ResultSet query1(Database db) throws SQLException {
        // Wieviele Produkte jeden Typs (Buch, Musik-CD, DVD) sind in der Datenbank erfasst? Hinweis: Geben Sie das Ergebnis in einer 3-spaltigen Relation aus.
        return db.executeQuery("""
                SELECT (
                    SELECT COUNT(*)
                    FROM cd
                ) as cds, (
                    SELECT COUNT(*)
                    FROM buch
                ) as buecher, (
                    SELECT COUNT(*)
                    FROM dvd
                ) as dvds;
                """);
    }

    private ResultSet query2(Database db) throws SQLException {
        // Nennen Sie die 5 besten Produkte jedes Typs (Buch, Musik-CD, DVD) sortiert nach dem durchschnittlichem Rating. Hinweis: Geben Sie das Ergebnis in einer einzigen Relation mit den Attributen Typ, ProduktNr, Rating aus. Wie werden gleiche durchschnittliche Ratings behandelt?

        // Es werden exakt 5 Produkte jedes Typs ausgegeben, die sortiert sind nach der Anzahl ihrer Bewertungen
        // bspw. ein Produkt mit einem Rating von 5/5, aber nur einer Bewertung hat eine niedrigere Aussagekraft als
        // ein Produkt mit Rating 5/5 und 5 Bewertungen.
        return db.executeQuery("""
            WITH alle_produkte AS (
                SELECT 'Buch' AS typ, p.produkt_id, p.rating,
                       COUNT(bw.kunde_id) AS bewertungen
                FROM buch b
                         JOIN produkt p ON b.produkt_id = p.produkt_id
                         LEFT JOIN bewertung bw ON p.produkt_id = bw.produkt_id
                GROUP BY p.produkt_id, p.rating
           \s
                UNION ALL
           \s
                SELECT 'CD' AS typ, p.produkt_id, p.rating,
                       COUNT(bw.kunde_id) AS bewertungen
                FROM cd
                         JOIN produkt p ON cd.produkt_id = p.produkt_id
                         LEFT JOIN bewertung bw ON p.produkt_id = bw.produkt_id
                GROUP BY p.produkt_id, p.rating
           \s
                UNION ALL
           \s
                SELECT 'DVD' AS typ, p.produkt_id, p.rating,
                       COUNT(bw.kunde_id) AS bewertungen
                FROM dvd
                         JOIN produkt p ON dvd.produkt_id = p.produkt_id
                         LEFT JOIN bewertung bw ON p.produkt_id = bw.produkt_id
                GROUP BY p.produkt_id, p.rating
            ),
                 bewertete_produkte_mit_rang AS (
                     SELECT
                         typ,
                         produkt_id,
                         rating,
                         RANK() OVER (
                             PARTITION BY typ
                             ORDER BY rating DESC NULLS LAST, bewertungen DESC, produkt_id
                             ) AS rang
                     FROM alle_produkte
                 )
            SELECT
                typ AS "Typ",
                produkt_id AS "ProduktNr",
                rating AS "Rating"
            FROM bewertete_produkte_mit_rang
            WHERE rang <= 5
            ORDER BY typ, rang;
""");
    }

    private ResultSet query3(Database db) throws SQLException {
        // Für welche Produkte gibt es im Moment kein Angebot?
        return db.executeQuery("""
SELECT DISTINCT p.produkt_id, p.titel
FROM produkt p
         LEFT JOIN filial_produkte fp ON p.produkt_id = fp.produkt_id
WHERE fp.preis IS NULL;
                """);
    }

    private ResultSet query4(Database db) throws SQLException {
        // Für welche Produkte ist das teuerste Angebot mehr als doppelt so teuer wie das preiswerteste?
        return db.executeQuery("""
SELECT
    produkt_id AS "ProduktNr",
    MIN(preis) AS "MinPreis",
    MAX(preis) AS "MaxPreis"
FROM filial_produkte
GROUP BY produkt_id
HAVING MAX(preis) > 2 * MIN(preis);
                """);
    }

    private ResultSet query5(Database db) throws SQLException {
        // Welche Produkte haben sowohl mindestens eine sehr schlechte (Punktzahl: 1) als auch mindestens eine sehr gute (Punktzahl: 5) Bewertung?
        return db.executeQuery("""
SELECT p.produkt_id, p.titel
FROM produkt p
WHERE EXISTS (
    SELECT 1
    FROM bewertung b1
    WHERE b1.produkt_id = p.produkt_id AND b1.sterne = 1
)
  AND EXISTS (
    SELECT 1
    FROM bewertung b2
    WHERE b2.produkt_id = p.produkt_id AND b2.sterne = 5
);

                """);
    }

    private ResultSet query6(Database db) throws SQLException {
        // Für wieviele Produkte gibt es gar keine Rezension?
        return db.executeQuery("""
SELECT COUNT(*) AS anzahl_ohne_rezension
FROM produkt p
WHERE NOT EXISTS (
    SELECT 1
    FROM bewertung b
    WHERE b.produkt_id = p.produkt_id
);

                """);
    }

    private ResultSet query7(Database db) throws SQLException {
        // Nennen Sie alle Rezensenten, die mindestens 10 Rezensionen geschrieben haben.
        return db.executeQuery("""
SELECT
    k.kunde_id,
    k.username,
    COUNT(b.produkt_id) AS anzahl_rezensionen
FROM kunde k
         JOIN bewertung b ON k.kunde_id = b.kunde_id
GROUP BY k.kunde_id, k.vorname, k.nachname
HAVING COUNT(b.produkt_id) >= 10
ORDER BY anzahl_rezensionen DESC;

                """);
    }

    private ResultSet query8(Database db) throws SQLException {
        // Geben Sie eine duplikatfreie und alphabetisch sortierte Liste der Namen aller Buchautoren an, die auch an DVDs oder Musik-CDs beteiligt sind.
        return db.executeQuery("""
SELECT DISTINCT name
FROM person
WHERE name IN (
    -- Alle Buchautoren (nach Name)
    SELECT name
    FROM person p1
             JOIN buch_autor ba ON p1.person_id = ba.person_id
    WHERE p1.rolle = 'author'
)
  AND name IN (
    -- Beteiligte an DVD (director, actor, creator)
    SELECT name
    FROM person p2
             JOIN dvd_beteiligte db ON p2.person_id = db.person_id
    WHERE p2.rolle IN ('director', 'actor', 'creator')

    UNION

    -- Beteiligte an CD (nur artist)
    SELECT name
    FROM person p3
             JOIN cd_kuenstler ck ON p3.person_id = ck.person_id
    WHERE p3.rolle = 'artist'
)
ORDER BY name ASC;

                """);
    }

    private ResultSet query9(Database db) throws SQLException {
        // Wie hoch ist die durchschnittliche Anzahl von Liedern einer Musik-CD?
        return db.executeQuery("""
SELECT AVG(titel_anzahl) AS durchschnitt_lieder_pro_cd
FROM (
         SELECT produkt_id, COUNT(*) AS titel_anzahl
         FROM musiktitel
         GROUP BY produkt_id
     ) AS cd_liedanzahl;
                """);
    }

    private ResultSet query10(Database db) throws SQLException {
        // Für welche Produkte gibt es ähnliche Produkte in einer anderen Hauptkategorie? Hinweis: Eine Hauptkategorie ist eine Produktkategorie ohne Oberkategorie. Erstellen Sie eine rekursive Anfrage, die zu jedem Produkt dessen Hauptkategorie bestimmt.
        return db.executeQuery("""
WITH RECURSIVE hauptkategorie(produkt_id, kategorie_id, hauptkategorie_id) AS (
    SELECT pk.produkt_id, pk.kategorie_id, pk.kategorie_id
    FROM produkt_kategorie pk

    UNION ALL

    SELECT hk.produkt_id, k.kategorie_id, k.oberkategorie
    FROM hauptkategorie hk
             JOIN kategorie k ON hk.hauptkategorie_id = k.kategorie_id
    WHERE k.oberkategorie IS NOT NULL
),

-- Reduziere auf oberste (root) Kategorie pro Produkt
               rootkategorien AS (
                   SELECT produkt_id, hauptkategorie_id
                   FROM hauptkategorie
                   WHERE hauptkategorie_id NOT IN (
                       SELECT kategorie_id FROM kategorie WHERE oberkategorie IS NOT NULL
                   )
               ),

-- Hauptkategorien ähnlicher Produkte verbinden
               aehnliche_hauptkategorien AS (
                   SELECT
                       ap.produkt_id AS produkt1,
                       ap.aehnliches_produkt_id AS produkt2,
                       r1.hauptkategorie_id AS hauptkat1,
                       r2.hauptkategorie_id AS hauptkat2
                   FROM aehnliche_produkte ap
                            JOIN rootkategorien r1 ON ap.produkt_id = r1.produkt_id
                            JOIN rootkategorien r2 ON ap.aehnliches_produkt_id = r2.produkt_id
                   WHERE r1.hauptkategorie_id <> r2.hauptkategorie_id
               )

SELECT DISTINCT produkt1
FROM aehnliche_hauptkategorien
ORDER BY produkt1;

                """);
    }

    private ResultSet query11(Database db) throws SQLException {
        // Welche Produkte werden in allen Filialen angeboten? Hinweis: Ihre Query muss so formuliert werden, dass sie für eine beliebige Anzahl von Filialen funktioniert. Hinweis: Beachten Sie, dass ein Produkt mehrfach von einer Filiale angeboten werden kann (z.B. neu und gebraucht).
        return db.executeQuery("""
SELECT fp.produkt_id
FROM filial_produkte fp
GROUP BY fp.produkt_id
HAVING COUNT(DISTINCT fp.filiale_id) = (
    SELECT COUNT(*) FROM filiale
);
                """);
    }

    private ResultSet query12(Database db) throws SQLException {
        // In wieviel Prozent der Fälle der Frage 11 gibt es in Leipzig das preiswerteste Angebot?
        return db.executeQuery("""
WITH produkte_alle_filialen AS (
    SELECT produkt_id
    FROM filial_produkte
    GROUP BY produkt_id
    HAVING COUNT(DISTINCT filiale_id) = (SELECT COUNT(*) FROM filiale)
), min_preise AS (
    SELECT produkt_id, MIN(preis) AS min_preis
    FROM filial_produkte
    GROUP BY produkt_id
), leipzig_guenstig AS (
    SELECT fp.produkt_id
    FROM filial_produkte fp
             JOIN filiale f ON fp.filiale_id = f.filiale_id
             JOIN min_preise mp ON fp.produkt_id = mp.produkt_id AND fp.preis = mp.min_preis
    WHERE f.name = 'Leipzig'
)
SELECT
    COUNT(*) AS anzahl_produkte_alle_filialen,
    (SELECT COUNT(*) FROM leipzig_guenstig WHERE produkt_id IN (SELECT produkt_id FROM produkte_alle_filialen)) AS leipzig_preiswert,
    ROUND(
            100.0 * (SELECT COUNT(*) FROM leipzig_guenstig WHERE produkt_id IN (SELECT produkt_id FROM produkte_alle_filialen))
                / NULLIF(COUNT(*), 0),
            2
    ) AS prozent_leipzig
FROM produkte_alle_filialen;
                """);
    }

}
