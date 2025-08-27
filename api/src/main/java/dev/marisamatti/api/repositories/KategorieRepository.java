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

}
