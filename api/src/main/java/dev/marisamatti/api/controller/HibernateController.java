package dev.marisamatti.api.controller;


import dev.marisamatti.api.models.Kategorie;
import dev.marisamatti.api.models.Produkt;
import dev.marisamatti.api.models.ProduktListDto;
import dev.marisamatti.api.models.Verkauf;
import dev.marisamatti.api.repositories.KategorieRepository;
import dev.marisamatti.api.repositories.ProduktRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    @GetMapping("/produkte/top/{number}")
    public List<Produkt> getTopProducts(@PathVariable int number) {
        return produktRepository.findAll().stream()
                .sorted((p1, p2) -> {
                    Double r1 = p1.getRating();
                    Double r2 = p2.getRating();
                    if (r1 == null && r2 == null) return 0;
                    if (r1 == null) return 1;
                    if (r2 == null) return -1;
                    return Double.compare(r2, r1);
                })
                .limit(number)
                .toList();
    }

    @GetMapping("/produkt/{id}/similar-cheaper")
    public List<Produkt> getSimilarCheaperProduct(@PathVariable Long id) {
        Produkt original = produktRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produkt nicht gefunden"));

        // Ermittle den günstigsten Verkaufspreis des Originalprodukts
        Double originalPreis = original.getVerkauefe().stream()
                .map(Verkauf::getPreis)
                .filter(Objects::nonNull)
                .min(Double::compare)
                .orElse(null);

        if (originalPreis == null) return List.of();

        // Finde ähnliche Produkte, die mindestens einen günstigeren Verkauf haben
        return original.getAehnlicheProdukte().stream()
                .filter(p -> p.getVerkauefe().stream()
                        .map(Verkauf::getPreis)
                        .anyMatch(preis -> preis != null && preis < originalPreis))
                .toList();
    }




}
