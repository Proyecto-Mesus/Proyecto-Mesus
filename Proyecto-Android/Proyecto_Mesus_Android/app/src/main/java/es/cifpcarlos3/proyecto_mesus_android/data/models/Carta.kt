package es.cifpcarlos3.proyecto_mesus_android.data.models

import java.io.Serializable

data class Carta(
    val idCarta: Int,
    val nombre: String,
    val set: String,
    val numeroSet: String,
    val idColeccion: Int,
    val imagen: String? = null
) : Serializable
