package es.cifpcarlos3.proyecto_mesus_android.data.repository

import es.cifpcarlos3.proyecto_mesus_android.data.models.Coleccion
import es.cifpcarlos3.proyecto_mesus_android.data.models.Juego
import es.cifpcarlos3.proyecto_mesus_android.data.models.Usuario
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.MesusApi
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto.ColeccionDto
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto.JuegoDto
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto.UsuarioDto
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto.toDomain

class ColeccionProvider(private val api: MesusApi) {

    suspend fun getColecciones(userId: Int): Result<List<Coleccion>> {
        return try {
            val response = api.getColeccionesByUsuario(userId)
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getColeccionesPublicas(userId: Int): Result<List<Coleccion>> {
        return try {
            val response = api.getColeccionesPublicasByUsuario(userId)
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createColeccion(coleccion: Coleccion, usuario: Usuario, juego: Juego): Result<Unit> {
        return try {
            val dto = ColeccionDto(
                id = 0,
                nombre = coleccion.nombre,
                usuario = UsuarioDto(usuario.idUsuario, usuario.nombre),
                juego = JuegoDto(juego.idJuego, juego.nombre),
                publica = coleccion.publica == 1
            )
            api.createColeccion(dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateColeccion(id: Int, coleccion: Coleccion, usuario: Usuario, juego: Juego): Result<Unit> {
        return try {
            val dto = ColeccionDto(
                id = id,
                nombre = coleccion.nombre,
                usuario = UsuarioDto(usuario.idUsuario, usuario.nombre),
                juego = JuegoDto(juego.idJuego, juego.nombre),
                publica = coleccion.publica == 1
            )
            api.updateColeccion(id, dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteColeccion(id: Int): Result<Unit> {
        return try {
            api.deleteColeccion(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
