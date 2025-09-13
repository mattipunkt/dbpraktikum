package dev.marisamatti.api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "kategorie")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    @JsonBackReference(value = "kategorie-children")
    private Kategorie oberkategorie;

    @OneToMany
    @JsonManagedReference(value = "kategorie-children")
    @JoinColumn(name = "oberkategorie")
    private Set<Kategorie> kategorien = new LinkedHashSet<>();

    @ManyToMany
    @JsonBackReference(value = "produkt-kategorie")
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

    public Set<Kategorie> getKategorien() {
        return kategorien;
    }

    public void setKategorien(Set<Kategorie> kategorien) {
        this.kategorien = kategorien;
    }

    public Set<Produkt> getProdukts() {
        return produkts;
    }

    public void setProdukts(Set<Produkt> produkts) {
        this.produkts = produkts;
    }

}