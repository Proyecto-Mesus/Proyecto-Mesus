package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEventViewModel : ViewModel() {
    private val dbHelper = DatabaseHelper()
    private val _uiState = MutableStateFlow<EventoUiState>(EventoUiState.Idle)
    val uiState: StateFlow<EventoUiState> = _uiState.asStateFlow()

    fun saveEvent(nombre: String, descripcion: String, fecha: String, lat: Double, lng: Double, idUsuario: Int) {
        if (nombre.isBlank() || descripcion.isBlank() || fecha.isBlank()) {
            _uiState.value = EventoUiState.Error("Todos los campos son obligatorios")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = EventoUiState.Loading
            try {
                dbHelper.getConnection().use { conn ->
                    val query = "INSERT INTO eventos (nombre, descripcion, fecha, latitud, longitud, id_usuario) VALUES (?, ?, ?, ?, ?, ?)"
                    conn.prepareStatement(query).use { stmt ->
                        stmt.setString(1, nombre)
                        stmt.setString(2, descripcion)
                        stmt.setString(3, fecha)
                        stmt.setDouble(4, lat)
                        stmt.setDouble(5, lng)
                        stmt.setInt(6, idUsuario)
                        stmt.executeUpdate()
                        _uiState.value = EventoUiState.ActionSuccess
                    }
                }
            } catch (e: Exception) {
                _uiState.value = EventoUiState.Error(e.message ?: "Error al guardar el evento")
            }
        }
    }
}
