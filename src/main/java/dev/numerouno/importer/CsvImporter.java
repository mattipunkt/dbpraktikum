package dev.numerouno.importer;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import dev.numerouno.db.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final IntegrityLogger il = new IntegrityLogger();

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
                    String msg = "Product with ASIN " + review.getAsin() + " not found in the database. Review will be skipped.";
                    il.addError(IntegrityLogger.ErrorType.DB_ERROR, msg);
                    LOGGER.warn(msg);
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
                    String msg = "Customer could not be created or found for user " + review.getUser();
                    il.addError(IntegrityLogger.ErrorType.DB_ERROR, msg);
                    LOGGER.error(msg);
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
                String msg = "Error while saving review for ASIN " + review.getAsin() + ": " + e.getMessage();
                if (e instanceof SQLException) {
                    if (((SQLException) e).getSQLState().equals("23505")) {
                        il.addError(IntegrityLogger.ErrorType.INTEGRITY_CONFLICT, msg);
                    }
                    else {
                        il.addError(IntegrityLogger.ErrorType.DB_ERROR, msg);
                    }
                } else {
                    il.addError(IntegrityLogger.ErrorType.DB_ERROR, msg);
                }
                LOGGER.warn(msg, e);
            }
        }
        String filename = "csv-log-" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".txt";
        il.printProblemsToFile(new File(filename));
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
            String msg = "Too few columns in line " + (rowIndex + 1) + ": " + String.join(",", cols);
            LOGGER.warn(msg);
            il.addError(IntegrityLogger.ErrorType.INVALID_DATA, msg);
            return null;
        }
        Review review = new Review();
        try {
            // ASIN
            String asin = cols[0];
            if (asin == null || asin.trim().isEmpty()) {
                String msg = "ASIN is missing in line " + (rowIndex + 1) + ".";
                LOGGER.warn(msg);
                il.addError(IntegrityLogger.ErrorType.MISSING_DATA, msg);
                return null;
            }
            review.setAsin(asin.trim());

            // Rating
            int rating;
            try {
                rating = Integer.parseInt(cols[1]);
            } catch (NumberFormatException e) {
                String msg = "Invalid rating in line " + (rowIndex + 1) + ": " + cols[1];
                il.addError(IntegrityLogger.ErrorType.INVALID_DATA, msg);
                LOGGER.warn(msg);
                return null;
            }
            if (rating < 0 || rating > 5) {
                String msg = "Invalid rating in line " + (rowIndex + 1) + ": " + rating + ". Must be between 0 and 5.";
                il.addError(IntegrityLogger.ErrorType.INVALID_DATA, msg);
                LOGGER.warn(msg);
                return null;
            }
            review.setRating(rating);

            // Helpful
            int helpful;
            try {
                helpful = Integer.parseInt(cols[2]);
            } catch (NumberFormatException e) {
                String msg = "Helpful is not a valid integer in line " + (rowIndex + 1) + ": " + cols[2];
                il.addError(IntegrityLogger.ErrorType.INVALID_DATA, msg);
                LOGGER.warn(msg);
                return null;
            }
            if (helpful < 0) {
                String msg = "Helpful value is negative in line " + (rowIndex + 1) + ": " + helpful;
                il.addError(IntegrityLogger.ErrorType.INVALID_DATA, msg);
                LOGGER.warn(msg);
                return null;
            }
            review.setHelpful(helpful);

            // Date
            try {
                review.setReviewDate(cols[3]);
                if (review.getReviewDate() == null) {
                    String msg = "Invalid date in line " + (rowIndex + 1) + ": " + cols[3];
                    il.addError(IntegrityLogger.ErrorType.INVALID_DATA, msg);
                    LOGGER.warn(msg);
                    return null;
                }
            } catch (DateTimeParseException e) {
                String msg = "Invalid date format in line " + (rowIndex + 1) + ": " + cols[3];
                il.addError(IntegrityLogger.ErrorType.INVALID_DATA, msg);
                LOGGER.warn(msg);
                return null;
            }

            // User
            String user = cols[4];
            if (user == null || user.trim().isEmpty()) {
                String msg = "User is missing in line " + (rowIndex + 1) + ".";
                il.addError(IntegrityLogger.ErrorType.MISSING_DATA, msg);
                LOGGER.warn(msg);
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
            String msg = "Unknown error while parsing line " + (rowIndex + 1) + ": " + String.join(",", cols);
            il.addError(IntegrityLogger.ErrorType.UNKNOWN_ERROR, msg + " - " + e.getMessage());
            LOGGER.error(msg, e);
            return null;
        }
    }

}
