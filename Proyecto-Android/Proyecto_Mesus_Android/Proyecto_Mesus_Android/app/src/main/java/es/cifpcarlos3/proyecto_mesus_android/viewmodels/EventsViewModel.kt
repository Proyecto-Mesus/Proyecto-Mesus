package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.models.Evento
import es.cifpcarlos3.proyecto_mesus_android.data.remote.RetrofitInstance
import es.cifpcarlos3.proyecto_mesus_android.data.repository.EventoProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EventoUiState {
    object Idle : EventoUiState()
    object Loading : EventoUiState()
    data class Success(val list: List<Evento>) : EventoUiState()
    object ActionSuccess : EventoUiState()
    data class Error(val message: String) : EventoUiState()
}

class EventsViewModel : ViewModel() {
    private val provider = EventoProvider(RetrofitInstance.api)
    private val _uiState = MutableStateFlow<EventoUiState>(EventoUiState.Idle)
    val uiState: StateFlow<EventoUiState> = _uiState.asStateFlow()

    private val _isListMode = MutableStateFlow(false)
    val isListMode: StateFlow<Boolean> = _isListMode.asStateFlow()

    fun toggleViewMode() {
        _isListMode.value = !_isListMode.value
    }

    fun fetchEvents() {
        viewModelScope.launch {
            _uiState.value = EventoUiState.Loading
            val result = provider.getEventos()
            result.onSuccess {
                _uiState.value = EventoUiState.Success(it)
            }.onFailure {
                _uiState.value = EventoUiState.Error(it.message ?: "Error desconocido")
            }
        }
    }

    fun fetchMyEvents(userId: Int) {
        viewModelScope.launch {
            _uiState.value = EventoUiState.Loading
            val result = provider.getEventosByUsuario(userId)
            result.onSuccess {
                _uiState.value = EventoUiState.Success(it)
            }.onFailure {
                _uiState.value = EventoUiState.Error(it.message ?: "Error desconocido")
            }
        }
    }
    
    fun deleteEvento(id: Int) {
        viewModelScope.launch {
            _uiState.value = EventoUiState.Loading
            val result = provider.deleteEvento(id)
            result.onSuccess {
                _uiState.value = EventoUiState.ActionSuccess
                fetchEvents()
            }.onFailure {
                _uiState.value = EventoUiState.Error(it.message ?: "Error al borrar")
            }
        }
    }
}
