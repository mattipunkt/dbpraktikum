package dev.marisamatti.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class VerkaufId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "filiale_id", nullable = false)
    private Integer filialeId;

    @Column(name = "produkt_id", nullable = false)
    private Integer produktId;

    public Integer getFilialeId() {
        return filialeId;
    }

    public void setFilialeId(Integer filialeId) {
        this.filialeId = filialeId;
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
        VerkaufId that = (VerkaufId) o;
        return Objects.equals(filialeId, that.filialeId) &&
                Objects.equals(produktId, that.produktId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filialeId, produktId);
    }
}
