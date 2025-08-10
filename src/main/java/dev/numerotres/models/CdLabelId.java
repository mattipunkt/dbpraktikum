package dev.numerotres.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CdLabelId implements Serializable {
    private static final long serialVersionUID = -2887437054589731682L;
    @Column(name = "produkt_id", nullable = false)
    private Integer produktId;

    @Column(name = "label_id", nullable = false)
    private Integer labelId;

    public Integer getProduktId() {
        return produktId;
    }

    public void setProduktId(Integer produktId) {
        this.produktId = produktId;
    }

    public Integer getLabelId() {
        return labelId;
    }

    public void setLabelId(Integer labelId) {
        this.labelId = labelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CdLabelId entity = (CdLabelId) o;
        return Objects.equals(this.produktId, entity.produktId) &&
                Objects.equals(this.labelId, entity.labelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produktId, labelId);
    }

}