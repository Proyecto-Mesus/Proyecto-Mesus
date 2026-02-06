package es.cifpcarlos3.api.repositories;

import es.cifpcarlos3.api.entities.Carta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartaRepository extends JpaRepository<Carta, Integer> {
    List<Carta> findByColeccionId(int id);
}
