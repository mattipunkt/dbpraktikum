package dev.marisamatti.api.repositories;


import dev.marisamatti.api.models.Produkt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduktRepository extends JpaRepository<Produkt, Integer> {

}
