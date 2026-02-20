package es.cifpcarlos3.proyecto_mesus_android.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EventoDto(
    @SerializedName("id_evento")
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val fecha: String,
    @SerializedName("ubicacion_latitud")
    val latitud: Double,
    @SerializedName("ubicacion_longitud")
    val longitud: Double
)
