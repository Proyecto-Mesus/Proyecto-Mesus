package es.cifpcarlos3.api.controllers;

import es.cifpcarlos3.api.entities.ChatEvento;
import es.cifpcarlos3.api.entities.MensajeChatEvento;
import es.cifpcarlos3.api.repositories.ChatEventoRepository;
import es.cifpcarlos3.api.repositories.MensajeChatEventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mensajes_chats_evento")
@RequiredArgsConstructor
public class MensajeChatEventoController {
    @Autowired
    MensajeChatEventoRepository mensajeChatEventoRepository;
    @Autowired
    ChatEventoRepository chatEventoRepository;

    //obtiene todos los mensajes de un chat
    @GetMapping("/chat/{idChat}")
    public ResponseEntity<Page<MensajeChatEvento>> findAllMensajesByChat(@PageableDefault(page = 0,
            size = 20,
            sort = "fechaEnvio",
            direction = Sort.Direction.DESC) Pageable pageable, @PathVariable int idChat) {
        Page<MensajeChatEvento> mensajes = mensajeChatEventoRepository.findByChatEventoIdOrderByFechaEnvioAsc(idChat, pageable);
        return ResponseEntity.ok(mensajes);
    }

    //enviar mensaje a un chat
    @PostMapping("/chat/{idChat}")
    public ResponseEntity<MensajeChatEvento> createMensaje(@PathVariable Integer idChat, @RequestBody MensajeChatEvento mensaje) {
        Optional<ChatEvento> chatOpt = chatEventoRepository.findById(idChat);
        if (chatOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        mensaje.setChatEvento(chatOpt.get());
        mensaje.setFechaEnvio(LocalDateTime.now());

        return ResponseEntity.status(201).body(mensajeChatEventoRepository.save(mensaje));
    }

    //borrar un mensaje
    @DeleteMapping("/{idMensaje}")
    public ResponseEntity<?> deleteMensaje(@PathVariable int idMensaje) {
        Optional<MensajeChatEvento> mensajeOpt = mensajeChatEventoRepository.findById(idMensaje);
        if(mensajeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        mensajeChatEventoRepository.deleteById(idMensaje);
        return ResponseEntity.noContent().build();
    }

}
