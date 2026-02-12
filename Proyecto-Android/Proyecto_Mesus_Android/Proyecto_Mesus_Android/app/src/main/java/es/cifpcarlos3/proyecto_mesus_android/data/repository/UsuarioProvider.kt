package es.cifpcarlos3.proyecto_mesus_android.data.repository

import es.cifpcarlos3.proyecto_mesus_android.data.models.Usuario
import es.cifpcarlos3.proyecto_mesus_android.data.remote.MesusApi
import es.cifpcarlos3.proyecto_mesus_android.data.remote.dto.toDomain

class UsuarioProvider(private val api: MesusApi) {

    suspend fun login(login: String, pass: String): Result<Usuario> {
        return try {
            val response = api.login(login, pass)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchUsuarios(query: String): Result<List<Usuario>> {
        return try {
            val response = api.searchUsuarios(query)
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
