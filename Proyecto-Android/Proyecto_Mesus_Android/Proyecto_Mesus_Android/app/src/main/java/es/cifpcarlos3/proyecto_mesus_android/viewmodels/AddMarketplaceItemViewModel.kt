package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import es.cifpcarlos3.proyecto_mesus_android.data.models.Carta
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddMarketplaceItemViewModel : ViewModel() {
    private val dbHelper = DatabaseHelper()
    
    private val _uiState = MutableStateFlow<MercadoUiState>(MercadoUiState.Idle)
    val uiState: StateFlow<MercadoUiState> = _uiState.asStateFlow()

    private val _cartasState = MutableStateFlow<CartaUiState>(CartaUiState.Idle)
    val cartasState: StateFlow<CartaUiState> = _cartasState.asStateFlow()

    fun fetchUserCards(userId: Int) {
        viewModelScope.launch {
            _cartasState.value = CartaUiState.Loading
            val resultado = withContext(Dispatchers.IO) {
                val lista = mutableListOf<Carta>()
                try {
                    dbHelper.getConnection()?.use { conn ->
                        val query = """
                            SELECT c.id_carta, c.nombre, c.`set`, c.numero_set, c.imagen, c.id_coleccion 
                            FROM cartas c
                            JOIN colecciones col ON c.id_coleccion = col.id_coleccion
                            WHERE col.id_usuario = ?
                        """.trimIndent()
                        val stmt = conn.prepareStatement(query)
                        stmt.setInt(1, userId)
                        val rs = stmt.executeQuery()
                        while (rs.next()) {
                            lista.add(Carta(
                                rs.getInt("id_carta"),
                                rs.getString("nombre"),
                                rs.getString("set"),
                                rs.getString("numero_set"),
                                rs.getInt("id_coleccion"),
                                rs.getString("imagen")
                            ))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                lista
            }
            _cartasState.value = CartaUiState.Success(resultado)
        }
    }

    fun saveItem(nombre: String, descripcion: String, precio: Double, lat: Double, lng: Double, idUsuario: Int, idCarta: Int?) {
        if (nombre.isBlank() || descripcion.isBlank()) {
            _uiState.value = MercadoUiState.Error("Nombre y descripción son obligatorios")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = MercadoUiState.Loading
            try {
                dbHelper.getConnection().use { conn ->
                    val query = "INSERT INTO mercado_items (nombre, descripcion, precio, latitud, longitud, id_usuario, id_carta) VALUES (?, ?, ?, ?, ?, ?, ?)"
                    conn.prepareStatement(query).use { stmt ->
                        stmt.setString(1, nombre)
                        stmt.setString(2, descripcion)
                        stmt.setDouble(3, precio)
                        stmt.setDouble(4, lat)
                        stmt.setDouble(5, lng)
                        stmt.setInt(6, idUsuario)
                        if (idCarta != null) stmt.setInt(7, idCarta) else stmt.setNull(7, java.sql.Types.INTEGER)
                        stmt.executeUpdate()
                        _uiState.value = MercadoUiState.ActionSuccess
                    }
                }
            } catch (e: Exception) {
                _uiState.value = MercadoUiState.Error(e.message ?: "Error al guardar")
            }
        }
    }
}
