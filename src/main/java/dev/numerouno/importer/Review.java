package dev.numerouno.importer;

import java.util.Date;

public class Review {
    private String user;
    private String asin;
    private String review;
    private String summary;
    private int rating;
    private int helpful;
    private Date reviewDate;
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
        this.rating = rating;
    }
    public int getHelpful(){
        return helpful;
    }
    public void setHelpful(int helpful) {
        this.helpful = helpful;
    }
    public Date getReviewDate(){
        return reviewDate;
    }
    public void setReviewDate(String reviewDate){
        // TODO validate format
        this.reviewDate = new Date(reviewDate);
    }
}
