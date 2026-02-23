package es.cifpcarlos3.proyecto_mesus_android.data.repository

import es.cifpcarlos3.proyecto_mesus_android.data.models.Juego
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.MesusApi
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto.toDomain

class JuegoProvider(private val api: MesusApi) {

    suspend fun getJuegos(): Result<List<Juego>> {
        return try {
            val response = api.getJuegos()
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
