package dev.numerotres.models;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 150)
    private String name;

    @Column(name = "rolle", length = 50)
    private String rolle;

    @Column(name = "alias", length = 50)
    private String alias;

    @ManyToMany
    private Set<Buch> buches = new LinkedHashSet<>();

    @ManyToMany
    private Set<Cd> cds = new LinkedHashSet<>();

    @ManyToMany
    private Set<Dvd> dvds = new LinkedHashSet<>();

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

    public String getRolle() {
        return rolle;
    }

    public void setRolle(String rolle) {
        this.rolle = rolle;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Set<Buch> getBuches() {
        return buches;
    }

    public void setBuches(Set<Buch> buches) {
        this.buches = buches;
    }

    public Set<Cd> getCds() {
        return cds;
    }

    public void setCds(Set<Cd> cds) {
        this.cds = cds;
    }

    public Set<Dvd> getDvds() {
        return dvds;
    }

    public void setDvds(Set<Dvd> dvds) {
        this.dvds = dvds;
    }

}