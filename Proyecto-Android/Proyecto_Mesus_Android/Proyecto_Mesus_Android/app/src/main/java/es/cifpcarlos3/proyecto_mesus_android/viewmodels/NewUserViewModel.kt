package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import es.cifpcarlos3.proyecto_mesus_android.data.models.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewUserViewModel : ViewModel() {
    private var inputTextUser = ""
    private var inputTextEmail = ""
    private var inputTextPasswd = ""
    private var inputTextConfirmPassword = ""

    private val _uiState = MutableStateFlow<UsuarioUiState>(UsuarioUiState.Idle)
    val uiState: StateFlow<UsuarioUiState> = _uiState

    private val _isButtonEnabled = MutableStateFlow<Boolean>(false)
    val isButtonEnabled: StateFlow<Boolean> get() = _isButtonEnabled

    fun onTextChanged(value: String) {
        inputTextUser = value
        _isButtonEnabled.value = validateInput()
    }

    fun onEmailTextChanged(value: String) {
        inputTextEmail = value
        _isButtonEnabled.value = validateInput()
    }

    fun onPasswdTextChanged(value: String) {
        inputTextPasswd = value
        _isButtonEnabled.value = validateInput()
    }

    fun onConfirmPasswdTextChanged(value: String) {
        inputTextConfirmPassword = value
        _isButtonEnabled.value = validateInput()
    }

    private fun validateInput(): Boolean {
        return inputTextUser.isNotEmpty() && inputTextEmail.isNotEmpty() && inputTextPasswd.length >= 4 && inputTextConfirmPassword.length >= 4
    }

    private val dbHelper = DatabaseHelper()

    fun performRegister() {
        if (inputTextPasswd != inputTextConfirmPassword) {
            _uiState.value = UsuarioUiState.Error("PASSWORDS_MISMATCH")
            return
        }

        _uiState.value = UsuarioUiState.Loading
        viewModelScope.launch {
            var result = "ERROR"
            try {
                withContext(Dispatchers.IO) {
                    val conn = dbHelper.getConnection()
                    conn?.use { connection ->
                        val checkUser = "SELECT COUNT(*) FROM usuarios WHERE nombre_usuario = ?"
                        val stmtCheck = connection.prepareStatement(checkUser)
                        stmtCheck.setString(1, inputTextUser)
                        val rsCheck = stmtCheck.executeQuery()
                        if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                            result = "USER_EXISTS"
                        } else {
                            val checkEmail = "SELECT COUNT(*) FROM usuarios WHERE email = ?"
                            val stmtCheckEmail = connection.prepareStatement(checkEmail)
                            stmtCheckEmail.setString(1, inputTextEmail)
                            val rsCheckEmail = stmtCheckEmail.executeQuery()
                            if (rsCheckEmail.next() && rsCheckEmail.getInt(1) > 0) {
                                result = "EMAIL_EXISTS"
                            } else {
                                val insertSql =
                                    "INSERT INTO usuarios (nombre_usuario, password, email) VALUES (?, ?, ?)"
                                val stmtInsert = connection.prepareStatement(insertSql)
                                stmtInsert.setString(1, inputTextUser)
                                stmtInsert.setString(2, inputTextPasswd)
                                stmtInsert.setString(3, inputTextEmail)
                                if (stmtInsert.executeUpdate() > 0) {
                                    result = "SUCCESS"
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            when (result) {
                "SUCCESS" -> _uiState.value = UsuarioUiState.ActionSuccess
                "USER_EXISTS" -> _uiState.value = UsuarioUiState.Error("USER_EXISTS")
                "EMAIL_EXISTS" -> _uiState.value = UsuarioUiState.Error("EMAIL_EXISTS")
                else -> _uiState.value = UsuarioUiState.Error("GENERAL_ERROR")
            }
        }
    }
}
