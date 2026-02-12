package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import es.cifpcarlos3.proyecto_mesus_android.data.models.Carta
import es.cifpcarlos3.proyecto_mesus_android.data.models.Coleccion
import es.cifpcarlos3.proyecto_mesus_android.data.models.Juego
import es.cifpcarlos3.proyecto_mesus_android.data.models.Usuario
import es.cifpcarlos3.proyecto_mesus_android.data.remote.RetrofitInstance
import es.cifpcarlos3.proyecto_mesus_android.data.repository.CartaProvider
import es.cifpcarlos3.proyecto_mesus_android.data.repository.JuegoProvider
import es.cifpcarlos3.proyecto_mesus_android.data.utils.CloudinaryHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddCardViewModel : ViewModel() {
    private val juegoProvider = JuegoProvider(RetrofitInstance.api)
    private val cartaProvider = CartaProvider(RetrofitInstance.api)

    private val _uiState = MutableStateFlow<CartaUiState>(CartaUiState.Idle)
    val uiState: StateFlow<CartaUiState> = _uiState

    fun guardarCarta(nombre: String, set: String, numeroSet: String, imageFile: Any?, coleccion: Coleccion) {
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

            var juegoObject: Juego? = null
            val juegosResult = withContext(Dispatchers.IO) { juegoProvider.getJuegos() }
            
            juegoObject = juegosResult.getOrNull()?.find { it.idJuego == coleccion.idJuego }
            
            if (juegoObject == null) {
               juegoObject = Juego(coleccion.idJuego, "Unknown")
            }
            
            val usuario = Usuario(coleccion.idUsuario, "Unknown")
            val carta = Carta(0, nombre, set, numeroSet, coleccion.idColeccion, imageUrl)

            val result = withContext(Dispatchers.IO) {
                cartaProvider.createCarta(carta, coleccion, usuario, juegoObject)
            }

            result.onSuccess {
                _uiState.value = CartaUiState.ActionSuccess
            }.onFailure { exception ->
                 _uiState.value = CartaUiState.Error(exception.message ?: "GUARDAR_DB_ERROR")
            }
        }
    }

    fun actualizarCarta(idCarta: Int, nombre: String, set: String, numeroSet: String, imageFile: Any?, coleccion: Coleccion, oldImageUrl: String?) {
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
            
            var juegoObject: Juego? = null
            val juegosResult = withContext(Dispatchers.IO) { juegoProvider.getJuegos() }
            
            juegoObject = juegosResult.getOrNull()?.find { it.idJuego == coleccion.idJuego }
            
            if (juegoObject == null) {
               juegoObject = Juego(coleccion.idJuego, "Unknown")
            }
            
             val usuario = Usuario(coleccion.idUsuario, "Unknown")
             val carta = Carta(idCarta, nombre, set, numeroSet, coleccion.idColeccion, imageUrl)

            val result = withContext(Dispatchers.IO) {
                cartaProvider.updateCarta(idCarta, carta, coleccion, usuario, juegoObject)
            }

            result.onSuccess {
                _uiState.value = CartaUiState.ActionSuccess
            }.onFailure { exception ->
                 _uiState.value = CartaUiState.Error(exception.message ?: "GUARDAR_DB_ERROR")
            }
        }
    }
}
