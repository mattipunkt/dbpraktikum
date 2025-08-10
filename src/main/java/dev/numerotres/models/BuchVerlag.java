package dev.numerotres.models;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "buch_verlag")
public class BuchVerlag {
    @EmbeddedId
    private BuchVerlagId id;

    @MapsId("produktId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "produkt_id", nullable = false)
    private Buch produkt;

    @MapsId("verlagId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "verlag_id", nullable = false)
    private Verlag verlag;

    public BuchVerlagId getId() {
        return id;
    }

    public void setId(BuchVerlagId id) {
        this.id = id;
    }

    public Buch getProdukt() {
        return produkt;
    }

    public void setProdukt(Buch produkt) {
        this.produkt = produkt;
    }

    public Verlag getVerlag() {
        return verlag;
    }

    public void setVerlag(Verlag verlag) {
        this.verlag = verlag;
    }

}