package dev.marisamatti.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BewertungDto {
    private Integer rating;
    private String name;
    private String mail;

    @JsonProperty("rating-text")
    private String ratingText;

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getRatingText() {
        return ratingText;
    }

    public void setRatingText(String ratingText) {
        this.ratingText = ratingText;
    }
}
