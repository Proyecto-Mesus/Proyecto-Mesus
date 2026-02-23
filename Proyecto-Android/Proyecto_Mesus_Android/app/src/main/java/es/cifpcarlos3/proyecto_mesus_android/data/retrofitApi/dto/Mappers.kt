package es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto

import es.cifpcarlos3.proyecto_mesus_android.data.models.Carta
import es.cifpcarlos3.proyecto_mesus_android.data.models.Coleccion
import es.cifpcarlos3.proyecto_mesus_android.data.models.Conversacion
import es.cifpcarlos3.proyecto_mesus_android.data.models.Juego
import es.cifpcarlos3.proyecto_mesus_android.data.models.Mensaje
import es.cifpcarlos3.proyecto_mesus_android.data.models.Usuario

fun UsuarioDto.toDomain(): Usuario {
    return Usuario(
        idUsuario = id,
        nombre = nombreUsuario
    )
}

fun EventoDto.toDomain(): es.cifpcarlos3.proyecto_mesus_android.data.models.Evento {
    return es.cifpcarlos3.proyecto_mesus_android.data.models.Evento(
        idEvento = id,
        nombre = nombre,
        descripcion = descripcion,
        fecha = fecha,
        latitud = latitud,
        longitud = longitud,
        idCreador = creador?.id
    )
}

fun JuegoDto.toDomain(): Juego {
    return Juego(
        idJuego = id,
        nombre = nombre
    )
}

fun ColeccionDto.toDomain(): Coleccion {
    return Coleccion(
        idColeccion = id,
        nombre = nombre,
        idUsuario = usuario.id,
        idJuego = juego.id,
        publica = if (publica) 1 else 0
    )
}

fun CartaDto.toDomain(): Carta {
    return Carta(
        idCarta = id,
        nombre = nombre,
        set = set,
        numeroSet = numeroSet,
        idColeccion = coleccion.id,
        imagen = imagen
    )
}

fun ChatEventoDto.toDomain(): Conversacion {
    return Conversacion(
        id = id,
        otroUsuarioId = evento.id, // Usamos el ID del evento como referencia
        otroUsuarioNombre = evento.nombre,
        ultimoMensaje = "", // Se llenará dinámicamente
        timestamp = System.currentTimeMillis()
    )
}

fun MensajeChatEventoDto.toDomain(): Mensaje {
    // Formato de fecha de Spring: 2026-02-20T14:35:01
    val millis = try {
        val format = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
        format.parse(fechaEnvio)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }

    return Mensaje(
        id = id,
        emisorId = idUsuario,
        emisorNombre = nombreRemitente,
        contenido = contenido,
        timestamp = millis,
        chatId = chatEvento?.id ?: 0
    )
}
