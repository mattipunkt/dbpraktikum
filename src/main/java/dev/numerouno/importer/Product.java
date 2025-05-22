package dev.numerouno.importer;

import dev.numerouno.db.Database;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private final String asin;
    private String name;
    private double rating;
    private int rank;
    private String image;
    private List<Product> similarProducts = new ArrayList<>();
    private String condition;
    private double price;
    private String ean;

    public Product(String asin) {
        this.asin = asin;
    }

    public String getAsin() {
        return asin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }



    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Product> getSimilarProducts() {
        return similarProducts;
    }

    public void setSimilarProducts(List<Product> similarProducts) {
        this.similarProducts = similarProducts;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    @Override
    public String toString() {
        return "Product{" +
                "asin='" + asin + '\'' +
                ", name='" + name + '\'' +
                ", rating=" + rating +
                ", rank=" + rank +
                ", image='" + image + '\'' +
                ", similarProducts=" + similarProducts.toString() +
                ", condition='" + condition + '\'' +
                ", price=" + price +
                ", ean='" + ean + '\'' +
                '}';
    }

    public Product getOrCreate(Database database) {
        return new Product(null);
    }
}
