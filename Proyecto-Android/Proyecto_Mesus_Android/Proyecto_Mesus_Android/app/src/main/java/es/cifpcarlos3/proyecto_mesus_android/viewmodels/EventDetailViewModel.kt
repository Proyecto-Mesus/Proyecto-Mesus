package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.models.Conversacion
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.RetrofitInstance
import es.cifpcarlos3.proyecto_mesus_android.data.repository.ChatProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class EventDetailUiState {
    object Idle : EventDetailUiState()
    object Loading : EventDetailUiState()
    data class ChatReady(val conversacion: Conversacion) : EventDetailUiState()
    data class Error(val message: String) : EventDetailUiState()
}

class EventDetailViewModel : ViewModel() {
    private val chatProvider = ChatProvider(RetrofitInstance.api)
    
    private val _uiState = MutableStateFlow<EventDetailUiState>(EventDetailUiState.Idle)
    val uiState: StateFlow<EventDetailUiState> = _uiState

    fun openOrCreateChat(eventoId: Int) {
        _uiState.value = EventDetailUiState.Loading
        viewModelScope.launch {
            chatProvider.getOrCreateChatEvento(eventoId).onSuccess {
                _uiState.value = EventDetailUiState.ChatReady(it)
            }.onFailure { error ->
                _uiState.value = EventDetailUiState.Error("No se pudo abrir el chat: ${error.message}")
            }
        }
    }
    
    fun resetState() {
        _uiState.value = EventDetailUiState.Idle
    }
}
