package es.cifpcarlos3.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @NotEmpty(message = "Debe haber un nombre de usuario")
    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 250)
    private String nombreUsuario;

    @NotEmpty(message = "Debe haber una contraseña")
    @Column(nullable = false, length = 250)
    private String password;

    @Email(message = "El email no es válido")
    @Column(unique = true, length = 250)
    private String email;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Coleccion> colecciones;
}
