package dev.marisamatti.api.repositories;

import dev.marisamatti.api.models.Kategorie;
import dev.marisamatti.api.models.Produkt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface KategorieRepository extends JpaRepository<Kategorie, Long> {
    @Query("SELECT k FROM Kategorie k WHERE k.oberkategorie IS NULL")
    Set<Kategorie> getTopLevelCategories();

    default List<Produkt> getProductsByCategoryPath(String catpath) {
        String[] pathParts = catpath.split("/");
        Kategorie current = null;
        List<Kategorie> candidates = getTopLevelCategories().stream().toList();

        for (String part : pathParts) {
            current = candidates.stream()
                    .filter(k -> k.getName().equals(part))
                    .findFirst()
                    .orElse(null);
            if (current == null) return List.of();
            candidates = current.getKategorien().stream().toList();
        }
        return current != null ? current.getProdukts().stream().toList() : List.of();
    }

}
