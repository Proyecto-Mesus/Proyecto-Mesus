package es.cifpcarlos3.api.repositories;

import es.cifpcarlos3.api.entities.MensajeChatEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeChatEventoRepository extends JpaRepository<MensajeChatEvento, Integer> {
    Page<MensajeChatEvento> findByChatEventoIdOrderByFechaEnvioAsc(int idChatEvento, Pageable pageable);
}
