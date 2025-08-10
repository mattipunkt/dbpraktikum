package dev.numerotres.models;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "buch_autor")
public class BuchAutor {
    @EmbeddedId
    private BuchAutorId id;

    @MapsId("produktId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "produkt_id", nullable = false)
    private Buch produkt;

    @MapsId("personId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    public BuchAutorId getId() {
        return id;
    }

    public void setId(BuchAutorId id) {
        this.id = id;
    }

    public Buch getProdukt() {
        return produkt;
    }

    public void setProdukt(Buch produkt) {
        this.produkt = produkt;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

}