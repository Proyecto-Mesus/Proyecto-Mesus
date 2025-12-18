package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel: ViewModel() {
    private val _inputTextUser = MutableLiveData<String>()
    private val _inputTextPasswd = MutableLiveData<String>()
    
    // Ahora usaremos esto para notificar el resultado del login (true/false)
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private val _isButtonEnabled = MutableLiveData<Boolean>()
    val isButtonEnabled: LiveData<Boolean> get()= _isButtonEnabled

    private val dbHelper = DatabaseHelper()

    init{
        _isButtonEnabled.value = false
        _inputTextUser.value = ""
        _inputTextPasswd.value = ""
    }

    fun onTextChanged(value: String)
    {
        _inputTextUser.value = value
        _isButtonEnabled.value = validateInput()
    }

    fun onPasswdTextChanged(value: String) {
        _inputTextPasswd.value = value
        _isButtonEnabled.value = validateInput()
    }

    private fun validateInput(): Boolean
    {
        val user = _inputTextUser.value ?: ""
        val passwd = _inputTextPasswd.value ?: ""
        return user.isNotEmpty() && passwd.length >= 4
    }

    fun performLogin() {
        val user = _inputTextUser.value ?: ""
        val passwd = _inputTextPasswd.value ?: ""
        
        viewModelScope.launch {
            var loginSuccess = false
            try {
                withContext(Dispatchers.IO) {
                    val conn = dbHelper.getConnection()
                    conn?.use { connection ->
                        val consulta = "SELECT * FROM usuarios WHERE nombre = ? AND password = ?"
                        val stmt = connection.prepareStatement(consulta)
                        stmt.setString(1, user)
                        stmt.setString(2, passwd)
                        
                        val rs = stmt.executeQuery()
                        if (rs.next()) {
                            loginSuccess = true
                        }
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            
            _loginResult.value = loginSuccess
        }
    }
}
