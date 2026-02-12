package es.cifpcarlos3.proyecto_mesus_android.data.remote.dto

import com.google.gson.annotations.SerializedName

data class JuegoDto(
    @SerializedName("id_juego")
    val id: Int,
    val nombre: String
)
