package es.cifpcarlos3.api.controllers;

import es.cifpcarlos3.api.dto.UsuarioSinContrasenaDTO;
import es.cifpcarlos3.api.entities.Usuario;
import es.cifpcarlos3.api.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    UsuarioRepository usuarioRepository;

    //obtener por id
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> findUsuarioById(@PathVariable int id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //obtener un usuario por su nombre
    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<List<UsuarioSinContrasenaDTO>> findUsuariosByNombre(@PathVariable String nombre) {
        List<UsuarioSinContrasenaDTO> usuarios = usuarioRepository.obtenerUsuariosSinContrasenaPorNombreUsuario(nombre);
        return ResponseEntity.ok(usuarios);
    }

    //añadir un usuario
    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.status(201).body(usuarioRepository.save(usuario));
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

    // actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@Valid @RequestBody Usuario usuarioNuevo, @PathVariable int id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setNombreUsuario(usuarioNuevo.getNombreUsuario());
                    usuario.setPassword(usuarioNuevo.getPassword());
                    usuario.setEmail(usuarioNuevo.getEmail());
                    return ResponseEntity.ok(usuarioRepository.save(usuario));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}

