package es.cifpcarlos3.proyecto_mesus_android.data.models

import java.io.Serializable

data class Conversacion(
    val id: Int,
    val otroUsuarioId: Int,
    val otroUsuarioNombre: String,
    val ultimoMensaje: String,
    val timestamp: Long,
    val imagenUsuario: String? = null
) : Serializable
