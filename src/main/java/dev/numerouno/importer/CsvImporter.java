package dev.numerouno.importer;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import dev.numerouno.db.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Imports and processes customer reviews from a CSV file and stores them in a database.
 * This class extends {@link FileImporter} and handles parsing, validation, and persistence of reviews.
 */
public class CsvImporter extends FileImporter {
    private static final Logger LOGGER = LogManager.getLogger(CsvImporter.class);

    /**
     * Constructs a CsvImporter with the given database reference.
     *
     * @param db The database instance.
     */
    public CsvImporter(Database db) {
        super(db);
    }

    @Override
    public void importFile(File file) throws IOException {
    }

    /**
     * Parses reviews from a CSV file at the given path.
     *
     * @param path The path to the CSV file.
     * @return A list of valid Review objects.
     * @throws IOException If reading the file fails.
     */
    public List<Review> parseReviews(String path) throws IOException {
        List<String[]> rows = importCsv(path);
        List<Review> reviews = new ArrayList<>();

        // Skip Header
        for (int i = 1; i < rows.size(); i++) {
            String[] cols = rows.get(i);
            Review review = createValidReview(cols, i);

            if (review != null) {
                reviews.add(review);
            } else {
                LOGGER.warn("Review in line {} was skipped.", i + 1);
            }
        }

        return reviews;
    }

    /**
     * Saves a list of reviews to the database.
     * Skips reviews where the product or customer cannot be resolved.
     *
     * @param reviews The list of reviews to save.
     * @param db      The database instance to use for saving.
     */
    public void saveReviewsToDatabase(List<Review> reviews, Database db) {
        for (Review review : reviews) {
            try {
                int produktId = -1;
                // Find product ID by ASIN
                var rsProdukt = db.executeQuery("SELECT produkt_id FROM produkt WHERE asin = ?", review.getAsin());
                if (rsProdukt.next()) {
                    produktId = rsProdukt.getInt("produkt_id");
                } else {
                    LOGGER.warn("Product with ASIN {} not found in the database. Review will be skipped.", review.getAsin());
                    continue;
                }

                int kundeId = -1;
                // Handle guest user or find customer ID by username
                if (Objects.equals(review.getUser(), "guest")) {
                    kundeId = db.executeUpdate("INSERT INTO kunde (gast) VALUES (?)", true);
                } else {
                    var rsKunde = db.executeQuery("SELECT kunde_id  FROM kunde WHERE username = ?", review.getUser());
                    if (rsKunde.next()) {
                        kundeId = rsKunde.getInt("kunde_id");
                    } else {
                        kundeId = db.executeUpdate("INSERT INTO kunde (username) VALUES (?)", review.getUser());
                    }
                }

                if (kundeId == -1) {
                    LOGGER.error("Customer could not be created or found.");
                    continue;
                }

                // Insert review into database
                db.executeUpdate("INSERT INTO bewertung (kunde_id, produkt_id, rezension, zusammenfassung, sterne, hilfreich, datum) VALUES (?, ?, ?, ?, ?, ?, ?) ",
                        kundeId,
                        produktId,
                        review.getReview(),
                        review.getSummary(),
                        review.getRating(),
                        review.getHelpful(),
                        review.getReviewDate()
                );
            } catch (Exception e) {
                LOGGER.error("Error while saving review for ASIN {}", review.getAsin(), e);
            }
        }
    }

    /**
     * Imports and parses a CSV file into rows.
     *
     * @param path The path to the CSV file.
     * @return A list of string arrays representing the CSV rows.
     * @throws IOException If the file cannot be read or parsed.
     */
    private List<String[]> importCsv(String path) throws IOException {
        List<String[]> rows = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                rows.add(line);
            }
        } catch (CsvValidationException e) {
            throw new IOException("Error reading CSV file: " + e.getMessage(), e);
        }

        return rows;
    }

    /**
     * Creates a Review object from a CSV row if all fields are valid.
     *
     * @param cols     The columns of a single CSV row.
     * @param rowIndex The index of the row (for logging purposes).
     * @return A valid Review object or null if validation fails.
     */
    private Review createValidReview(String[] cols, int rowIndex) {
        if (cols.length < 7) {
            LOGGER.warn("Too few columns in line {}: {}", rowIndex + 1, String.join(",", cols));
            return null;
        }
        Review review = new Review();
        try {
            // ASIN
            String asin = cols[0];
            if (asin == null || asin.trim().isEmpty()) {
                LOGGER.warn("ASIN is missing in line {}.", rowIndex + 1);
                return null;
            }
            review.setAsin(asin.trim());

            // Rating
            int rating;
            try {
                rating = Integer.parseInt(cols[1]);
            } catch (NumberFormatException e) {
                LOGGER.warn("Rating is not a valid integer in line {}: {}", rowIndex + 1, cols[1]);
                return null;
            }
            if (rating < 0 || rating > 5) {
                LOGGER.warn("Invalid rating in line {}: {}. Must be between 0 and 5.", rowIndex + 1, rating);
                return null;
            }
            review.setRating(rating);

            // Helpful
            int helpful;
            try {
                helpful = Integer.parseInt(cols[2]);
            } catch (NumberFormatException e) {
                LOGGER.warn("Helpful is not a valid integer in line {}: {}", rowIndex + 1, cols[2]);
                return null;
            }
            if (helpful < 0) {
                LOGGER.warn("Helpful value is negative in line {}: {}", rowIndex + 1, helpful);
                return null;
            }
            review.setHelpful(helpful);

            // Datum
            try {
                review.setReviewDate(cols[3]);
                if (review.getReviewDate() == null) {
                    LOGGER.warn("Invalid date in line {}: {}", rowIndex + 1, cols[3]);
                    return null;
                }
            } catch (DateTimeParseException e) {
                LOGGER.warn("Invalid date format in line {}: {}", rowIndex + 1, cols[3]);
                return null;
            }

            // User
            String user = cols[4];
            if (user == null || user.trim().isEmpty()) {
                LOGGER.warn("User is missing in line {}.", rowIndex + 1);
                return null;
            }
            review.setUser(user.trim());

            // Summary
            String summary = cols[5];
            review.setSummary(summary != null ? summary.trim() : "");

            // Review
            String reviewText = cols[6];
            review.setReview(reviewText != null ? reviewText.trim() : "");

            return review;

        } catch (Exception e) {
            LOGGER.error("Unknown error while parsing line {}: {}", rowIndex + 1, String.join(",", cols), e);
            return null;
        }
    }

}
