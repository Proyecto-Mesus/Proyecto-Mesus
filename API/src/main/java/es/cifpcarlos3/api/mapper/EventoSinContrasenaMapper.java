package es.cifpcarlos3.api.mapper;

import es.cifpcarlos3.api.dto.EventoSinContrasenaDTO;
import es.cifpcarlos3.api.dto.UsuarioSinContrasenaDTO;
import es.cifpcarlos3.api.entities.Evento;
import org.springframework.stereotype.Component;

@Component
public class EventoSinContrasenaMapper {

    public EventoSinContrasenaDTO toDTO(Evento evento) {
        if (evento == null) {
            return null;
        }

        UsuarioSinContrasenaDTO creadorDTO = null;

        if (evento.getCreador() != null) {
            creadorDTO = new UsuarioSinContrasenaDTO(
                    evento.getCreador().getId(),
                    evento.getCreador().getNombreUsuario()
            );
        }

        return new EventoSinContrasenaDTO(
                evento.getId(),
                evento.getNombre(),
                evento.getDescripcion(),
                evento.getLatitud(),
                evento.getLongitud(),
                evento.getFecha(),
                creadorDTO
        );
    }
}
