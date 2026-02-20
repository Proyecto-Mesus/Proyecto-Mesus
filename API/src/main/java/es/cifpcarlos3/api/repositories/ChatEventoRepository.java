package es.cifpcarlos3.api.repositories;

import es.cifpcarlos3.api.entities.ChatEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatEventoRepository extends JpaRepository<ChatEvento, Integer> {
    Optional<ChatEvento> findByEventoId(Integer id);
}
