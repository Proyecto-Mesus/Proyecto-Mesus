package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import es.cifpcarlos3.proyecto_mesus_android.data.models.MercadoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.sql.ResultSet

sealed class MercadoUiState {
    object Idle : MercadoUiState()
    object Loading : MercadoUiState()
    data class Success(val list: List<MercadoItem>) : MercadoUiState()
    object ActionSuccess : MercadoUiState()
    data class Error(val message: String) : MercadoUiState()
}

class MarketplaceViewModel : ViewModel() {
    private val dbHelper = DatabaseHelper()
    private val _uiState = MutableStateFlow<MercadoUiState>(MercadoUiState.Idle)
    val uiState: StateFlow<MercadoUiState> = _uiState.asStateFlow()

    fun fetchItems() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = MercadoUiState.Loading
            try {
                dbHelper.getConnection().use { conn ->
                    val query = "SELECT m.*, c.nombre as nombre_carta FROM mercado_items m LEFT JOIN cartas c ON m.id_carta = c.id_carta"
                    conn.prepareStatement(query).use { stmt ->
                        val rs: ResultSet = stmt.executeQuery()
                        val items = mutableListOf<MercadoItem>()
                        while (rs.next()) {
                            items.add(
                                MercadoItem(
                                    rs.getInt("id_item"),
                                    rs.getString("nombre"),
                                    rs.getString("descripcion"),
                                    rs.getDouble("precio"),
                                    rs.getDouble("latitud"),
                                    rs.getDouble("longitud"),
                                    rs.getInt("id_usuario"),
                                    if (rs.getObject("id_carta") != null) rs.getInt("id_carta") else null,
                                    rs.getString("nombre_carta")
                                )
                            )
                        }
                        _uiState.value = MercadoUiState.Success(items)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = MercadoUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
