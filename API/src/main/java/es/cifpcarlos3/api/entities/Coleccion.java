package es.cifpcarlos3.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "colecciones")
public class Coleccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_coleccion")
    private Integer id;

    @Column(nullable = false, length = 250)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_juego", nullable = false)
    private Juego juego;

    @Column(nullable = false)
    private Boolean publica;

    @JsonIgnore
    @OneToMany(mappedBy = "coleccion", cascade = CascadeType.ALL)
    private List<Carta> cartas;
}
