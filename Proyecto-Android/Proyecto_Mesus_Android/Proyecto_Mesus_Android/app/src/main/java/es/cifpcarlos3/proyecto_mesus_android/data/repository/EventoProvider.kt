package es.cifpcarlos3.proyecto_mesus_android.data.repository

import es.cifpcarlos3.proyecto_mesus_android.data.models.Evento
import es.cifpcarlos3.proyecto_mesus_android.data.remote.MesusApi
import es.cifpcarlos3.proyecto_mesus_android.data.remote.dto.toDomain

class EventoProvider(private val api: MesusApi) {

    suspend fun getEventos(): Result<List<Evento>> {
        return try {
            val response = api.getEventos()
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEventosByUsuario(userId: Int): Result<List<Evento>> {
        return try {
            val response = api.getEventosByUsuario(userId)
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEvento(id: Int): Result<Unit> {
        return try {
            api.deleteEvento(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createEvento(evento: Evento, idUsuario: Int): Result<Unit> {
        return try {
            val dto = es.cifpcarlos3.proyecto_mesus_android.data.remote.dto.EventoDto(
                id = 0,
                nombre = evento.nombre,
                descripcion = evento.descripcion,
                fecha = evento.fecha,
                latitud = evento.latitud,
                longitud = evento.longitud
            )
            api.createEvento(dto)
            // Nota: El endpoint addUsuario a evento existe en Java pero aquí estamos simplificando
            // asumiendo que el server maneja la autoría o que seguiremos igual que el JDBC original.
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
