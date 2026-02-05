package es.cifpcarlos3.api.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cartas")
public class Carta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carta")
    private int id;

    @Column(nullable = false, length = 250)
    private String nombre;

    @Column(name = "set", nullable = false, length = 250)
    private String set;

    @Column(name = "numero_set", nullable = false, length = 250)
    private String numeroSet;

    @ManyToOne
    @JoinColumn(name = "id_coleccion", nullable = false)
    private Coleccion coleccion;

    @Column(length = 250)
    private String imagen;
}
