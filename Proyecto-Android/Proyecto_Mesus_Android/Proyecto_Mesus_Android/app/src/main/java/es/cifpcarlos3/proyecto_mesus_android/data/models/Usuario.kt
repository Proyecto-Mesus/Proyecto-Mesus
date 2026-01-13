package es.cifpcarlos3.proyecto_mesus_android.data.models

data class Usuario(
    val idUsuario: Int,
    val nombre: String,
    val contrasena: String? = null // Optional, we don't need it for search display
)
