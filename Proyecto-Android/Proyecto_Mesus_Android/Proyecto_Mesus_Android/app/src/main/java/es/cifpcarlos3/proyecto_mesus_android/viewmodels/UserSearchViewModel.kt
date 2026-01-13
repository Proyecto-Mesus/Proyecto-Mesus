package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import es.cifpcarlos3.proyecto_mesus_android.data.models.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserSearchViewModel(application: Application) : AndroidViewModel(application) {
    private val dbHelper = DatabaseHelper()

    private val _users = MutableLiveData<List<Usuario>>()
    val users: LiveData<List<Usuario>> get() = _users

    fun searchUsers(query: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                val list = mutableListOf<Usuario>()
                try {
                    val conn = dbHelper.getConnection()
                    conn?.use { c ->
                        val sql = "SELECT id_usuario, nombre_usuario FROM usuarios WHERE nombre_usuario LIKE ?"
                        val stmt = c.prepareStatement(sql)
                        stmt.setString(1, "%$query%")
                        val rs = stmt.executeQuery()
                        while (rs.next()) {
                            list.add(
                                Usuario(
                                    idUsuario = rs.getInt("id_usuario"),
                                    nombre = rs.getString("nombre_usuario")
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                list
            }
            _users.value = result
        }
    }
}
