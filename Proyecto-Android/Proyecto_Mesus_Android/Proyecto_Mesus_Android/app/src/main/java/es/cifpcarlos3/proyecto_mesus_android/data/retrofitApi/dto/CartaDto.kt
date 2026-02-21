package es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto

import com.google.gson.annotations.SerializedName

data class CartaDto(
    @SerializedName(value = "id", alternate = ["id_carta"])
    val id: Int,
    val nombre: String,
    val set: String,
    @SerializedName(value = "numeroSet", alternate = ["numero_set"])
    val numeroSet: String,
    val coleccion: ColeccionDto,
    val imagen: String?
)
