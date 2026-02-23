package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.models.Conversacion
import es.cifpcarlos3.proyecto_mesus_android.data.models.Mensaje
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.RetrofitInstance
import es.cifpcarlos3.proyecto_mesus_android.data.repository.ChatProvider
import es.cifpcarlos3.proyecto_mesus_android.data.repository.EventoProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ChatUiState {
    object Idle : ChatUiState()
    object Loading : ChatUiState()
    data class SuccessList(val list: List<Conversacion>) : ChatUiState()
    data class SuccessMensajes(val list: List<Mensaje>) : ChatUiState()
    data class SuccessChatOpened(val conversacion: Conversacion) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val api = RetrofitInstance.api
    private val chatProvider = ChatProvider(api)
    private val eventoProvider = EventoProvider(api)
    private val sharedPrefs = application.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val uiState: StateFlow<ChatUiState> = _uiState

    fun getConversaciones() {
        val userId = sharedPrefs.getInt("userId", -1)
        if (userId == -1) {
            _uiState.value = ChatUiState.Error("SESION_EXPIRADA")
            return
        }

        _uiState.value = ChatUiState.Loading
        viewModelScope.launch {
            val eventosResult = eventoProvider.getEventos()
            eventosResult.onSuccess { eventos ->
                val conversaciones = eventos.map { evento ->
                    async {
                        val chat = chatProvider.getChatForEvento(evento.idEvento).getOrNull()
                        if (chat != null) {
                            val mensajesResult = chatProvider.getMensajes(chat.id)
                            val mensajes = mensajesResult.getOrNull() ?: emptyList()
                            
                            if (mensajes.any { it.emisorId == userId }) {
                                chat.copy(
                                    otroUsuarioNombre = evento.nombre,
                                    otroUsuarioId = evento.idEvento,
                                    ultimoMensaje = mensajes.lastOrNull()?.contenido ?: "",
                                    timestamp = mensajes.lastOrNull()?.timestamp ?: 0L
                                )
                            } else null
                        } else null
                    }
                }.awaitAll().filterNotNull()
                
                _uiState.value = ChatUiState.SuccessList(conversaciones)
            }.onFailure {
                _uiState.value = ChatUiState.Error("Error al cargar chats: ${it.message}")
            }
        }
    }

    fun getMensajes(chatId: Int) {
        _uiState.value = ChatUiState.Loading
        viewModelScope.launch {
            val result = chatProvider.getMensajes(chatId)
            result.onSuccess {
                _uiState.value = ChatUiState.SuccessMensajes(it)
            }.onFailure {
                _uiState.value = ChatUiState.Error("Error al cargar mensajes: ${it.message}")
            }
        }
    }

    fun enviarMensaje(chatId: Int, contenido: String) {
        val userName = sharedPrefs.getString("username", "Usuario") ?: "Usuario"
        val userId = sharedPrefs.getInt("userId", -1)
        
        if (userId == -1) return

        viewModelScope.launch {
            val result = chatProvider.enviarMensaje(chatId, contenido, userName, userId)
            result.onSuccess {
                getMensajes(chatId)
            }.onFailure {
                _uiState.value = ChatUiState.Error("Error al enviar: ${it.message}")
            }
        }
    }

    fun abrirChatEvento(idEvento: Int) {
        _uiState.value = ChatUiState.Loading
        viewModelScope.launch {
            chatProvider.getOrCreateChatEvento(idEvento).onSuccess { conversacion ->
                _uiState.value = ChatUiState.SuccessChatOpened(conversacion)
            }.onFailure {
                _uiState.value = ChatUiState.Error("Error al abrir chat: ${it.message}")
            }
        }
    }
}
