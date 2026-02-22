package es.cifpcarlos3.proyecto_mesus_android.data.repository

import es.cifpcarlos3.proyecto_mesus_android.data.models.Evento
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.MesusApi
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto.EventoDto
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto.UsuarioDto
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto.toDomain
import java.text.SimpleDateFormat
import java.util.Locale

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

    private fun formatToIso(fecha: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(fecha) ?: java.util.Date()
            val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            fecha
        }
    }

    suspend fun createEvento(evento: Evento, idUsuario: Int): Result<Unit> {
        return try {
            val isoFecha = formatToIso(evento.fecha)
            val dto = EventoDto(
                id = 0,
                nombre = evento.nombre,
                descripcion = evento.descripcion,
                fecha = isoFecha,
                latitud = evento.latitud,
                longitud = evento.longitud,
                creador = UsuarioDto(id = idUsuario, nombreUsuario = "")
            )
            api.createEvento(dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateEvento(id: Int, evento: Evento, idUsuario: Int): Result<Unit> {
        return try {
            val isoFecha = formatToIso(evento.fecha)
            val dto = EventoDto(
                id = id,
                nombre = evento.nombre,
                descripcion = evento.descripcion,
                fecha = isoFecha,
                latitud = evento.latitud,
                longitud = evento.longitud,
                creador = UsuarioDto(id = idUsuario, nombreUsuario = "")
            )
            api.updateEvento(id, dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
