package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import es.cifpcarlos3.proyecto_mesus_android.data.models.Evento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.sql.ResultSet

sealed class EventoUiState {
    object Idle : EventoUiState()
    object Loading : EventoUiState()
    data class Success(val list: List<Evento>) : EventoUiState()
    object ActionSuccess : EventoUiState()
    data class Error(val message: String) : EventoUiState()
}

class EventsViewModel : ViewModel() {
    private val dbHelper = DatabaseHelper()
    private val _uiState = MutableStateFlow<EventoUiState>(EventoUiState.Idle)
    val uiState: StateFlow<EventoUiState> = _uiState.asStateFlow()

    private val _isListMode = MutableStateFlow(false)
    val isListMode: StateFlow<Boolean> = _isListMode.asStateFlow()

    fun toggleViewMode() {
        _isListMode.value = !_isListMode.value
    }

    fun fetchEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = EventoUiState.Loading
            try {
                dbHelper.getConnection().use { conn ->
                    val query = "SELECT * FROM eventos"
                    conn.prepareStatement(query).use { stmt ->
                        val rs: ResultSet = stmt.executeQuery()
                        val events = mutableListOf<Evento>()
                        while (rs.next()) {
                            events.add(
                                Evento(
                                    rs.getInt("id_evento"),
                                    rs.getString("nombre"),
                                    rs.getString("descripcion"),
                                    rs.getString("fecha"),
                                    rs.getDouble("latitud"),
                                    rs.getDouble("longitud")
                                )
                            )
                        }
                        _uiState.value = EventoUiState.Success(events)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = EventoUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun fetchMyEvents(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = EventoUiState.Loading
            try {
                dbHelper.getConnection().use { conn ->
                    // Assuming there's an id_usuario column in eventos table.
                    // If not, I'll need to verify the table schema.
                    val query = "SELECT * FROM eventos WHERE id_usuario = ?"
                    conn.prepareStatement(query).use { stmt ->
                        stmt.setInt(1, userId)
                        val rs: ResultSet = stmt.executeQuery()
                        val events = mutableListOf<Evento>()
                        while (rs.next()) {
                            events.add(
                                Evento(
                                    rs.getInt("id_evento"),
                                    rs.getString("nombre"),
                                    rs.getString("descripcion"),
                                    rs.getString("fecha"),
                                    rs.getDouble("latitud"),
                                    rs.getDouble("longitud")
                                )
                            )
                        }
                        _uiState.value = EventoUiState.Success(events)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = EventoUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
