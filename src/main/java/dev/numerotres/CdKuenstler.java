package dev.numerotres;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "cd_kuenstler")
public class CdKuenstler {
    @EmbeddedId
    private CdKuenstlerId id;

    @MapsId("produktId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "produkt_id", nullable = false)
    private Cd produkt;

    @MapsId("personId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    public CdKuenstlerId getId() {
        return id;
    }

    public void setId(CdKuenstlerId id) {
        this.id = id;
    }

    public Cd getProdukt() {
        return produkt;
    }

    public void setProdukt(Cd produkt) {
        this.produkt = produkt;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

}