package dev.numerotres.models;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "cd_label")
public class CdLabel {
    @EmbeddedId
    private CdLabelId id;

    @MapsId("produktId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "produkt_id", nullable = false)
    private Cd produkt;

    @MapsId("labelId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "label_id", nullable = false)
    private Label label;

    public CdLabelId getId() {
        return id;
    }

    public void setId(CdLabelId id) {
        this.id = id;
    }

    public Cd getProdukt() {
        return produkt;
    }

    public void setProdukt(Cd produkt) {
        this.produkt = produkt;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

}