package dev.numerotres;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BestellungProdukteId implements Serializable {
    private static final long serialVersionUID = -7019704372932935888L;
    @Column(name = "bestell_id", nullable = false)
    private Integer bestellId;

    @Column(name = "produkt_id", nullable = false)
    private Integer produktId;

    public Integer getBestellId() {
        return bestellId;
    }

    public void setBestellId(Integer bestellId) {
        this.bestellId = bestellId;
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
        BestellungProdukteId entity = (BestellungProdukteId) o;
        return Objects.equals(this.produktId, entity.produktId) &&
                Objects.equals(this.bestellId, entity.bestellId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produktId, bestellId);
    }

}