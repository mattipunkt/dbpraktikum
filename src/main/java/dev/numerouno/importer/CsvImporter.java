package dev.numerouno.importer;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import dev.numerouno.db.Database;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;


public class CsvImporter extends FileImporter {
    public CsvImporter(Database db) {
        super(db);
    }

    @Override
    public void importFile(File file) throws IOException {
        // in DB Speichern?
    }
    private List<String[]> importCsv(String path) throws IOException {
        List<String[]> rows = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                rows.add(line);
            }
        } catch (CsvValidationException e) {
            throw new IOException("Fehler beim Lesen der CSV-Datei: " + e.getMessage(), e);
        }

        return rows;
    }

    public List<Review> parseReviews(String path) throws IOException {
        List<String[]> rows = importCsv(path);
        List<Review> reviews = new ArrayList<>();

        for (int i = 1; i < rows.size(); i++) {
            String[] cols = rows.get(i);
            Review review = new Review();

            review.setAsin(cols[0]);
            review.setRating(Integer.parseInt(cols[1]));
            review.setHelpful(Integer.parseInt(cols[2]));
            review.setReviewDate(cols[3]);
            review.setUser(cols[4]);
            review.setSummary(cols[5]);
            review.setReview(cols[6]);

            reviews.add(review);
        }

        return reviews;
    }

    public void saveReviewsToDatabase(List<Review> reviews, Database db) {
        for(Review review : reviews) {
            try {
                int produktId = -1;
                var rsProdukt = db.executeQuery("SELECT produkt_id FROM produkt WHERE asin = ? ", review.getAsin());
                if (rsProdukt.next()) {
                    produktId = rsProdukt.getInt("produkt_id");
                } else {
                    //fehler werfen? => Produkt gibt es nicht
                }

                int kundeId = -1;
                if(Objects.equals(review.getUser(), "guest" )) {
                    kundeId = db.executeUpdate("INSERT INTO kunde (gast) VALUES (?)", true);
                } else {
                    var rsKunde = db.executeQuery("SELECT kunde_id  FROM kunde WHERE username = ?", review.getUser());
                    if(rsKunde.next()) {
                        kundeId = rsKunde.getInt("kunde_id" );
                    }
                }

                if(kundeId == -1) {
                    throw new IllegalStateException("Kunde konnte nicht erstellt oder gefunden werden.");
                }

                db.executeUpdate("INSERT INTO bewertung (kunde_id, produkt_id, rezension, zusammenfassung, sterne, hilfreich, datum) VALUES (?, ?, ?, ?, ?, ?, ?) ",
                    kundeId,
                    produktId,
                    review.getReview(),
                    review.getSummary(),
                    review.getRating(),
                    review.getHelpful(),
                    review.getReviewDate() //format?    
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
