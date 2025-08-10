package dev.numerotres.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "buch")
public class Buch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "produkt_id", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ColumnDefault("nextval('buch_produkt_id_seq')")
    @JoinColumn(name = "produkt_id", nullable = false)
    private Produkt produkt;

    @Column(name = "seitenzahl")
    private Integer seitenzahl;

    @Column(name = "erscheinungsdatum")
    private LocalDate erscheinungsdatum;

    @Column(name = "isbn", length = 30)
    private String isbn;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Produkt getProdukt() {
        return produkt;
    }

    public void setProdukt(Produkt produkt) {
        this.produkt = produkt;
    }

    public Integer getSeitenzahl() {
        return seitenzahl;
    }

    public void setSeitenzahl(Integer seitenzahl) {
        this.seitenzahl = seitenzahl;
    }

    public LocalDate getErscheinungsdatum() {
        return erscheinungsdatum;
    }

    public void setErscheinungsdatum(LocalDate erscheinungsdatum) {
        this.erscheinungsdatum = erscheinungsdatum;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

}