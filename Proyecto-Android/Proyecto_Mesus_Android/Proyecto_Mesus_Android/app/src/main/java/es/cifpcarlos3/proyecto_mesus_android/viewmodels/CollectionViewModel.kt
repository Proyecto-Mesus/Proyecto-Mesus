package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import es.cifpcarlos3.proyecto_mesus_android.data.models.Coleccion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class ColeccionUiState {
    object Idle : ColeccionUiState()
    object Loading : ColeccionUiState()
    data class Success(val list: List<Coleccion>) : ColeccionUiState()
    object ActionSuccess : ColeccionUiState()
    data class Error(val message: String) : ColeccionUiState()
}

class CollectionViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPrefs = application.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val dbHelper = DatabaseHelper()

    private val _uiState = MutableStateFlow<ColeccionUiState>(ColeccionUiState.Idle)
    val uiState: StateFlow<ColeccionUiState> = _uiState

    fun getColecciones(targetUserId: Int = -1) {
        val currentUserId = sharedPrefs.getInt("userId", -1)
        if (currentUserId == -1) {
            _uiState.value = ColeccionUiState.Error("SESSION_ERROR")
            return
        }

        val userIdToQuery = if (targetUserId != -1) targetUserId else currentUserId
        val isOwnProfile = (userIdToQuery == currentUserId)

        _uiState.value = ColeccionUiState.Loading
        viewModelScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                val lista = mutableListOf<Coleccion>()
                try {
                    val conn = dbHelper.getConnection()
                    conn?.use { c ->
                        val queryBuilder =
                            StringBuilder("SELECT id_coleccion, nombre, id_juego, publica FROM colecciones WHERE id_usuario = ?")
                        if (!isOwnProfile) {
                            queryBuilder.append(" AND publica = 1")
                        }

                        val stmt = c.prepareStatement(queryBuilder.toString())
                        stmt.setInt(1, userIdToQuery)
                        val rs = stmt.executeQuery()
                        while (rs.next()) {
                            lista.add(
                                Coleccion(
                                    idColeccion = rs.getInt("id_coleccion"),
                                    nombre = rs.getString("nombre"),
                                    idUsuario = userIdToQuery,
                                    idJuego = rs.getInt("id_juego"),
                                    publica = rs.getInt("publica")
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                lista
            }
            _uiState.value = ColeccionUiState.Success(resultado)
        }
    }

    fun deleteCollection(collectionId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    dbHelper.getConnection()?.use { conn ->
                        val stmtDeleteCards = conn.prepareStatement("DELETE FROM cartas WHERE id_coleccion = ?")
                        stmtDeleteCards.setInt(1, collectionId)
                        stmtDeleteCards.executeUpdate()

                        val stmt = conn.prepareStatement("DELETE FROM colecciones WHERE id_coleccion = ?")
                        stmt.setInt(1, collectionId)
                        stmt.executeUpdate()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            val currentUserId = sharedPrefs.getInt("userId", -1)
            getColecciones(currentUserId)
        }
    }
}
