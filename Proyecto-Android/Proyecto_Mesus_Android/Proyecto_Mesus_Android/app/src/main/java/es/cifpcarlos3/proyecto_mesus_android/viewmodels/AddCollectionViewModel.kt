package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import es.cifpcarlos3.proyecto_mesus_android.data.models.Juego
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class JuegoUiState {
    object Idle : JuegoUiState()
    object Loading : JuegoUiState()
    data class Success(val list: List<Juego>) : JuegoUiState()
    data class Error(val message: String) : JuegoUiState()
}

class AddCollectionViewModel(application: Application) : AndroidViewModel(application) {
    private val dbHelper = DatabaseHelper()
    private val sharedPrefs = application.getSharedPreferences("user_session", android.content.Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow<ColeccionUiState>(ColeccionUiState.Idle)
    val uiState: StateFlow<ColeccionUiState> = _uiState

    private val _juegosState = MutableStateFlow<JuegoUiState>(JuegoUiState.Idle)
    val juegosState: StateFlow<JuegoUiState> = _juegosState

    fun fetchJuegos() {
        _juegosState.value = JuegoUiState.Loading
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                val resultList = mutableListOf<Juego>()
                try {
                    val conn = dbHelper.getConnection()
                    conn?.use { connection ->
                        val query = "SELECT id_juego, nombre FROM juegos"
                        val stmt = connection.prepareStatement(query)
                        val rs = stmt.executeQuery()
                        while (rs.next()) {
                            resultList.add(Juego(rs.getInt("id_juego"), rs.getString("nombre")))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                resultList
            }
            _juegosState.value = JuegoUiState.Success(list)
        }
    }

    fun guardarColeccion(nombre: String, idJuego: Int, isPublic: Boolean) {
        if (nombre.isBlank()) {
            _uiState.value = ColeccionUiState.Error("NOMBRE_VACIO")
            return
        }

        val userId = sharedPrefs.getInt("userId", -1)
        if (userId == -1) {
            _uiState.value = ColeccionUiState.Error("SESSION_ERROR")
            return
        }

        _uiState.value = ColeccionUiState.Loading
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val conn = dbHelper.getConnection()
                    conn?.use { connection ->
                        val query =
                            "INSERT INTO colecciones (nombre, id_usuario, id_juego, publica) VALUES (?, ?, ?, ?)"
                        val stmt = connection.prepareStatement(query)
                        stmt.setString(1, nombre)
                        stmt.setInt(2, userId)
                        stmt.setInt(3, idJuego)
                        stmt.setInt(4, if (isPublic) 1 else 0)
                        stmt.executeUpdate() > 0
                    } ?: false
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }

            if (result) {
                _uiState.value = ColeccionUiState.ActionSuccess
            } else {
                _uiState.value = ColeccionUiState.Error("GUARDAR_ERROR")
            }
        }
    }

    fun actualizarColeccion(idColeccion: Int, nombre: String, idJuego: Int, isPublic: Boolean) {
        if (nombre.isBlank()) {
            _uiState.value = ColeccionUiState.Error("NOMBRE_VACIO")
            return
        }

        _uiState.value = ColeccionUiState.Loading
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val conn = dbHelper.getConnection()
                    conn?.use { connection ->
                        val query = "UPDATE colecciones SET nombre = ?, id_juego = ?, publica = ? WHERE id_coleccion = ?"
                        val stmt = connection.prepareStatement(query)
                        stmt.setString(1, nombre)
                        stmt.setInt(2, idJuego)
                        stmt.setInt(3, if (isPublic) 1 else 0)
                        stmt.setInt(4, idColeccion)
                        stmt.executeUpdate() > 0
                    } ?: false
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }

            if (result) {
                _uiState.value = ColeccionUiState.ActionSuccess
            } else {
                _uiState.value = ColeccionUiState.Error("ACTUALIZAR_ERROR")
            }
        }
    }
}
