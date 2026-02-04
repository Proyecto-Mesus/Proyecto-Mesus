package es.cifpcarlos3.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "juegos")
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_juego")
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 250)
    private String nombre;

    @JsonIgnore
    @OneToMany(mappedBy = "juego", cascade = CascadeType.ALL)
    private List<Coleccion> colecciones;
}

