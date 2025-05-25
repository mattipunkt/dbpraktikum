package dev.numerouno.importer;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;



public class CsvImporter extends FileImporter {
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

            review.setProduct(cols[0]);
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
}
