package es.cifpcarlos3.api.repositories;

import es.cifpcarlos3.api.entities.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    List<Evento> findEventosByUsuarios_Id(Integer idUsuario);
    @Query("SELECT e FROM Evento e WHERE :idUsuario NOT IN (SELECT u.id FROM e.usuarios u)")
    List<Evento> findEventosDondeUsuarioNoParticipa(Integer idUsuario);

    List<Evento> findByCreadorId(Integer idCreador);
}
