package es.cifpcarlos3.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoSinContrasenaDTO {

    private int id;
    private String nombre;
    private String descripcion;
    private Double latitud;
    private Double longitud;
    private LocalDateTime fecha;

    private UsuarioSinContrasenaDTO creador;
}
