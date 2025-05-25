package dev.numerouno.importer;

public class Review {
    private String user;
    private String product;
    private String review;
    private String summary;
    private int rating;
    private int helpful;
    private String date;




    public Review(){}
    public String getUser(){
        return this.user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getProduct(){
        return this.product;
    }
    public void setProduct(String product) {
        this.product = product;
    }
    public String getReview(){
        return this.review;
    }
    public void setReview(String review) {
        this.review = review;
    }
    public String getSummary() {
        return this.summary;
    }
    public void setSummary(String summary){
        this.summary = summary;
    }
    public int getRating(){
        return this.rating;
    }
    public void setRating(int rating){
        this.rating = rating;
    }
    public int getHelpful(){
        return this.helpful;
    }
    public void setHelpful(int helpful) {
        this.helpful = helpful;
    }
    public String getDate(){
        return this.date;
    }
    public void setDate(String Date){
        this.date = date;
    }
}
