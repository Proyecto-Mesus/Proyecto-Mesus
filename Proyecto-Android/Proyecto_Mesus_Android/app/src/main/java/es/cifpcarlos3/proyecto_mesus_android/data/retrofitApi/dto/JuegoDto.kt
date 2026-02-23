package es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto

import com.google.gson.annotations.SerializedName

data class JuegoDto(
    @SerializedName(value = "id", alternate = ["id_juego"])
    val id: Int,
    val nombre: String
)
