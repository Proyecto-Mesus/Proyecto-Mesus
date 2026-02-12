package es.cifpcarlos3.proyecto_mesus_android.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CartaDto(
    @SerializedName("id_carta")
    val id: Int,
    val nombre: String,
    val set: String,
    @SerializedName("numero_set")
    val numeroSet: String,
    val coleccion: ColeccionDto,
    val imagen: String?
)
