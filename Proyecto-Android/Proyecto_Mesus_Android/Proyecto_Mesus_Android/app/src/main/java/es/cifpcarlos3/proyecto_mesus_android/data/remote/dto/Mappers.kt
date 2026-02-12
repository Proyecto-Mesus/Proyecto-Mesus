package es.cifpcarlos3.proyecto_mesus_android.data.remote.dto

import es.cifpcarlos3.proyecto_mesus_android.data.models.Carta
import es.cifpcarlos3.proyecto_mesus_android.data.models.Coleccion
import es.cifpcarlos3.proyecto_mesus_android.data.models.Juego
import es.cifpcarlos3.proyecto_mesus_android.data.models.Usuario

fun UsuarioDto.toDomain(): Usuario {
    return Usuario(
        idUsuario = id,
        nombre = nombreUsuario
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
