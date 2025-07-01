package dev.numerotres;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BuchVerlagId implements Serializable {
    private static final long serialVersionUID = 5348870252785797644L;
    @Column(name = "produkt_id", nullable = false)
    private Integer produktId;

    @Column(name = "verlag_id", nullable = false)
    private Integer verlagId;

    public Integer getProduktId() {
        return produktId;
    }

    public void setProduktId(Integer produktId) {
        this.produktId = produktId;
    }

    public Integer getVerlagId() {
        return verlagId;
    }

    public void setVerlagId(Integer verlagId) {
        this.verlagId = verlagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BuchVerlagId entity = (BuchVerlagId) o;
        return Objects.equals(this.verlagId, entity.verlagId) &&
                Objects.equals(this.produktId, entity.produktId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(verlagId, produktId);
    }

}