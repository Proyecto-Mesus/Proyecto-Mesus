package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import es.cifpcarlos3.proyecto_mesus_android.data.models.Juego

class AddCollectionViewModel(application: Application) : AndroidViewModel(application) {
    private val dbHelper = DatabaseHelper()
    private val sharedPrefs = application.getSharedPreferences("user_session", android.content.Context.MODE_PRIVATE)

    private val _saveStatus = MutableLiveData<String?>()
    val saveStatus: LiveData<String?> get() = _saveStatus

    private val _isSaving = MutableLiveData<Boolean>()
    val isSaving: LiveData<Boolean> get() = _isSaving

    private val _juegos = MutableLiveData<List<Juego>>()
    val juegos: LiveData<List<Juego>> get() = _juegos

    fun fetchJuegos() {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                val resultList = mutableListOf<Juego>()
                try {
                    val conn = dbHelper.getConnection()
                    conn?.use { connection ->
                        val query = "SELECT id_juego, nombre FROM juegos"
                        val stmt = connection.prepareStatement(query)
                        val rs = stmt.executeQuery()
                        while (rs.next()) {
                            resultList.add(Juego(rs.getInt("id_juego"), rs.getString("nombre")))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                resultList
            }
            _juegos.value = list
        }
    }

    fun guardarColeccion(nombre: String, idJuego: Int, isPublic: Boolean) {
        if (nombre.isBlank()) {
            _saveStatus.value = "El nombre no puede estar vacío"
            return
        }

        val userId = sharedPrefs.getInt("userId", -1)
        if (userId == -1) {
            _saveStatus.value = "Error de sesión: Usuario no encontrado"
            return
        }

        _isSaving.value = true
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val conn = dbHelper.getConnection()
                    conn?.use { connection ->
                        val query = "INSERT INTO colecciones (nombre, id_usuario, id_juego, publica) VALUES (?, ?, ?, ?)"
                        val stmt = connection.prepareStatement(query)
                        stmt.setString(1, nombre)
                        stmt.setInt(2, userId)
                        stmt.setInt(3, idJuego)
                        stmt.setInt(4, if (isPublic) 1 else 0)
                        stmt.executeUpdate() > 0
                    } ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                _saveStatus.postValue("Error: ${e.message}")
                false
            }
            }

            _isSaving.value = false
            if (result) {
                _saveStatus.value = "SUCCESS"
            } else {
                _saveStatus.value = "Error al conectar o guardar la colección"
            }
        }
    }
}
