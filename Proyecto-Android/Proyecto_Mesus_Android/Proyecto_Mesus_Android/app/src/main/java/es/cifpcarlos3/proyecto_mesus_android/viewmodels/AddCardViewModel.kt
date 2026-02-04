package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import es.cifpcarlos3.proyecto_mesus_android.data.utils.CloudinaryHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddCardViewModel : ViewModel() {
    private val dbHelper = DatabaseHelper()

    private val _uiState = MutableStateFlow<CartaUiState>(CartaUiState.Idle)
    val uiState: StateFlow<CartaUiState> = _uiState

    fun guardarCarta(nombre: String, set: String, numeroSet: String, imageFile: Any?, collectionId: Int) {
        if (nombre.isBlank()) {
            _uiState.value = CartaUiState.Error("NOMBRE_VACIO")
            return
        }

        _uiState.value = CartaUiState.Loading
        
        viewModelScope.launch {
            val imageUrl = withContext(Dispatchers.IO) {
                imageFile?.let { CloudinaryHelper.uploadImage(it) }
            }

            if (imageFile != null && imageUrl == null) {
                _uiState.value = CartaUiState.Error("SUBIR_IMAGEN_ERROR")
                return@launch
            }

            val success = withContext(Dispatchers.IO) {
                try {
                    val conn = dbHelper.getConnection()
                    conn?.use { connection ->
                        val query = "INSERT INTO cartas (nombre, `set`, numero_set, imagen, id_coleccion) VALUES (?, ?, ?, ?, ?)"
                        val stmt = connection.prepareStatement(query)
                        stmt.setString(1, nombre)
                        stmt.setString(2, set)
                        stmt.setString(3, numeroSet)
                        stmt.setString(4, imageUrl)
                        stmt.setInt(5, collectionId)
                        stmt.executeUpdate() > 0
                    } ?: false
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }

            if (success) {
                _uiState.value = CartaUiState.ActionSuccess
            } else {
                _uiState.value = CartaUiState.Error("GUARDAR_DB_ERROR")
            }
        }
    }

    fun actualizarCarta(idCarta: Int, nombre: String, set: String, numeroSet: String, imageFile: Any?, collectionId: Int, oldImageUrl: String?) {
        if (nombre.isBlank()) {
            _uiState.value = CartaUiState.Error("NOMBRE_VACIO")
            return
        }

        _uiState.value = CartaUiState.Loading

        viewModelScope.launch {
            var imageUrl = oldImageUrl
            if (imageFile != null) {
                imageUrl = withContext(Dispatchers.IO) {
                    CloudinaryHelper.uploadImage(imageFile)
                }
            }

            if (imageFile != null && imageUrl == null) {
                _uiState.value = CartaUiState.Error("SUBIR_IMAGEN_ERROR")
                return@launch
            }

            val success = withContext(Dispatchers.IO) {
                try {
                    val conn = dbHelper.getConnection()
                    conn?.use { connection ->
                        val query = "UPDATE cartas SET nombre = ?, `set` = ?, numero_set = ?, imagen = ?, id_coleccion = ? WHERE id_carta = ?"
                        val stmt = connection.prepareStatement(query)
                        stmt.setString(1, nombre)
                        stmt.setString(2, set)
                        stmt.setString(3, numeroSet)
                        stmt.setString(4, imageUrl)
                        stmt.setInt(5, collectionId)
                        stmt.setInt(6, idCarta)
                        stmt.executeUpdate() > 0
                    } ?: false
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }

            if (success) {
                _uiState.value = CartaUiState.ActionSuccess
            } else {
                _uiState.value = CartaUiState.Error("GUARDAR_DB_ERROR")
            }
        }
    }
}
