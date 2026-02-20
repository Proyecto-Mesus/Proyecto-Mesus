package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.models.Coleccion
import es.cifpcarlos3.proyecto_mesus_android.data.remote.RetrofitInstance
import es.cifpcarlos3.proyecto_mesus_android.data.repository.ColeccionProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ColeccionUiState {
    object Idle : ColeccionUiState()
    object Loading : ColeccionUiState()
    data class Success(val list: List<Coleccion>) : ColeccionUiState()
    object ActionSuccess : ColeccionUiState()
    data class Error(val message: String) : ColeccionUiState()
}

class CollectionViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPrefs = application.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val provider = ColeccionProvider(RetrofitInstance.api)

    private val _uiState = MutableStateFlow<ColeccionUiState>(ColeccionUiState.Idle)
    val uiState: StateFlow<ColeccionUiState> = _uiState

    fun getColecciones(targetUserId: Int = -1) {
        val currentUserId = sharedPrefs.getInt("userId", -1)
        if (currentUserId == -1) {
            _uiState.value = ColeccionUiState.Error("SESSION_ERROR")
            return
        }

        val userIdToQuery = if (targetUserId != -1) targetUserId else currentUserId
        
        _uiState.value = ColeccionUiState.Loading
        viewModelScope.launch {
            val result = provider.getColecciones(userIdToQuery)
            result.onSuccess {
                _uiState.value = ColeccionUiState.Success(it)
            }.onFailure {
                _uiState.value = ColeccionUiState.Error(it.message ?: "LOAD_FAILED")
            }
        }
    }

    fun deleteCollection(collectionId: Int) {
        _uiState.value = ColeccionUiState.Loading
        viewModelScope.launch {
            val result = provider.deleteColeccion(collectionId)
            result.onSuccess {
                _uiState.value = ColeccionUiState.ActionSuccess
                val currentUserId = sharedPrefs.getInt("userId", -1)
                getColecciones(currentUserId)
            }.onFailure {
                _uiState.value = ColeccionUiState.Error(it.message ?: "DELETE_FAILED")
            }
        }
    }
}
