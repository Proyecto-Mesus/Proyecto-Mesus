package es.cifpcarlos3.proyecto_mesus_android.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UsuarioDto(
    @SerializedName(value = "id_usuario", alternate = ["id"])
    val id: Int,
    @SerializedName(value = "nombre_usuario", alternate = ["nombreUsuario"])
    val nombreUsuario: String
)
