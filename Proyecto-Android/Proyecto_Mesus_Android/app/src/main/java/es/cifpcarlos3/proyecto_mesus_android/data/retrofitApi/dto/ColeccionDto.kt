package es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto

import com.google.gson.annotations.SerializedName

data class ColeccionDto(
    @SerializedName(value = "id", alternate = ["id_coleccion"])
    val id: Int,
    val nombre: String,
    val usuario: UsuarioDto,
    val juego: JuegoDto,
    val publica: Boolean
)
