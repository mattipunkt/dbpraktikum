package dev.marisamatti.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BewertungId implements Serializable {
    private static final long serialVersionUID = -1144911783892130486L;
    @Column(name = "kunde_id", nullable = false)
    private Integer kundeId;

    @Column(name = "produkt_id", nullable = false)
    private Integer produktId;

    public Integer getKundeId() {
        return kundeId;
    }

    public void setKundeId(Integer kundeId) {
        this.kundeId = kundeId;
    }

    public Integer getProduktId() {
        return produktId;
    }

    public void setProduktId(Integer produktId) {
        this.produktId = produktId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BewertungId entity = (BewertungId) o;
        return Objects.equals(this.produktId, entity.produktId) &&
                Objects.equals(this.kundeId, entity.kundeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produktId, kundeId);
    }

}