package es.cifpcarlos3.api.repositories;

import es.cifpcarlos3.api.entities.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    List<Evento> findEventosByUsuarios_Id(Integer idUsuario);
    List<Evento> findByCreadorId(Integer idCreador);
}
