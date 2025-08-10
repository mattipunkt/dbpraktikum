package dev.numerotres.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AehnlicheProdukteId implements Serializable {
    private static final long serialVersionUID = -2223974629620974144L;
    @Column(name = "produkt_id", nullable = false)
    private Integer produktId;

    @Column(name = "aehnliches_produkt_id", nullable = false)
    private Integer aehnlichesProduktId;

    public Integer getProduktId() {
        return produktId;
    }

    public void setProduktId(Integer produktId) {
        this.produktId = produktId;
    }

    public Integer getAehnlichesProduktId() {
        return aehnlichesProduktId;
    }

    public void setAehnlichesProduktId(Integer aehnlichesProduktId) {
        this.aehnlichesProduktId = aehnlichesProduktId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AehnlicheProdukteId entity = (AehnlicheProdukteId) o;
        return Objects.equals(this.aehnlichesProduktId, entity.aehnlichesProduktId) &&
                Objects.equals(this.produktId, entity.produktId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aehnlichesProduktId, produktId);
    }

}