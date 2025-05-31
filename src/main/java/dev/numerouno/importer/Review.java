package dev.numerouno.importer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class Review {
    private static final Logger LOGGER = LogManager.getLogger(Review.class);
    private String user;
    private String asin;
    private String review;
    private String summary;
    private int rating;
    private int helpful;
    private LocalDate reviewDate;
    public Review(){}
    public String getUser(){
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getAsin(){
        return asin;
    }
    public void setAsin(String product) {
        this.asin = product;
    }
    public String getReview(){
        return review;
    }
    public void setReview(String review) {
        this.review = review;
    }
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary){
        this.summary = summary;
    }
    public int getRating(){
        return rating;
    }
    public void setRating(int rating){
        if(rating > 0 && rating <=5 ) {
            this.rating = rating;
        } else {
            LOGGER.error(rating + " is not a valid rating. Rating should be 0 trough 5");
        }
    }
    public int getHelpful(){
        return helpful;
    }
    public void setHelpful(int helpful) {
        this.helpful = helpful;
    }
    public LocalDate getReviewDate(){
        return reviewDate;
    }
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
