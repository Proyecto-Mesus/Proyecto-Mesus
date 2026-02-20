package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.remote.RetrofitInstance
import es.cifpcarlos3.proyecto_mesus_android.data.repository.UsuarioProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewUserViewModel : ViewModel() {
    private var inputTextUser = ""
    private var inputTextEmail = ""
    private var inputTextPasswd = ""
    private var inputTextConfirmPassword = ""

    private val _uiState = MutableStateFlow<UsuarioUiState>(UsuarioUiState.Idle)
    val uiState: StateFlow<UsuarioUiState> = _uiState

    private val _isButtonEnabled = MutableStateFlow<Boolean>(false)
    val isButtonEnabled: StateFlow<Boolean> get() = _isButtonEnabled

    private val repository = UsuarioProvider(RetrofitInstance.api)

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

    fun performRegister() {
        if (inputTextPasswd != inputTextConfirmPassword) {
            _uiState.value = UsuarioUiState.Error("PASSWORDS_MISMATCH")
            return
        }

        _uiState.value = UsuarioUiState.Loading
        viewModelScope.launch {
            val result = repository.registerUsuario(inputTextUser, inputTextEmail, inputTextPasswd)
            result.onSuccess {
                _uiState.value = UsuarioUiState.ActionSuccess
            }.onFailure {
                val errorMsg = when {
                    it.message?.contains("409") == true -> "USER_EXISTS"
                    else -> "GENERAL_ERROR"
                }
                _uiState.value = UsuarioUiState.Error(errorMsg)
            }
        }
    }
}
