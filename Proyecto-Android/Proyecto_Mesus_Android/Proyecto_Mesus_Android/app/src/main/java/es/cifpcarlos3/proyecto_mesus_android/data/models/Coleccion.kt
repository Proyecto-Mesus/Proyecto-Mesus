package es.cifpcarlos3.proyecto_mesus_android.data.models

import java.io.Serializable

data class Coleccion(
    val idColeccion: Int,
    val nombre: String,
    val idUsuario: Int,
    val idJuego: Int,
    val publica: Int
) : Serializable
