package es.cifpcarlos3.proyecto_mesus_android.data.repository

import es.cifpcarlos3.proyecto_mesus_android.data.models.Carta
import es.cifpcarlos3.proyecto_mesus_android.data.models.Coleccion
import es.cifpcarlos3.proyecto_mesus_android.data.models.Juego
import es.cifpcarlos3.proyecto_mesus_android.data.models.Usuario
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.MesusApi
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto.CartaDto
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto.ColeccionDto
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto.JuegoDto
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto.UsuarioDto
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto.toDomain

class CartaProvider(private val api: MesusApi) {

    suspend fun getCartas(coleccionId: Int): Result<List<Carta>> {
        return try {
            val response = api.getCartasByColeccion(coleccionId)
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createCarta(carta: Carta, coleccion: Coleccion, usuario: Usuario, juego: Juego): Result<Unit> {
         return try {
             val coleccionDto = ColeccionDto(
                id = coleccion.idColeccion,
                nombre = coleccion.nombre,
                 usuario = UsuarioDto(usuario.idUsuario, usuario.nombre),
                 juego = JuegoDto(juego.idJuego, juego.nombre),
                 publica = coleccion.publica == 1
             )

            val dto = CartaDto(
                id = 0,
                nombre = carta.nombre,
                set = carta.set,
                numeroSet = carta.numeroSet,
                coleccion = coleccionDto,
                imagen = carta.imagen
            )
            api.createCarta(dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCarta(id: Int, carta: Carta, coleccion: Coleccion, usuario: Usuario, juego: Juego): Result<Unit> {
        return try {
             val coleccionDto = ColeccionDto(
                id = coleccion.idColeccion,
                nombre = coleccion.nombre,
                 usuario = UsuarioDto(usuario.idUsuario, usuario.nombre),
                 juego = JuegoDto(juego.idJuego, juego.nombre),
                 publica = coleccion.publica == 1
             )

            val dto = CartaDto(
                id = id,
                nombre = carta.nombre,
                set = carta.set,
                numeroSet = carta.numeroSet,
                coleccion = coleccionDto,
                imagen = carta.imagen
            )
            api.updateCarta(id, dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCarta(id: Int): Result<Unit> {
        return try {
            api.deleteCarta(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
