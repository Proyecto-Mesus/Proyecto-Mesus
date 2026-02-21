package es.cifpcarlos3.proyecto_mesus_android.data.models

import java.io.Serializable

data class Mensaje(
    val id: Int,
    val emisorId: Int,
    val emisorNombre: String,
    val contenido: String,
    val timestamp: Long,
    val chatId: Int
) : Serializable
