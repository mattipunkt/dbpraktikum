package dev.numerotres;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "produkt")
public class Produkt {
    @Id
//    @ColumnDefault("nextval('produkt_produkt_id_seq')")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "produkt_id", nullable = false)
    private Integer id;

    @Column(name = "asin", nullable = false, length = 50)
    private String asin;

    @Column(name = "titel", length = 300)
    private String titel;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "bild", length = 400)
    private String bild;

    @Column(name = "verkaufsrang")
    private Integer verkaufsrang;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getBild() {
        return bild;
    }

    public void setBild(String bild) {
        this.bild = bild;
    }

    public Integer getVerkaufsrang() {
        return verkaufsrang;
    }

    public void setVerkaufsrang(Integer verkaufsrang) {
        this.verkaufsrang = verkaufsrang;
    }

}