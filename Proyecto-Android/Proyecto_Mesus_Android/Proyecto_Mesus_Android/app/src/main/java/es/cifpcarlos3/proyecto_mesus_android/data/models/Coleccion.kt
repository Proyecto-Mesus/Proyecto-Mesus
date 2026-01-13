package es.cifpcarlos3.proyecto_mesus_android.data.models

data class Coleccion(
    val idColeccion: Int,
    val nombre: String,
    val idUsuario: Int,
    val idJuego: Int,
    val publica: Int // Use 1 for true, 0 for false
)
