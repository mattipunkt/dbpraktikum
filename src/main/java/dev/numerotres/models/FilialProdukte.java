package dev.numerotres.models;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "filial_produkte")
public class FilialProdukte {
    @EmbeddedId
    private FilialProdukteId id;

    @MapsId("filialeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "filiale_id", nullable = false)
    private Filiale filiale;

    @MapsId("produktId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "produkt_id", nullable = false)
    private Produkt produkt;

    @Column(name = "preis")
    private Double preis;

    @Column(name = "zustand", length = 20)
    private String zustand;

    public FilialProdukteId getId() {
        return id;
    }

    public void setId(FilialProdukteId id) {
        this.id = id;
    }

    public Filiale getFiliale() {
        return filiale;
    }

    public void setFiliale(Filiale filiale) {
        this.filiale = filiale;
    }

    public Produkt getProdukt() {
        return produkt;
    }

    public void setProdukt(Produkt produkt) {
        this.produkt = produkt;
    }

    public Double getPreis() {
        return preis;
    }

    public void setPreis(Double preis) {
        this.preis = preis;
    }

    public String getZustand() {
        return zustand;
    }

    public void setZustand(String zustand) {
        this.zustand = zustand;
    }

}