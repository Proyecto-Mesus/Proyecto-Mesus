package es.cifpcarlos3.api.repositories;

import es.cifpcarlos3.api.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Ejemplos útiles
    boolean existsByNombreUsuario(String nombreUsuario);
    boolean existsByEmail(String email);
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
}

