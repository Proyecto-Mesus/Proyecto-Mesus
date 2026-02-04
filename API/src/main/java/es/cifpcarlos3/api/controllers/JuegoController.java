package es.cifpcarlos3.api.controllers;

import es.cifpcarlos3.api.entities.Juego;
import es.cifpcarlos3.api.repositories.JuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/juegos")
public class JuegoController {
    @Autowired
    JuegoRepository juegoRepository;

    //devuelve todos los juegos
    @GetMapping
    public ResponseEntity<List<Juego>> findAllJuegos() {
        return ResponseEntity.ok(juegoRepository.findAll());
    }
}
