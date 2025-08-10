package dev.marisamatti.api.models;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "kategorie")
public class Kategorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kategorie_id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "oberkategorie")
    private Kategorie oberkategorie;

    @OneToMany
    @JoinColumn(name = "oberkategorie")
    private Set<Kategorie> kategories = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "produkt_kategorie",
            joinColumns = @JoinColumn(name = "kategorie_id"),
            inverseJoinColumns = @JoinColumn(name = "produkt_id"))
    private Set<Produkt> produkts = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Kategorie getOberkategorie() {
        return oberkategorie;
    }

    public void setOberkategorie(Kategorie oberkategorie) {
        this.oberkategorie = oberkategorie;
    }

    public Set<Kategorie> getKategories() {
        return kategories;
    }

    public void setKategories(Set<Kategorie> kategories) {
        this.kategories = kategories;
    }

    public Set<Produkt> getProdukts() {
        return produkts;
    }

    public void setProdukts(Set<Produkt> produkts) {
        this.produkts = produkts;
    }

}