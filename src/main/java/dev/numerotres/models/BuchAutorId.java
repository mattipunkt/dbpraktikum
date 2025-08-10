package dev.numerotres.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BuchAutorId implements Serializable {
    private static final long serialVersionUID = -8876329124376616993L;
    @Column(name = "produkt_id", nullable = false)
    private Integer produktId;

    @Column(name = "person_id", nullable = false)
    private Integer personId;

    public Integer getProduktId() {
        return produktId;
    }

    public void setProduktId(Integer produktId) {
        this.produktId = produktId;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BuchAutorId entity = (BuchAutorId) o;
        return Objects.equals(this.produktId, entity.produktId) &&
                Objects.equals(this.personId, entity.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produktId, personId);
    }

}