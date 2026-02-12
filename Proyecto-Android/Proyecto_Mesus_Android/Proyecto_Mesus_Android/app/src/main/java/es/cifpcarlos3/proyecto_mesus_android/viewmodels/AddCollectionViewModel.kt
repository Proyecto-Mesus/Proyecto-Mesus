package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import es.cifpcarlos3.proyecto_mesus_android.data.models.Coleccion
import es.cifpcarlos3.proyecto_mesus_android.data.models.Juego
import es.cifpcarlos3.proyecto_mesus_android.data.models.Usuario
import es.cifpcarlos3.proyecto_mesus_android.data.remote.RetrofitInstance
import es.cifpcarlos3.proyecto_mesus_android.data.repository.ColeccionProvider
import es.cifpcarlos3.proyecto_mesus_android.data.repository.JuegoProvider
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
    private val juegoProvider = JuegoProvider(RetrofitInstance.api)
    private val coleccionProvider = ColeccionProvider(RetrofitInstance.api)
    private val sharedPrefs = application.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow<ColeccionUiState>(ColeccionUiState.Idle)
    val uiState: StateFlow<ColeccionUiState> = _uiState

    private val _juegosState = MutableStateFlow<JuegoUiState>(JuegoUiState.Idle)
    val juegosState: StateFlow<JuegoUiState> = _juegosState

    fun fetchJuegos() {
        _juegosState.value = JuegoUiState.Loading
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                juegoProvider.getJuegos()
            }
            result.onSuccess { juegos ->
                _juegosState.value = JuegoUiState.Success(juegos)
            }.onFailure { exception ->
                _juegosState.value = JuegoUiState.Error(exception.message ?: "Error al cargar juegos")
            }
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

        val username = sharedPrefs.getString("username", "User") ?: "User"

        val juegos = (_juegosState.value as? JuegoUiState.Success)?.list
        val juegoSeleccionado = juegos?.find { it.idJuego == idJuego } ?: Juego(idJuego, "Unknown")

        val usuario = Usuario(userId, username)
        
        val coleccion = Coleccion(
            idColeccion = 0,
            nombre = nombre,
            idUsuario = userId,
            idJuego = idJuego,
            publica = if (isPublic) 1 else 0
        )

        _uiState.value = ColeccionUiState.Loading
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                coleccionProvider.createColeccion(coleccion, usuario, juegoSeleccionado)
            }

            result.onSuccess {
                _uiState.value = ColeccionUiState.ActionSuccess
            }.onFailure { exception ->
                _uiState.value = ColeccionUiState.Error(exception.message ?: "GUARDAR_ERROR")
            }
        }
    }

    fun actualizarColeccion(idColeccion: Int, nombre: String, idJuego: Int, isPublic: Boolean) {
        if (nombre.isBlank()) {
            _uiState.value = ColeccionUiState.Error("NOMBRE_VACIO")
            return
        }
        
        val userId = sharedPrefs.getInt("userId", -1)
        val username = sharedPrefs.getString("username", "User") ?: "User"
        
        val juegos = (_juegosState.value as? JuegoUiState.Success)?.list
        val juegoSeleccionado = juegos?.find { it.idJuego == idJuego } ?: Juego(idJuego, "Unknown")
        
        val usuario = Usuario(userId, username)

        val coleccion = Coleccion(
            idColeccion = idColeccion,
            nombre = nombre,
            idUsuario = userId,
            idJuego = idJuego,
            publica = if (isPublic) 1 else 0
        )

        _uiState.value = ColeccionUiState.Loading
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                coleccionProvider.updateColeccion(idColeccion, coleccion, usuario, juegoSeleccionado)
            }

            result.onSuccess {
                _uiState.value = ColeccionUiState.ActionSuccess
            }.onFailure { exception ->
                _uiState.value = ColeccionUiState.Error(exception.message ?: "ACTUALIZAR_ERROR")
            }
        }
    }
}
