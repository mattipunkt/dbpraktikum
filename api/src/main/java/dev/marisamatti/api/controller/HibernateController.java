package dev.marisamatti.api.controller;


import dev.marisamatti.api.models.*;
import dev.marisamatti.api.repositories.VerkaufRepository;
import dev.marisamatti.api.repositories.KategorieRepository;
import dev.marisamatti.api.repositories.ProduktRepository;
import dev.marisamatti.api.repositories.BewertungRepository;
import dev.marisamatti.api.repositories.KundeRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class HibernateController {

    private final ProduktRepository produktRepository;
    private final KategorieRepository kategorieRepository;
    private final VerkaufRepository verkaufRepository;
    private final BewertungRepository bewertungRepository;
    private final KundeRepository kundeRepository;

    public HibernateController(ProduktRepository repository, KategorieRepository kategorieRepository, VerkaufRepository verkaufRepository, BewertungRepository bewertungRepository, KundeRepository kundeRepository) {
        this.produktRepository = repository;
        this.kategorieRepository = kategorieRepository;
        this.verkaufRepository = verkaufRepository;
        this.bewertungRepository = bewertungRepository;
        this.kundeRepository = kundeRepository;
    }

    @GetMapping("/produkte")
    public List<ProduktListDto> getProducts() {
        return produktRepository.findAll().stream()
                .map(p -> {
                    ProduktListDto dto = new ProduktListDto();
                    dto.setId(p.getId());
                    dto.setTitel(p.getTitel());
                    // Setze den Produkttyp (CD, DVD, Buch)
                    if (p instanceof Cd) {
                        dto.setTyp("CD");
                    } else if (p instanceof Dvd) {
                        dto.setTyp("DVD");
                    } else if (p instanceof Buch) {
                        dto.setTyp("BUCH");
                    } else {
                        dto.setTyp("PRODUKT");
                    }
                    return dto;
                })
                .toList();
    }

    @GetMapping("/produkt/{id}")
    public Produkt getProductById(@PathVariable Integer id) {
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
    public List<Produkt> getSimilarCheaperProduct(@PathVariable Integer id) {
        // Referenzprodukt laden
        Produkt referenz = produktRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produkt nicht gefunden"));

        // Minimalpreis des Referenzprodukts über Verkäufe ermitteln
        Double refMin = verkaufRepository.findMinPreisByProduktId(referenz.getId());
        if (refMin == null) {
            // Wenn es keinen Preis gibt, gibt es keine billigeren ähnlichen Produkte
            return List.of();
        }

        // Ähnliche Produkte einsammeln (IDs)
        List<Integer> similarIds = referenz.getAehnlicheProdukte().stream()
                .map(Produkt::getId)
                .toList();
        if (similarIds.isEmpty()) {
            return List.of();
        }

        // Minimalpreise für ähnliche Produkte abfragen
        var rows = verkaufRepository.findMinPreisByProduktIds(similarIds);
        // Map: produktId -> minPreis
        java.util.Map<Integer, Double> minPriceByProduct = new java.util.HashMap<>();
        for (Object[] row : rows) {
            Integer pid = (Integer) row[0];
            Double min = (Double) row[1];
            if (min != null) minPriceByProduct.put(pid, min);
        }

        // Filtern: günstiger als Referenz
        return referenz.getAehnlicheProdukte().stream()
                .filter(p -> {
                    Double min = minPriceByProduct.get(p.getId());
                    return min != null && min < refMin;
                })
                // Optional sort by price difference ascending
                .sorted((p1, p2) -> {
                    Double m1 = minPriceByProduct.get(p1.getId());
                    Double m2 = minPriceByProduct.get(p2.getId());
                    return Double.compare(m1, m2);
                })
                .toList();
    }

    @PostMapping("/produkt/{id}/reviews/add")
    public Bewertung addReviews(@PathVariable Integer id, @RequestBody BewertungDto dto) {
        // Load product
        Produkt produkt = produktRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produkt nicht gefunden"));

        if (dto == null) {
            throw new RuntimeException("BewertungDto darf nicht null sein");
        }
        if (dto.getMail() == null || dto.getMail().isBlank()) {
            throw new RuntimeException("mail ist erforderlich");
        }

        // Find or create customer by username (use mail as username)
        Kunde kunde = kundeRepository.findByUsername(dto.getMail())
                .orElseGet(() -> {
                    Kunde k = new Kunde();
                    k.setUsername(dto.getMail());
                    k.setVorname(dto.getName());
                    k.setGast(true);
                    return kundeRepository.save(k);
                });

        // Build Bewertung from DTO
        Bewertung review = new Bewertung();
        review.setProdukt(produkt);
        review.setKunde(kunde);
        review.setSterne(dto.getRating());
        review.setRezension(dto.getRatingText());
        // Optional: store the name as summary if provided
        review.setZusammenfassung(dto.getName());
        review.setDatum(LocalDate.now());

        // Prepare composite key
        BewertungId bid = new BewertungId();
        bid.setProduktId(produkt.getId());
        bid.setKundeId(kunde.getId());
        review.setId(bid);

        // Persist review
        return bewertungRepository.save(review);
    }

    @GetMapping("/trolls/{rating}")
    public List<Kunde> getTrolls(@PathVariable Double rating) {
        if (rating == null) {
            throw new RuntimeException("rating ist erforderlich");
        }
        return kundeRepository.findCustomersWithAvgRatingBelow(rating);
    }

    @GetMapping("/produkt/{id}/offers")
    public List<Verkauf> getOffers(@PathVariable Integer id) {
        return verkaufRepository.getOffers(id);
    }
}
