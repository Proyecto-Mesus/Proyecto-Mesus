package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import es.cifpcarlos3.proyecto_mesus_android.data.models.Carta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CollectionDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val dbHelper = DatabaseHelper()

    private val _cartas = MutableLiveData<List<Carta>>()
    val cartas: LiveData<List<Carta>> get() = _cartas

    fun getCartas(collectionId: Int) {
        viewModelScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                val lista = mutableListOf<Carta>()
                try {
                    val conn = dbHelper.getConnection()
                    conn?.use { c ->
                        val query = "SELECT id_carta, nombre, `set`, numero_set, imagen FROM cartas WHERE id_coleccion = ?"
                        val stmt = c.prepareStatement(query)
                        stmt.setInt(1, collectionId)
                        val rs = stmt.executeQuery()
                        while (rs.next()) {
                            lista.add(
                                Carta(
                                    idCarta = rs.getInt("id_carta"),
                                    nombre = rs.getString("nombre"),
                                    set = rs.getString("set"),
                                    numeroSet = rs.getString("numero_set"),
                                    imagen = rs.getString("imagen"),
                                    idColeccion = collectionId
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                lista
            }
            _cartas.value = resultado
        }
    }
}
