package es.cifpcarlos3.api.controllers;

import es.cifpcarlos3.api.dto.EventoSinContrasenaDTO;
import es.cifpcarlos3.api.entities.Carta;
import es.cifpcarlos3.api.entities.Evento;
import es.cifpcarlos3.api.entities.Usuario;
import es.cifpcarlos3.api.mapper.EventoSinContrasenaMapper;
import es.cifpcarlos3.api.repositories.EventoRepository;
import es.cifpcarlos3.api.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {
    @Autowired
    EventoRepository eventoRepository;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    EventoSinContrasenaMapper eventoMapper;

    //obtener por id
    @GetMapping("/{id}")
    public ResponseEntity<Evento> findEventoById(@PathVariable int id) {
        return eventoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //obtener todos los eventos
    @GetMapping
    public ResponseEntity<List<EventoSinContrasenaDTO>> findAllEventos() {
        List<EventoSinContrasenaDTO> eventosDTO = eventoRepository.findAll()
                .stream()
                .map(eventoMapper::toDTO)
                .toList();
        return ResponseEntity.ok(eventosDTO);
    }


    //obtener todos los eventos de un usuario
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<EventoSinContrasenaDTO>> findEventosByIdUsuario(@PathVariable int idUsuario) {
        List<EventoSinContrasenaDTO> eventosDTO = eventoRepository.findByCreadorId(idUsuario)
                .stream()
                .map(eventoMapper::toDTO)
                .toList();
        return ResponseEntity.ok(eventosDTO);
    }


    //añadir un evento
    @PostMapping
    public ResponseEntity<Evento> createEvento(@RequestBody Evento evento) {
        return ResponseEntity.status(201).body(eventoRepository.save(evento));
    }

    //añadir un usuario a un evento
    @PostMapping("/{eventoId}/usuario/{usuarioId}")
    public ResponseEntity<?> addUsuario(@PathVariable int eventoId, @PathVariable int usuarioId) {
        Optional<Evento> eventoOpt = eventoRepository.findById(eventoId);
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

        if (eventoOpt.isPresent() && usuarioOpt.isPresent()) {
            Evento evento = eventoOpt.get();
            Usuario usuario = usuarioOpt.get();

            if(evento.getUsuarios() == null) {
                evento.setUsuarios(new ArrayList<>());
            }
            if(usuario.getEventos() == null) {
                usuario.setEventos(new ArrayList<>());
            }

            if(!usuario.getEventos().contains(evento)) {
                usuario.getEventos().add(evento);
            }
            if(!evento.getUsuarios().contains(usuario)) {
                evento.getUsuarios().add(usuario);
            }

            eventoRepository.save(evento);

            return ResponseEntity.ok(evento);

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //actualiza los datos de un evento
    @PutMapping("/{id}")
    public ResponseEntity<Evento> updateEvento(@PathVariable int id, @RequestBody Evento eventoActualizado) {

        return eventoRepository.findById(id)
                .map(evento -> {
                    evento.setNombre(eventoActualizado.getNombre());
                    evento.setDescripcion(eventoActualizado.getDescripcion());
                    evento.setFecha(eventoActualizado.getFecha());
                    evento.setLatitud(eventoActualizado.getLatitud());
                    evento.setLongitud(eventoActualizado.getLongitud());

                    Evento eventoGuardado = eventoRepository.save(evento);
                    return ResponseEntity.ok(eventoGuardado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //borra un evento
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvento(@PathVariable int id) {
        return eventoRepository.findById(id)
                .map(evento -> {

                    if(evento.getUsuarios() != null) {
                        evento.getUsuarios().forEach(usuario -> {
                            usuario.getEventos().remove(evento);
                        });
                    }

                    eventoRepository.delete(evento);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
