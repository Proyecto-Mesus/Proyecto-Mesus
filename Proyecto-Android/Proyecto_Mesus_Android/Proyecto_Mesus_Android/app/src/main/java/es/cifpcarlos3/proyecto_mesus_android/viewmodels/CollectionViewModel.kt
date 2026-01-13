package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import es.cifpcarlos3.proyecto_mesus_android.data.models.Coleccion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CollectionViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPrefs = application.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val dbHelper = DatabaseHelper()

    private val _colecciones = MutableLiveData<List<Coleccion>>()
    val colecciones: LiveData<List<Coleccion>> get() = _colecciones

    fun getColecciones(targetUserId: Int = -1) {
        val currentUserId = sharedPrefs.getInt("userId", -1)
        if (currentUserId == -1) return

        val userIdToQuery = if (targetUserId != -1) targetUserId else currentUserId
        val isOwnProfile = (userIdToQuery == currentUserId)

        viewModelScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                val lista = mutableListOf<Coleccion>()
                try {
                    val conn = dbHelper.getConnection()
                    conn?.use { c ->
                        val queryBuilder = StringBuilder("SELECT id_coleccion, nombre, id_juego, publica FROM colecciones WHERE id_usuario = ?")
                        if (!isOwnProfile) {
                            queryBuilder.append(" AND publica = 1")
                        }
                        
                        val stmt = c.prepareStatement(queryBuilder.toString())
                        stmt.setInt(1, userIdToQuery)
                        val rs = stmt.executeQuery()
                        while (rs.next()) {
                            lista.add(
                                Coleccion(
                                    idColeccion = rs.getInt("id_coleccion"),
                                    nombre = rs.getString("nombre"),
                                    idUsuario = userIdToQuery,
                                    idJuego = rs.getInt("id_juego"),
                                    publica = rs.getInt("publica")
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                lista
            }
            _colecciones.value = resultado
        }
    }
}
