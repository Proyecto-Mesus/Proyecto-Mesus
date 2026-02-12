package com.example.tarearetrofit.ui

import com.example.tarearetrofit.recyclerview.Character

sealed class CharacterUiState
{
    object Idle : CharacterUiState()
    object Loading : CharacterUiState()
    data class Success(val character : Character) : CharacterUiState()
    data class SuccessList(val characters : List<Character>) : CharacterUiState()
    data class Error(val message: String) : CharacterUiState()
}