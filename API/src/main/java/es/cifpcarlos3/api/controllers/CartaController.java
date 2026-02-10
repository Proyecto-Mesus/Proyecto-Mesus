package es.cifpcarlos3.api.controllers;

import es.cifpcarlos3.api.entities.Carta;
import es.cifpcarlos3.api.entities.Coleccion;
import es.cifpcarlos3.api.repositories.CartaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cartas")
public class CartaController {
    @Autowired
    CartaRepository cartaRepository;

    //obtener cartas de una colección
    @GetMapping("/coleccion/{idColeccion}")
    public ResponseEntity<List<Carta>> findCartasByColeccionId(@PathVariable int idColeccion) {
        List<Carta> cartas = cartaRepository.findByColeccionId(idColeccion);
        return ResponseEntity.ok(cartas);
    }

    //añadir una colección
    @PostMapping
    public ResponseEntity<Carta> createCarta(@RequestBody Carta carta) {
        return ResponseEntity.status(301).body(cartaRepository.save(carta));
    }

    //borrar una carta
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCarta(@PathVariable int id) {
        return cartaRepository.findById(id)
                .map(carta -> {
                    cartaRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //actualizar todos los datos de una carta
    @PutMapping("/{id}")
    public ResponseEntity<Carta> updateCarta(@PathVariable int id, @RequestBody Carta cartaActualizada) {

        return cartaRepository.findById(id)
                .map(carta -> {
                    carta.setNombre(cartaActualizada.getNombre());
                    carta.setSet(cartaActualizada.getSet());
                    carta.setNumeroSet(cartaActualizada.getNumeroSet());
                    carta.setColeccion(cartaActualizada.getColeccion());
                    carta.setImagen(cartaActualizada.getImagen());

                    Carta cartaGuardada = cartaRepository.save(carta);
                    return ResponseEntity.ok(cartaGuardada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
