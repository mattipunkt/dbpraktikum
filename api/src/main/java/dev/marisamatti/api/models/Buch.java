package dev.marisamatti.api.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "buch")
@PrimaryKeyJoinColumn(name = "produkt_id")
public class Buch extends Produkt{

    @Column(name = "seitenzahl")
    private Integer seitenzahl;

    @Column(name = "erscheinungsdatum")
    private LocalDate erscheinungsdatum;

    @Column(name = "isbn", length = 30)
    private String isbn;

    @ManyToMany
    @JoinTable(name = "buch_autor",
            joinColumns = @JoinColumn(name = "produkt_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"))
    private Set<Person> people = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "buch_verlag",
            joinColumns = @JoinColumn(name = "produkt_id"),
            inverseJoinColumns = @JoinColumn(name = "verlag_id"))
    private Set<Verlag> verlags = new LinkedHashSet<>();


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

    public Set<Person> getPeople() {
        return people;
    }

    public void setPeople(Set<Person> people) {
        this.people = people;
    }

    public Set<Verlag> getVerlags() {
        return verlags;
    }

    public void setVerlags(Set<Verlag> verlags) {
        this.verlags = verlags;
    }

}