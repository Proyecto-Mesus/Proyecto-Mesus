package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.models.Evento
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.RetrofitInstance
import es.cifpcarlos3.proyecto_mesus_android.data.repository.EventoProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEventViewModel : ViewModel() {
    private val provider = EventoProvider(RetrofitInstance.api)
    private val _uiState = MutableStateFlow<EventoUiState>(EventoUiState.Idle)
    val uiState: StateFlow<EventoUiState> = _uiState.asStateFlow()

    fun saveEvent(nombre: String, descripcion: String, fecha: String, lat: Double, lng: Double, idUsuario: Int) {
        if (nombre.isBlank() || descripcion.isBlank() || fecha.isBlank()) {
            _uiState.value = EventoUiState.Error("Todos los campos son obligatorios")
            return
        }

        _uiState.value = EventoUiState.Loading
        viewModelScope.launch {
            val evento = Evento(0, nombre, descripcion, fecha, lat, lng)
            val result = provider.createEvento(evento, idUsuario)
            result.onSuccess {
                _uiState.value = EventoUiState.ActionSuccess
            }.onFailure {
                _uiState.value = EventoUiState.Error(it.message ?: "Error al guardar el evento")
            }
        }
    }

    fun updateEvent(id: Int, nombre: String, descripcion: String, fecha: String, lat: Double, lng: Double, idUsuario: Int) {
        if (nombre.isBlank() || descripcion.isBlank() || fecha.isBlank()) {
            _uiState.value = EventoUiState.Error("Todos los campos son obligatorios")
            return
        }

        _uiState.value = EventoUiState.Loading
        viewModelScope.launch {
            val evento = Evento(id, nombre, descripcion, fecha, lat, lng)
            val result = provider.updateEvento(id, evento, idUsuario)
            result.onSuccess {
                _uiState.value = EventoUiState.ActionSuccess
            }.onFailure {
                _uiState.value = EventoUiState.Error(it.message ?: "Error al actualizar el evento")
            }
        }
    }
}
