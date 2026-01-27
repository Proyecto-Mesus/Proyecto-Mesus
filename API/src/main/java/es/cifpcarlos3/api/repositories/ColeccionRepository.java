package es.cifpcarlos3.api.repositories;

import es.cifpcarlos3.api.entities.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColeccionRepository extends JpaRepository<Coleccion, Integer> {
}

