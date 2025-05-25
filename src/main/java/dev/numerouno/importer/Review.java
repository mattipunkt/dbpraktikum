package dev.numerouno.importer;

public class Review {
    private String user;
    private String product;
    private String review;
    private String summary;
    private int rating;
    private int helpful;
    private String reviewDate;




    public Review(){}
    public String getUser(){
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getProduct(){
        return product;
    }
    public void setProduct(String product) {
        this.product = product;
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
    public String getReviewDate(){
        return reviewDate;
    }
    public void setReviewDate(String Date){
        this.reviewDate = reviewDate;
    }
}
