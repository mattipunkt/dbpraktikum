package dev.marisamatti.api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "kunde")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Kunde {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kunde_id", nullable = false)
    private Integer id;

    @Column(name = "gast")
    private Boolean gast;

    @Column(name = "vorname", length = 40)
    private String vorname;

    @Column(name = "nachname", length = 40)
    private String nachname;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "kontonummer")
    private Integer kontonummer;

    @Column(name = "adresse_strasse", length = 100)
    private String adresseStrasse;

    @Column(name = "adresse_plz", length = 5)
    private String adressePlz;

    @Column(name = "adresse_ort", length = 50)
    private String adresseOrt;

    @OneToMany
    @JoinColumn(name = "kunde_id")
    private Set<Bestellung> bestellungs = new LinkedHashSet<>();

    @OneToMany
    @JoinColumn(name = "kunde_id")
    @JsonBackReference(value = "kunde-bewertung")
    private Set<Bewertung> bewertungs = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getGast() {
        return gast;
    }

    public void setGast(Boolean gast) {
        this.gast = gast;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getKontonummer() {
        return kontonummer;
    }

    public void setKontonummer(Integer kontonummer) {
        this.kontonummer = kontonummer;
    }

    public String getAdresseStrasse() {
        return adresseStrasse;
    }

    public void setAdresseStrasse(String adresseStrasse) {
        this.adresseStrasse = adresseStrasse;
    }

    public String getAdressePlz() {
        return adressePlz;
    }

    public void setAdressePlz(String adressePlz) {
        this.adressePlz = adressePlz;
    }

    public String getAdresseOrt() {
        return adresseOrt;
    }

    public void setAdresseOrt(String adresseOrt) {
        this.adresseOrt = adresseOrt;
    }

    public Set<Bestellung> getBestellungs() {
        return bestellungs;
    }

    public void setBestellungs(Set<Bestellung> bestellungs) {
        this.bestellungs = bestellungs;
    }

    public Set<Bewertung> getBewertungs() {
        return bewertungs;
    }

    public void setBewertungs(Set<Bewertung> bewertungs) {
        this.bewertungs = bewertungs;
    }

}