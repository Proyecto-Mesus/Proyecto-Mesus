package es.cifpcarlos3.api.repositories;

import es.cifpcarlos3.api.entities.Juego;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JuegoRepository extends JpaRepository<Juego, Integer> {
}
