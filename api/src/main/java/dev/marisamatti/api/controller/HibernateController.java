package dev.marisamatti.api.controller;


import dev.marisamatti.api.models.Kategorie;
import dev.marisamatti.api.models.Produkt;
import dev.marisamatti.api.models.ProduktListDto;
import dev.marisamatti.api.repositories.KategorieRepository;
import dev.marisamatti.api.repositories.ProduktRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class HibernateController {

    private final ProduktRepository produktRepository;
    private final KategorieRepository kategorieRepository;

    public HibernateController(ProduktRepository repository, KategorieRepository kategorieRepository) {
        this.produktRepository = repository;
        this.kategorieRepository = kategorieRepository;
    }

    @GetMapping("/produkte")
    public List<ProduktListDto> getProducts() {
        return produktRepository.findAll().stream()
                .map(p -> {
                    ProduktListDto dto = new ProduktListDto();
                    dto.setId(p.getId());
                    dto.setTitel(p.getTitel());
                    return dto;
                })
                .toList();
    }

    @GetMapping("/produkt/{id}")
    public Produkt getProductById(@PathVariable Long id) {
        return produktRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produkt nicht gefunden"));
    }

    @GetMapping("/produkte/{pattern}")
    public List<Produkt> getProductsByPattern(@PathVariable String pattern) {
        return produktRepository.findAll().stream()
                .filter(p -> p.getTitel() != null && p.getTitel().toLowerCase().contains(pattern.toLowerCase()))
                .toList();
    }

    @GetMapping("/categories")
    public Set<Kategorie> getCatTree() {
        return kategorieRepository.getTopLevelCategories();
    }

    @PostMapping("/categories/products")
    public List<Produkt> getProductsByCategoryPath(@RequestBody String catpath) {
        return kategorieRepository.getProductsByCategoryPath(catpath);
    }

}
