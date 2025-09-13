package dev.marisamatti.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

public class ProduktListDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "produkt_id", nullable = false)
    private Integer id;

    @Column(name = "asin", nullable = false, length = 50)
    private String asin;

    @Column(name = "titel", length = 300)
    private String titel;

    // Neu: Produkt-Typ (CD, DVD, Buch)
    private String typ;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "bild", length = 400)
    private String bild;

    @Column(name = "verkaufsrang")
    private Integer verkaufsrang;


    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "produkt_kategorie",
            joinColumns = @JoinColumn(name = "produkt_id"),
            inverseJoinColumns = @JoinColumn(name = "kategorie_id")
    )
    private Set<Kategorie> kategories = new LinkedHashSet<>();

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

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
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

    public Set<Kategorie> getKategories() {
        return kategories;
    }

    public void setKategories(Set<Kategorie> kategories) {
        this.kategories = kategories;
    }

    public static ProduktListDto fromProdukt(Produkt p) {
        ProduktListDto dto = new ProduktListDto();
        dto.setId(p.getId());
        dto.setAsin(p.getAsin());
        dto.setTitel(p.getTitel());
        dto.setRating(p.getRating());
        dto.setBild(p.getBild());
        dto.setVerkaufsrang(p.getVerkaufsrang());
        String typ;
        if (p instanceof Buch) {
            typ = "Buch";
        } else if (p instanceof Cd) {
            typ = "CD";
        } else if (p instanceof Dvd) {
            typ = "DVD";
        } else {
            typ = "Produkt";
        }
        dto.setTyp(typ);
        return dto;
    }

}
