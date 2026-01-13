package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewUserViewModel: ViewModel() {
    private val _inputTextUser = MutableLiveData<String>()

    private val _inputTextEmail = MutableLiveData<String>()

    private val _inputTextPasswd = MutableLiveData<String>()

    private val _inputTextConfirmPassword = MutableLiveData<String>()

    private val _isButtonEnabled = MutableLiveData<Boolean>()

    val isButtonEnabled: LiveData<Boolean> get()= _isButtonEnabled

    init{
        _isButtonEnabled.value = false
        _inputTextUser.value = ""
        _inputTextEmail.value = ""
        _inputTextPasswd.value = ""
        _inputTextConfirmPassword.value = ""
    }

    fun onTextChanged(value: String)
    {
        _inputTextUser.value = value
        _isButtonEnabled.value = validateInput()
    }

    fun onEmailTextChanged(value: String)
    {
        _inputTextEmail.value = value
        _isButtonEnabled.value = validateInput()
    }

    fun onPasswdTextChanged(value: String) {
        _inputTextPasswd.value = value
        _isButtonEnabled.value = validateInput()
    }

    fun onConfirmPasswdTextChanged(value: String) {
        _inputTextConfirmPassword.value = value
        _isButtonEnabled.value = validateInput()
    }

    private fun validateInput(): Boolean
    {
        val user = _inputTextUser.value ?: ""
        val email = _inputTextEmail.value ?: ""
        val passwd = _inputTextPasswd.value ?: ""
        val confirmPassword = _inputTextConfirmPassword.value ?: ""
        return user.isNotEmpty() && email.isNotEmpty() && passwd.length >= 4 && confirmPassword.length >= 4
    }

    private val _registerError = MutableLiveData<String?>()
    val registerError: LiveData<String?> get() = _registerError

    private val dbHelper = DatabaseHelper()

    fun performRegister() {
        val user = _inputTextUser.value ?: ""
        val email = _inputTextEmail.value ?: ""
        val passwd = _inputTextPasswd.value ?: ""
        val confirmPassword = _inputTextConfirmPassword.value ?: ""

        if (passwd != confirmPassword) {
            _registerError.value = "Las contraseñas no coinciden"
            return
        }

        viewModelScope.launch {
            var errorMessage: String? = "Error desconocido"
            try {
                withContext(Dispatchers.IO) {
                    val conn = dbHelper.getConnection()
                    
                    conn.use { connection ->
                        val consulta1 = "SELECT nombre_usuario, email FROM usuarios WHERE nombre_usuario = ? OR email = ?"
                        val stmt1 = connection.prepareStatement(consulta1)
                        stmt1.setString(1, user)
                        stmt1.setString(2, email)
                        val rs = stmt1.executeQuery()

                        if (rs.next()) {
                            val existingName = rs.getString("nombre_usuario")
                            if (existingName.equals(user)) {
                                errorMessage = "El nombre de usuario ya existe"
                            } else {
                                errorMessage = "El correo electrónico ya está registrado"
                            }
                        } else {
                            val consulta2 = "INSERT INTO usuarios (nombre_usuario, email, password) VALUES (?, ?, ?)"
                            val stmt2 = connection.prepareStatement(consulta2)
                            stmt2.setString(1, user)
                            stmt2.setString(2, email)
                            stmt2.setString(3, passwd)
                            
                            val rowsAffected = stmt2.executeUpdate()
                            if (rowsAffected > 0) {
                                errorMessage = null
                            }
                        }
                    }
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
                errorMessage = "Error: Driver de Base de Datos no encontrado"
            } catch (e: Throwable) {
                e.printStackTrace()
                errorMessage = "Fallo Conexión: ${e.message}"
            }

            _registerError.value = errorMessage
        }
    }
}
