package dev.numerotres.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "cd")
public class Cd {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "produkt_id", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ColumnDefault("nextval('cd_produkt_id_seq')")
    @JoinColumn(name = "produkt_id", nullable = false)
    private Produkt produkt;

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

    @OneToMany(mappedBy = "produkt")
    private Set<Musiktitel> musiktitels = new LinkedHashSet<>();

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