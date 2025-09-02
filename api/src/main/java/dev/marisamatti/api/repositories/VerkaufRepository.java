package dev.marisamatti.api.repositories;

import dev.marisamatti.api.models.Verkauf;
import dev.marisamatti.api.models.VerkaufId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface VerkaufRepository extends JpaRepository<Verkauf, VerkaufId> {

    @Query("select min(v.preis) from Verkauf v where v.produkt.id = :produktId")
    Double findMinPreisByProduktId(@Param("produktId") Integer produktId);

    @Query("select v.produkt.id, min(v.preis) from Verkauf v where v.produkt.id in :produktIds group by v.produkt.id")
    List<Object[]> findMinPreisByProduktIds(@Param("produktIds") Collection<Integer> produktIds);

    @Query("SELECT v FROM Produkt p, Verkauf v WHERE p.id = v.produkt.id AND p.id = :produktId")
    List<Verkauf> getOffers(@Param("produktId") Integer produktId);
}
