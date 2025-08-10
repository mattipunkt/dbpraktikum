package dev.marisamatti.api.models;

import jakarta.persistence.*;

@Entity
@Table(name = "verlag")
public class Verlag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verlag_id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 100)
    private String name;

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

}