package es.cifpcarlos3.api.repositories;

import es.cifpcarlos3.api.entities.Carta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaRepository extends JpaRepository<Carta, Integer> {
}
