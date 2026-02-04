package es.cifpcarlos3.api.controllers;

import es.cifpcarlos3.api.entities.Usuario;
import es.cifpcarlos3.api.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    UsuarioRepository usuarioRepository;

    //obtener un usuario por su nombre
    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<Usuario> findUsuarioByNombre(@PathVariable String nombre) {
        return usuarioRepository.findByNombreUsuario(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //añadir un usuario
    @PostMapping
    public ResponseEntity<Usuario> createEmpleado(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.status(301).body(usuarioRepository.save(usuario));
    }

    //verifica el login
    @GetMapping("/login/usuario/{login}/password/{password}")
    public ResponseEntity<Usuario> login(@PathVariable String login, @PathVariable String password) {
        //busco usuario por nombre
        Optional<Usuario> usuario = usuarioRepository.findByNombreUsuario(login);

        //si no está ese nombre, busco por email
        if (usuario.isEmpty()) {
            usuario = usuarioRepository.findByEmail(login);
        }

        //si no existe doy error
        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuarioExistente = usuario.get();

        //verifico la contraseña
        if (!usuarioExistente.getPassword().equals(password)) {
            return ResponseEntity.status(401).build();
        }

        //si el login es correcto, envío el usuario
        return ResponseEntity.ok(usuarioExistente);
    }

}

