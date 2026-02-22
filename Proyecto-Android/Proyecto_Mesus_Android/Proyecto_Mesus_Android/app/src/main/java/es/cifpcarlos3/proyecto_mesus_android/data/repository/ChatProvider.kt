package es.cifpcarlos3.proyecto_mesus_android.data.repository

import es.cifpcarlos3.proyecto_mesus_android.data.models.Conversacion
import es.cifpcarlos3.proyecto_mesus_android.data.models.Mensaje
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.MesusApi
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto.MensajeChatEventoDto
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.dto.toDomain

class ChatProvider(private val api: MesusApi) {

    suspend fun getChatForEvento(eventoId: Int): Result<Conversacion> {
        return try {
            val response = api.getChatByEvento(eventoId)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createChatForEvento(eventoId: Int): Result<Conversacion> {
        return try {
            val response = api.createChatForEvento(eventoId)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMensajes(chatId: Int): Result<List<Mensaje>> {
        return try {
            val response = api.getMensajesByChat(chatId)
            Result.success(response.content.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun enviarMensaje(chatId: Int, contenido: String, nombreUsuario: String, idUsuario: Int): Result<Mensaje> {
        return try {
            val dto = MensajeChatEventoDto(
                id = 0,
                contenido = contenido,
                fechaEnvio = "",
                nombreRemitente = nombreUsuario,
                idUsuario = idUsuario
            )
            val response = api.sendMensaje(chatId, dto)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrCreateChatEvento(eventoId: Int): Result<Conversacion> {
        val existing = getChatForEvento(eventoId)
        if (existing.isSuccess) return existing
        
        return createChatForEvento(eventoId)
    }
}
