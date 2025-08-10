package dev.marisamatti.api.models;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "bewertung")
public class Bewertung {
    @EmbeddedId
    private BewertungId id;

    @MapsId("kundeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "kunde_id", nullable = false)
    private Kunde kunde;

    @MapsId("produktId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "produkt_id", nullable = false)
    private Produkt produkt;

    @Column(name = "rezension", length = 10000)
    private String rezension;

    @Column(name = "zusammenfassung", length = 15000)
    private String zusammenfassung;

    @Column(name = "sterne")
    private Integer sterne;

    @Column(name = "hilfreich")
    private Integer hilfreich;

    @Column(name = "datum")
    private LocalDate datum;

    public BewertungId getId() {
        return id;
    }

    public void setId(BewertungId id) {
        this.id = id;
    }

    public Kunde getKunde() {
        return kunde;
    }

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

    public Produkt getProdukt() {
        return produkt;
    }

    public void setProdukt(Produkt produkt) {
        this.produkt = produkt;
    }

    public String getRezension() {
        return rezension;
    }

    public void setRezension(String rezension) {
        this.rezension = rezension;
    }

    public String getZusammenfassung() {
        return zusammenfassung;
    }

    public void setZusammenfassung(String zusammenfassung) {
        this.zusammenfassung = zusammenfassung;
    }

    public Integer getSterne() {
        return sterne;
    }

    public void setSterne(Integer sterne) {
        this.sterne = sterne;
    }

    public Integer getHilfreich() {
        return hilfreich;
    }

    public void setHilfreich(Integer hilfreich) {
        this.hilfreich = hilfreich;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

}