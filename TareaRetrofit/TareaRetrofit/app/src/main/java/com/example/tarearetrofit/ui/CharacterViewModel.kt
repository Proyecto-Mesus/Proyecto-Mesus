package com.example.tarearetrofit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tarearetrofit.repository.CharacterProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.tarearetrofit.recyclerview.Character
import kotlinx.coroutines.launch

class CharacterViewModel(private val provider: CharacterProvider): ViewModel()
{
    //UIState
    private val _uiState = MutableStateFlow<CharacterUiState>(CharacterUiState.Idle)
    val uiState: StateFlow<CharacterUiState> = _uiState

    fun loadCharacter(id: Int)
    {
        viewModelScope.launch {

            _uiState.value = CharacterUiState.Loading

            provider.getCharacter(id)
                .onSuccess { character -> _uiState.value = CharacterUiState.Success(character) }
                .onFailure { _ -> _uiState.value = CharacterUiState.Error("Se produjo un error") }
        }
    }
    fun loadCharacters(numero: String)
    {
        viewModelScope.launch {

            _uiState.value = CharacterUiState.Loading

            provider.getCharacters(numero)
                .onSuccess { characters -> _uiState.value = CharacterUiState.SuccessList(characters) }
                .onFailure { _ -> _uiState.value = CharacterUiState.Error("Se produjo un error") }
        }
    }
}