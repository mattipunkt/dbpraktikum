package dev.marisamatti.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "produkt")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Produkt {
    @Id
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

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "aehnliche_produkte",
            joinColumns = @JoinColumn(name = "produkt_id"),
            inverseJoinColumns = @JoinColumn(name = "aehnliches_produkt_id")
    )
    private Set<Produkt> aehnlicheProdukte = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany
    @JoinColumn(name = "produkt_id")
    private Set<Bewertung> bewertungs = new LinkedHashSet<>();

    @ManyToMany
    @JsonManagedReference
    @JoinTable(
            name = "produkt_kategorie",
            joinColumns = @JoinColumn(name = "produkt_id"),
            inverseJoinColumns = @JoinColumn(name = "kategorie_id")
    )
    private Set<Kategorie> kategories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "produkt")
    @JsonIgnore
    private Set<Verkauf> verkauefe = new LinkedHashSet<>();

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

    public Set<Produkt> getAehnlicheProdukte() {
        return aehnlicheProdukte;
    }

    public void setAehnlicheProdukte(Set<Produkt> produkts) {
        this.aehnlicheProdukte = produkts;
    }

    public Set<Bewertung> getBewertungs() {
        return bewertungs;
    }

    public void setBewertungs(Set<Bewertung> bewertungs) {
        this.bewertungs = bewertungs;
    }

    public Set<Kategorie> getKategories() {
        return kategories;
    }

    public void setKategories(Set<Kategorie> kategories) {
        this.kategories = kategories;
    }

    public Set<Verkauf> getVerkauefe() {
        return verkauefe;
    }

    public void setVerkauefe(Set<Verkauf> verkauefe) {
        this.verkauefe = verkauefe;
    }

}