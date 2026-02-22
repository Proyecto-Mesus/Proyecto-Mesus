package es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto

import com.google.gson.annotations.SerializedName

data class EventoDto(
    @SerializedName(value = "id", alternate = ["id_evento"])
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val fecha: String,
    @SerializedName(value = "latitud", alternate = ["ubicacion_latitud"])
    val latitud: Double,
    @SerializedName(value = "longitud", alternate = ["ubicacion_longitud"])
    val longitud: Double,
    val creador: UsuarioDto? = null
)
