package dev.marisamatti.api.repositories;

import dev.marisamatti.api.models.Bewertung;
import dev.marisamatti.api.models.BewertungId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BewertungRepository extends JpaRepository<Bewertung, BewertungId> {
}
