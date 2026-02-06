package es.cifpcarlos3.api.repositories;

import es.cifpcarlos3.api.entities.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColeccionRepository extends JpaRepository<Coleccion, Integer> {
    List<Coleccion> findByUsuarioId(int id);
    List<Coleccion> findByUsuarioIdAndPublicaTrue(int id);
}

