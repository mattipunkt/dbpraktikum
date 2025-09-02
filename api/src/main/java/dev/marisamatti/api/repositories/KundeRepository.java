package dev.marisamatti.api.repositories;

import dev.marisamatti.api.models.Kunde;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KundeRepository extends JpaRepository<Kunde, Integer> {
    Optional<Kunde> findByUsername(String username);

    @Query("select b.kunde from Bewertung b where b.sterne is not null group by b.kunde having avg(b.sterne) < :rating")
    List<Kunde> findCustomersWithAvgRatingBelow(@Param("rating") Double rating);
}
