package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import es.cifpcarlos3.proyecto_mesus_android.data.models.Usuario
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

    private val dbHelper = DatabaseHelper()
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
            var loginSuccess = false
            try {
                withContext(Dispatchers.IO) {
                    val conn = dbHelper.getConnection()
                    conn?.use { connection ->
                        val consulta = "SELECT id_usuario, nombre_usuario FROM usuarios WHERE nombre_usuario = ? AND password = ?"
                        val stmt = connection.prepareStatement(consulta)
                        stmt.setString(1, inputTextUser)
                        stmt.setString(2, inputTextPasswd)
                        
                        val rs = stmt.executeQuery()
                        if (rs.next()) {
                            loginSuccess = true
                            val userId = rs.getInt("id_usuario")
                            sharedPrefs.edit().apply {
                                putBoolean("isLoggedIn", true)
                                putString("username", inputTextUser)
                                putInt("userId", userId)
                                apply()
                            }
                        }
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            
            if (loginSuccess) {
                _uiState.value = UsuarioUiState.ActionSuccess
            } else {
                _uiState.value = UsuarioUiState.Error("LOGIN_FAILED")
            }
        }
    }
}
