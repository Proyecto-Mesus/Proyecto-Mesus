package es.cifpcarlos3.api.dto;

import es.cifpcarlos3.api.entities.Carta;
import es.cifpcarlos3.api.entities.Juego;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColeccionSinContrasenaDTO {
    private int id;
    private String nombre;
    private UsuarioSinContrasenaDTO usuario;
    private Juego juego;
    private Boolean publica;
    private List<Carta> cartas;
}

