package es.cifpcarlos3.api.controllers;

import es.cifpcarlos3.api.dto.ColeccionSinContrasenaDTO;
import es.cifpcarlos3.api.dto.UsuarioSinContrasenaDTO;
import es.cifpcarlos3.api.entities.Coleccion;
import es.cifpcarlos3.api.entities.Usuario;
import es.cifpcarlos3.api.repositories.CartaRepository;
import es.cifpcarlos3.api.repositories.ColeccionRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colecciones")
public class ColeccionController {
    @Autowired
    ColeccionRepository coleccionRepository;

    //obtener colecciones por id de usuario
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Coleccion>> findColeccionesByUsuarioId(@PathVariable int idUsuario) {
        List<Coleccion> colecciones = coleccionRepository.findByUsuarioId(idUsuario);
        return ResponseEntity.ok(colecciones);
    }

    //obtener colecciones por id de usuario, solo las públicas
    @GetMapping("/publicas/usuario/{idUsuario}")
    public ResponseEntity<?> findColeccionesPublicasByUsuarioId(@PathVariable int idUsuario) {
        List<Coleccion> colecciones = coleccionRepository.findByUsuarioIdAndPublicaTrue(idUsuario);

        List<ColeccionSinContrasenaDTO> coleccionesDTO = colecciones.stream()
                .map(c -> new ColeccionSinContrasenaDTO(
                        c.getId(),
                        c.getNombre(),
                        new UsuarioSinContrasenaDTO(
                                c.getUsuario().getId(),
                                c.getUsuario().getNombreUsuario()
                        ),
                        c.getJuego(),
                        c.getPublica(),
                        c.getCartas()
                ))
                .toList();

        return ResponseEntity.ok(coleccionesDTO);
    }


    //añadir una colección
    @PostMapping
    public ResponseEntity<Coleccion> createColeccion(@RequestBody Coleccion coleccion) {
        return ResponseEntity.status(201).body(coleccionRepository.save(coleccion));
    }

    //borrar una colección
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteColeccion(@PathVariable int id) {
        return coleccionRepository.findById(id)
                .map(coleccion -> {
                    coleccionRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //actualizar todos los datos de una colección
    @PutMapping("/{id}")
    public ResponseEntity<Coleccion> updateColeccion(@PathVariable int id, @RequestBody Coleccion coleccionActualizada) {

        return coleccionRepository.findById(id)
                .map(coleccion -> {
                    coleccion.setNombre(coleccionActualizada.getNombre());
                    coleccion.setUsuario(coleccionActualizada.getUsuario());
                    coleccion.setJuego(coleccionActualizada.getJuego());
                    coleccion.setPublica(coleccionActualizada.getPublica());

                    Coleccion coleccionGuardada = coleccionRepository.save(coleccion);
                    return ResponseEntity.ok(coleccionGuardada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
