package dev.marisamatti.api.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "filiale")
public class Filiale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "filiale_id", nullable = false)
    private Integer id;

    @Column(name = "anschrift", length = 200)
    private String anschrift;

    @Column(name = "name", length = 50)
    private String name;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAnschrift() {
        return anschrift;
    }

    public void setAnschrift(String anschrift) {
        this.anschrift = anschrift;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}