package dev.marisamatti.api.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "cd")
@PrimaryKeyJoinColumn(name = "produkt_id")
public class Cd extends Produkt {

    @Column(name = "erscheinungsdatum")
    private LocalDate erscheinungsdatum;

    @ManyToMany
    @JoinTable(name = "cd_kuenstler",
            joinColumns = @JoinColumn(name = "produkt_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"))
    private Set<Person> people = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "cd_label",
            joinColumns = @JoinColumn(name = "produkt_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id"))
    private Set<Label> labels = new LinkedHashSet<>();

    @OneToMany
    @JoinColumn(name = "produkt_id")
    @JsonManagedReference(value = "cd-musiktitel")
    private Set<Musiktitel> musiktitels = new LinkedHashSet<>();

    public LocalDate getErscheinungsdatum() {
        return erscheinungsdatum;
    }

    public void setErscheinungsdatum(LocalDate erscheinungsdatum) {
        this.erscheinungsdatum = erscheinungsdatum;
    }

    public Set<Person> getPeople() {
        return people;
    }

    public void setPeople(Set<Person> people) {
        this.people = people;
    }

    public Set<Label> getLabels() {
        return labels;
    }

    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }

    public Set<Musiktitel> getMusiktitels() {
        return musiktitels;
    }

    public void setMusiktitels(Set<Musiktitel> musiktitels) {
        this.musiktitels = musiktitels;
    }

}