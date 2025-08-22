package dev.marisamatti.api.controller;


import dev.marisamatti.api.models.Kategorie;
import dev.marisamatti.api.models.Produkt;
import dev.marisamatti.api.models.ProduktListDto;
import dev.marisamatti.api.repositories.ProduktRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HibernateController {

    private final ProduktRepository repository;

    public HibernateController(ProduktRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/produkte")
    public List<ProduktListDto> getProducts() {
        return repository.findAll().stream()
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
        return repository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Produkt nicht gefunden"));
    }

    @GetMapping("/produkte/{pattern}")
    public List<Produkt> getProductsByPattern(@PathVariable String pattern) {
        return repository.findAll().stream()
                .filter(p -> p.getTitel() != null && p.getTitel().toLowerCase().contains(pattern.toLowerCase()))
                .toList();
    }

    @GetMapping("/cattree")
    public List<Kategorie> getCattree() {
        return repository.findAll().stream()
                .flatMap(p -> p.getKategories().stream())
                .distinct()
                .toList();
    }
}
