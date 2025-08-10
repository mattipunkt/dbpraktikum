package dev.numerotres.models;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "bestellung")
public class Bestellung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bestell_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "kunde_id")
    private Kunde kunde;

    @Column(name = "zeit")
    private LocalTime zeit;

    @OneToMany(mappedBy = "bestell")
    private Set<BestellungProdukte> bestellungProduktes = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Kunde getKunde() {
        return kunde;
    }

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

    public LocalTime getZeit() {
        return zeit;
    }

    public void setZeit(LocalTime zeit) {
        this.zeit = zeit;
    }

    public Set<BestellungProdukte> getBestellungProduktes() {
        return bestellungProduktes;
    }

    public void setBestellungProduktes(Set<BestellungProdukte> bestellungProduktes) {
        this.bestellungProduktes = bestellungProduktes;
    }

}