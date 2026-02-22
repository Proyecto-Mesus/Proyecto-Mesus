package es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto

import com.google.gson.annotations.SerializedName

data class ChatEventoDto(
    @SerializedName(value = "id", alternate = ["id_chat"])
    val id: Int,
    val evento: EventoDto
)
