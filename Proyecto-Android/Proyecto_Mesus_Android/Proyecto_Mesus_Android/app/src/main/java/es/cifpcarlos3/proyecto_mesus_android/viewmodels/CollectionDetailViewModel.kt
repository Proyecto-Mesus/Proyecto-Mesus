package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import es.cifpcarlos3.proyecto_mesus_android.data.models.Carta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class CartaUiState {
    object Idle : CartaUiState()
    object Loading : CartaUiState()
    data class Success(val list: List<Carta>, val ownerId: Int = -1) : CartaUiState()
    object ActionSuccess : CartaUiState()
    data class Error(val message: String) : CartaUiState()
}

class CollectionDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val dbHelper = DatabaseHelper()

    private val _uiState = MutableStateFlow<CartaUiState>(CartaUiState.Idle)
    val uiState: StateFlow<CartaUiState> = _uiState

    private val _isGridView = MutableStateFlow(false)
    val isGridView: StateFlow<Boolean> = _isGridView

    fun toggleViewMode() {
        _isGridView.value = !_isGridView.value
    }

    fun getCartas(collectionId: Int) {
        viewModelScope.launch {
            _uiState.value = CartaUiState.Loading
            val resultado = withContext(Dispatchers.IO) {
                var list = emptyList<Carta>()
                var owner = -1
                try {
                    val conn = dbHelper.getConnection()
                    conn?.use { c ->
                        val query =
                            "SELECT id_carta, nombre, `set`, numero_set, imagen FROM cartas WHERE id_coleccion = ?"
                        val stmt = c.prepareStatement(query)
                        stmt.setInt(1, collectionId)
                        val rs = stmt.executeQuery()
                        val lista = mutableListOf<Carta>()
                        while (rs.next()) {
                            lista.add(
                                Carta(
                                    idCarta = rs.getInt("id_carta"),
                                    nombre = rs.getString("nombre"),
                                    set = rs.getString("set"),
                                    numeroSet = rs.getString("numero_set"),
                                    imagen = rs.getString("imagen"),
                                    idColeccion = collectionId
                                )
                            )
                        }
                        list = lista
                        
                        val ownerQuery = "SELECT id_usuario FROM colecciones WHERE id_coleccion = ?"
                        val ownerStmt = c.prepareStatement(ownerQuery)
                        ownerStmt.setInt(1, collectionId)
                        val ownerRs = ownerStmt.executeQuery()
                        if (ownerRs.next()) {
                            owner = ownerRs.getInt("id_usuario")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Pair(list, owner)
            }
            _uiState.value = CartaUiState.Success(resultado.first, resultado.second)
        }
    }

    fun deleteCarta(cardId: Int, collectionId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    dbHelper.getConnection()?.use { conn ->
                        val stmt = conn.prepareStatement("DELETE FROM cartas WHERE id_carta = ?")
                        stmt.setInt(1, cardId)
                        stmt.executeUpdate()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            getCartas(collectionId)
        }
    }
}
