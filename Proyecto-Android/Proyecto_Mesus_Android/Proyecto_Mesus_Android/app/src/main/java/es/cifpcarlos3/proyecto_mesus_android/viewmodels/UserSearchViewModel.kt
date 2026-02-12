package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import es.cifpcarlos3.proyecto_mesus_android.data.models.Usuario
import es.cifpcarlos3.proyecto_mesus_android.data.remote.RetrofitInstance
import es.cifpcarlos3.proyecto_mesus_android.data.repository.UsuarioProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserSearchViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UsuarioProvider(RetrofitInstance.api)

    private val _uiState = MutableStateFlow<UsuarioUiState>(UsuarioUiState.Idle)
    val uiState: StateFlow<UsuarioUiState> = _uiState

    fun searchUsers(query: String) {
        if (query.isBlank()) {
            _uiState.value = UsuarioUiState.SuccessList(emptyList())
            return
        }

        _uiState.value = UsuarioUiState.Loading
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.searchUsuarios(query)
            }
            result.onSuccess { users ->
                _uiState.value = UsuarioUiState.SuccessList(users)
            }.onFailure { exception ->
                _uiState.value = UsuarioUiState.Error(exception.message ?: "Error al buscar usuarios")
            }
        }
    }
}
