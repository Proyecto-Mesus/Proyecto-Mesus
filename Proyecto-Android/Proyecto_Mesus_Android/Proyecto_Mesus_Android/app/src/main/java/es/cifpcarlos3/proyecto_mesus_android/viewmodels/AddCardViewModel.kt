package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.db.DatabaseHelper
import es.cifpcarlos3.proyecto_mesus_android.data.utils.CloudinaryHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddCardViewModel : ViewModel() {
    private val dbHelper = DatabaseHelper()

    private val _uploadStatus = MutableLiveData<String?>()
    val uploadStatus: LiveData<String?> get() = _uploadStatus

    private val _isSaving = MutableLiveData<Boolean>()
    val isSaving: LiveData<Boolean> get() = _isSaving

    fun guardarCarta(nombre: String, set: String, numeroSet: String, imageFile: Any?, collectionId: Int) {
        if (nombre.isBlank()) {
            _uploadStatus.value = "El nombre no puede estar vacío"
            return
        }

        _isSaving.value = true
        
        if (imageFile != null) {
            _uploadStatus.value = "Subiendo imagen..."
            CloudinaryHelper.uploadImage(imageFile) { url ->
                if (url != null) {
                    insertarCarta(nombre, set, numeroSet, url, collectionId)
                } else {
                    _isSaving.value = false
                    _uploadStatus.value = "Error al subir la imagen"
                }
            }
        } else {
            insertarCarta(nombre, set, numeroSet, null, collectionId)
        }
    }

    private fun insertarCarta(nombre: String, set: String, numeroSet: String, imageUrl: String?, collectionId: Int) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val conn = dbHelper.getConnection()
                    conn?.use { connection ->
                        val query = "INSERT INTO cartas (nombre, `set`, numero_set, id_coleccion, imagen) VALUES (?, ?, ?, ?, ?)"
                        val stmt = connection.prepareStatement(query)
                        stmt.setString(1, nombre)
                        stmt.setString(2, set)
                        stmt.setString(3, numeroSet)
                        stmt.setInt(4, collectionId)
                        stmt.setString(5, imageUrl)
                        stmt.executeUpdate() > 0
                    } ?: false
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }

            _isSaving.value = false
            if (result) {
                _uploadStatus.value = "SUCCESS"
            } else {
                _uploadStatus.value = "Error al guardar en la base de datos"
            }
        }
    }
}
