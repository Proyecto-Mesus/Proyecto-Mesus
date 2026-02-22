package es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto

import com.google.gson.annotations.SerializedName

data class MensajeChatEventoDto(
    @SerializedName(value = "id", alternate = ["id_mensaje"])
    val id: Int,
    val contenido: String,
    @SerializedName("fechaEnvio")
    val fechaEnvio: String,
    val chatEvento: ChatEventoDto? = null,
    val nombreRemitente: String,
    val idUsuario: Int
)
