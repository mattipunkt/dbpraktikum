package dev.numerotres;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

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

    @OneToMany(mappedBy = "filiale")
    private Set<FilialProdukte> filialProduktes = new LinkedHashSet<>();

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

    public Set<FilialProdukte> getFilialProduktes() {
        return filialProduktes;
    }

    public void setFilialProduktes(Set<FilialProdukte> filialProduktes) {
        this.filialProduktes = filialProduktes;
    }

}