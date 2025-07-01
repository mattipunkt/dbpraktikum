package dev.numerotres;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "aehnliche_produkte")
public class AehnlicheProdukte {
    @EmbeddedId
    private AehnlicheProdukteId id;

    @MapsId("produktId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "produkt_id", nullable = false)
    private Produkt produkt;

    @MapsId("aehnlichesProduktId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "aehnliches_produkt_id", nullable = false)
    private Produkt aehnlichesProdukt;

    public AehnlicheProdukteId getId() {
        return id;
    }

    public void setId(AehnlicheProdukteId id) {
        this.id = id;
    }

    public Produkt getProdukt() {
        return produkt;
    }

    public void setProdukt(Produkt produkt) {
        this.produkt = produkt;
    }

    public Produkt getAehnlichesProdukt() {
        return aehnlichesProdukt;
    }

    public void setAehnlichesProdukt(Produkt aehnlichesProdukt) {
        this.aehnlichesProdukt = aehnlichesProdukt;
    }

}