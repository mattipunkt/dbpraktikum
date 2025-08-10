package dev.numerotres.models;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "dvd_beteiligte")
public class DvdBeteiligte {
    @EmbeddedId
    private DvdBeteiligteId id;

    @MapsId("produktId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "produkt_id", nullable = false)
    private Dvd produkt;

    @MapsId("personId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    public DvdBeteiligteId getId() {
        return id;
    }

    public void setId(DvdBeteiligteId id) {
        this.id = id;
    }

    public Dvd getProdukt() {
        return produkt;
    }

    public void setProdukt(Dvd produkt) {
        this.produkt = produkt;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

}