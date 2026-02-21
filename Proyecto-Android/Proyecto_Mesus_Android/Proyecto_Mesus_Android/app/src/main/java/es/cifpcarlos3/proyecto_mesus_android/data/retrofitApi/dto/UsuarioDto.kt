package es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto

import com.google.gson.annotations.SerializedName

data class UsuarioDto(
    @SerializedName(value = "id", alternate = ["id_usuario"])
    val id: Int,
    @SerializedName(value = "nombreUsuario", alternate = ["nombre_usuario"])
    val nombreUsuario: String
)
