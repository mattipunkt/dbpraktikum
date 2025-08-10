package dev.marisamatti.api.models;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "musiktitel")
public class Musiktitel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "titel_id", nullable = false)
    private Integer id;

    @Column(name = "nr")
    private Integer nr;

    @Column(name = "name", length = 200)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "produkt_id")
    private Cd produkt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNr() {
        return nr;
    }

    public void setNr(Integer nr) {
        this.nr = nr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cd getProdukt() {
        return produkt;
    }

    public void setProdukt(Cd produkt) {
        this.produkt = produkt;
    }

}