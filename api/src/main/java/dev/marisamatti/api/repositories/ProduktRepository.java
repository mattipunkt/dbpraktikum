package dev.marisamatti.api.repositories;


import dev.marisamatti.api.models.Produkt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduktRepository extends JpaRepository<Produkt, Long> {

    // This interface will automatically inherit methods for CRUD operations
    // from JpaRepository, such as save(), findById(), findAll(), deleteById(), etc.
    // You can also define custom query methods here if needed.

}
