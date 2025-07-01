package dev.numerotres;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "bestellung_produkte")
public class BestellungProdukte {
    @EmbeddedId
    private BestellungProdukteId id;

    @MapsId("bestellId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "bestell_id", nullable = false)
    private Bestellung bestell;

    public BestellungProdukteId getId() {
        return id;
    }

    public void setId(BestellungProdukteId id) {
        this.id = id;
    }

    public Bestellung getBestell() {
        return bestell;
    }

    public void setBestell(Bestellung bestell) {
        this.bestell = bestell;
    }

}