package es.cifpcarlos3.proyecto_mesus_android.data.models

import java.io.Serializable

data class Evento(
    val idEvento: Int,
    val nombre: String,
    val descripcion: String,
    val fecha: String,
    val latitud: Double,
    val longitud: Double,
    val idCreador: Int? = null
) : Serializable
