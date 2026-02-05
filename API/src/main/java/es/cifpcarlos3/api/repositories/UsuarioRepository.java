package es.cifpcarlos3.api.repositories;

import es.cifpcarlos3.api.dto.UsuarioSinContrasenaDTO;
import es.cifpcarlos3.api.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Ejemplos útiles
    boolean existsByNombreUsuario(String nombreUsuario);
    boolean existsByEmail(String email);
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    Optional<Usuario> findByEmail(String email);

    @Query("SELECT new es.cifpcarlos3.api.dto.UsuarioSinContrasenaDTO(u.id, u.nombreUsuario) FROM Usuario u WHERE u.nombreUsuario LIKE %:nombreUsuario%")
    List<UsuarioSinContrasenaDTO> obtenerUsuariosSinContrasenaPorNombreUsuario(String nombreUsuario);
}

