package es.cifpcarlos3.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private int id;

    @Column(nullable = false, length = 250)
    private String nombre;

    @Column(nullable = false, length = 250)
    private String descripcion;

    @com.fasterxml.jackson.annotation.JsonProperty("latitud")
    @Column(name = "ubicacion_latitud")
    private Double latitud;

    @com.fasterxml.jackson.annotation.JsonProperty("longitud")
    @Column(name = "ubicacion_longitud")
    private Double longitud;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @JsonIgnore
    @ManyToMany(
            cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
            }
    )
    @JoinTable(
            name = "usuarios_evento",
            joinColumns = @JoinColumn(name = "id_evento"),
            inverseJoinColumns = @JoinColumn(name = "id_usuario")
    )
    private List<Usuario> usuarios;

    @JsonIgnore
    @OneToOne(mappedBy = "evento", cascade = CascadeType.ALL)
    private ChatEvento chatEvento;

    @ManyToOne
    @JoinColumn(name = "id_creador", nullable = true)
    private Usuario creador;

}
