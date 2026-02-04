package es.cifpcarlos3.proyecto_mesus_android.data.models

import java.io.Serializable

data class MercadoItem(
    val idItem: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val latitud: Double,
    val longitud: Double,
    val idUsuario: Int,
    val idCarta: Int? = null,
    val nombreCarta: String? = null
) : Serializable
