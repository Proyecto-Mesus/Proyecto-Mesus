package es.cifpcarlos3.api.controllers;

import es.cifpcarlos3.api.entities.ChatEvento;
import es.cifpcarlos3.api.entities.Evento;
import es.cifpcarlos3.api.entities.Usuario;
import es.cifpcarlos3.api.repositories.ChatEventoRepository;
import es.cifpcarlos3.api.repositories.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/chats_evento")
@RequiredArgsConstructor
public class ChatEventoController {
    @Autowired
    ChatEventoRepository chatEventoRepository;
    @Autowired
    EventoRepository eventoRepository;

    //obtener por id
    @GetMapping("/{id}")
    public ResponseEntity<ChatEvento> findChatById(@PathVariable int id) {
        return chatEventoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //obtiene el chat de un evento
    @GetMapping("/evento/{idEvento}")
    public ResponseEntity<ChatEvento> findChatByEvento(@PathVariable int idEvento) {
        Optional<ChatEvento> chatOpt = chatEventoRepository.findByEventoId(idEvento);
        return chatOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //crea un chat para un evento
    @PostMapping("/evento/{idEvento}")
    public ResponseEntity<ChatEvento> createChatFromEventoId(@PathVariable int idEvento) {
        Optional<Evento> eventoOpt = eventoRepository.findById(idEvento);
        if(eventoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ChatEvento chat = new ChatEvento();
        chat.setEvento(eventoOpt.get());
        return ResponseEntity.status(201).body(chatEventoRepository.save(chat));
    }

    //borra un chat
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChatEvento(@PathVariable Integer id) {
        return chatEventoRepository.findById(id)
                .map(chat -> {
                    chatEventoRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
