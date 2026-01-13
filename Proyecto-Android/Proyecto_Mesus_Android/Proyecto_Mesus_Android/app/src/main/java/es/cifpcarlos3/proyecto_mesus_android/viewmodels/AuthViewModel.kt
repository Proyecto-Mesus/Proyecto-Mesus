package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(application: Application): AndroidViewModel(application) {
    private val _inputTextUser = MutableLiveData<String>()
    private val _inputTextPasswd = MutableLiveData<String>()
    
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private val _isButtonEnabled = MutableLiveData<Boolean>()
    val isButtonEnabled: LiveData<Boolean> get()= _isButtonEnabled

    private val dbHelper = DatabaseHelper()
    private val sharedPrefs = application.getSharedPreferences("user_session", Context.MODE_PRIVATE)

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
                        val consulta = "SELECT id_usuario, nombre_usuario FROM usuarios WHERE nombre_usuario = ? AND password = ?"
                        val stmt = connection.prepareStatement(consulta)
                        stmt.setString(1, user)
                        stmt.setString(2, passwd)
                        
                        val rs = stmt.executeQuery()
                        if (rs.next()) {
                            loginSuccess = true
                            val userId = rs.getInt("id_usuario")
                            sharedPrefs.edit().apply {
                                putBoolean("isLoggedIn", true)
                                putString("username", user)
                                putInt("userId", userId)
                                apply()
                            }
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
