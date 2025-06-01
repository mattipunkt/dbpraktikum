package dev.numerouno.importer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Repräsentiert eine Produktbewertung mit Details wie Benutzer, Produkt-ASIN, Bewertungstext, Zusammenfassung,
 * Bewertungspunktzahl, hilfreiche Stimmen und das Bewertungsdatum.
 */
public class Review {
    private static final Logger LOGGER = LogManager.getLogger(Review.class);
    private String user;
    private String asin;
    private String review;
    private String summary;
    private int rating;
    private int helpful;
    private LocalDate reviewDate;

    /**
     * Standardkonstruktor für Review.
     */
    public Review(){}

    /**
     * Gibt den Benutzernamen des Rezensenten zurück.
     * @return Benutzername als String
     */
    public String getUser(){
        return user;
    }


    /**
     * Setzt den Benutzernamen des Rezensenten.
     * @param user Benutzername als String
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Gibt die ASIN (Amazon Standard Identification Number) des Produkts zurück.
     * @return ASIN als String
     */
    public String getAsin(){
        return asin;
    }


    /**
     * Setzt die ASIN des Produkts.
     * @param product ASIN als String
     */
    public void setAsin(String product) {
        this.asin = product;
    }

    /**
     * Gibt den vollständigen Bewertungstext zurück.
     * @return Bewertungstext als String
     */
    public String getReview(){
        return review;
    }

    /**
     * Setzt den Bewertungstext.
     * @param review Bewertungstext als String
     */
    public void setReview(String review) {
        this.review = review;
    }

    /**
     * Gibt die kurze Zusammenfassung der Bewertung zurück.
     * @return Zusammenfassung als String
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Setzt die Zusammenfassung der Bewertung.
     * @param summary Zusammenfassung als String
     */
    public void setSummary(String summary){
        this.summary = summary;
    }

    /**
     * Gibt die Bewertungspunktzahl zurück.
     * @return Bewertung als int (z.B. Anzahl Sterne)
     */
    public int getRating(){
        return rating;
    }

    /**
     * Setzt die Bewertungspunktzahl.
     * @param rating Bewertung als int
     */
    public void setRating(int rating){
            this.rating = rating;
    }

    /**
     * Gibt die Anzahl der hilfreichen Stimmen zurück.
     * @return Anzahl hilfreicher Stimmen als int
     */
    public int getHelpful(){
        return helpful;
    }

    /**
     * Setzt die Anzahl der hilfreichen Stimmen.
     * @param helpful Anzahl hilfreicher Stimmen als int
     */
    public void setHelpful(int helpful) {
        this.helpful = helpful;
    }


    /**
     * Gibt das Datum der Bewertung zurück.
     * @return Bewertungsdatum als LocalDate
     */
    public LocalDate getReviewDate(){
        return reviewDate;
    }

    /**
     * Setzt das Datum der Bewertung. Das Datum muss im Format "yyyy-MM-dd" übergeben werden.
     * Falls das Datum null, leer oder im falschen Format ist, wird eine Fehlermeldung geloggt und
     * das Datum nicht gesetzt.
     *
     * @param reviewDate Bewertungsdatum als String im Format "yyyy-MM-dd"
     */
    public void setReviewDate(String reviewDate){
        if (reviewDate == null || reviewDate.trim().isEmpty()) {
            LOGGER.error("Review date is null or empty.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            this.reviewDate = LocalDate.parse(reviewDate, formatter);
        } catch (DateTimeParseException e) {
            LOGGER.error("Date format is not yyyy-MM-dd.");
        }

    }
}
