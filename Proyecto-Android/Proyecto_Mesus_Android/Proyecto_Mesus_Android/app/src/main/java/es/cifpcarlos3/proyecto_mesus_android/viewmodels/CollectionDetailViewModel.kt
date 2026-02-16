package es.cifpcarlos3.proyecto_mesus_android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.cifpcarlos3.proyecto_mesus_android.data.models.Carta
import es.cifpcarlos3.proyecto_mesus_android.data.remote.RetrofitInstance
import es.cifpcarlos3.proyecto_mesus_android.data.remote.dto.CartaDto
import es.cifpcarlos3.proyecto_mesus_android.data.remote.dto.toDomain
import es.cifpcarlos3.proyecto_mesus_android.data.repository.CartaProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CartaUiState {
    object Idle : CartaUiState()
    object Loading : CartaUiState()
    data class Success(val list: List<Carta>, val ownerId: Int = -1) : CartaUiState()
    object ActionSuccess : CartaUiState()
    data class Error(val message: String) : CartaUiState()
}

class CollectionDetailViewModel : ViewModel() {
    private val provider = CartaProvider(RetrofitInstance.api)
    private val _uiState = MutableStateFlow<CartaUiState>(CartaUiState.Idle)
    val uiState: StateFlow<CartaUiState> = _uiState

    private val _isGridView = MutableStateFlow(false)
    val isGridView: StateFlow<Boolean> = _isGridView

    fun toggleViewMode() {
        _isGridView.value = !_isGridView.value
    }

    fun getCartas(idColeccion: Int) {
        _uiState.value = CartaUiState.Loading
        viewModelScope.launch {
            val response = RetrofitInstance.api.getCartasByColeccion(idColeccion)
            try {
                val cards = response.map { it.toDomain() }
                val ownerId = response.firstOrNull()?.coleccion?.usuario?.id ?: -1
                _uiState.value = CartaUiState.Success(cards, ownerId)
            } catch (e: Exception) {
                _uiState.value = CartaUiState.Error(e.message ?: "LOAD_FAILED")
            }
        }
    }

    fun deleteCard(cardId: Int, idColeccion: Int) {
        _uiState.value = CartaUiState.Loading
        viewModelScope.launch {
            val result = provider.deleteCarta(cardId)
            result.onSuccess {
                _uiState.value = CartaUiState.ActionSuccess
                getCartas(idColeccion)
            }.onFailure {
                _uiState.value = CartaUiState.Error(it.message ?: "DELETE_FAILED")
            }
        }
    }
}
