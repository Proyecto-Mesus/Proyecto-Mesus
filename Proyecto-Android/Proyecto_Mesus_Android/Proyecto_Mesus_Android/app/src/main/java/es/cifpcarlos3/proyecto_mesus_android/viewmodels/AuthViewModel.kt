package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.models.Usuario
import es.cifpcarlos3.proyecto_mesus_android.data.retrofitApi.RetrofitInstance
import es.cifpcarlos3.proyecto_mesus_android.data.repository.UsuarioProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class UsuarioUiState {
    object Idle : UsuarioUiState()
    object Loading : UsuarioUiState()
    data class SuccessList(val list: List<Usuario>) : UsuarioUiState()
    object ActionSuccess : UsuarioUiState()
    data class Error(val message: String) : UsuarioUiState()
}

class AuthViewModel(application: Application): AndroidViewModel(application) {
    private var inputTextUser = ""
    private var inputTextPasswd = ""

    private val _uiState = MutableStateFlow<UsuarioUiState>(UsuarioUiState.Idle)
    val uiState: StateFlow<UsuarioUiState> = _uiState

    private val _isButtonEnabled = MutableStateFlow<Boolean>(false)
    val isButtonEnabled: StateFlow<Boolean> get()= _isButtonEnabled

    private val repository = UsuarioProvider(RetrofitInstance.api)
    private val sharedPrefs = application.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun onTextChanged(value: String)
    {
        inputTextUser = value
        _isButtonEnabled.value = validateInput()
    }

    fun onPasswdTextChanged(value: String) {
        inputTextPasswd = value
        _isButtonEnabled.value = validateInput()
    }

    private fun validateInput(): Boolean
    {
        return inputTextUser.isNotEmpty() && inputTextPasswd.length >= 4
    }

    fun performLogin() {
        _uiState.value = UsuarioUiState.Loading
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.login(inputTextUser, inputTextPasswd)
            }
            
            result.onSuccess { user ->
                sharedPrefs.edit().apply {
                    putBoolean("isLoggedIn", true)
                    putString("username", user.nombre)
                    putInt("userId", user.idUsuario)
                    apply()
                }
                _uiState.value = UsuarioUiState.ActionSuccess
            }.onFailure { exception ->
                _uiState.value = UsuarioUiState.Error(exception.message ?: "LOGIN_FAILED")
            }
        }
    }
}
