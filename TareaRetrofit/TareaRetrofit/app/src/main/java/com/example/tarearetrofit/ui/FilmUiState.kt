package com.example.tarearetrofit.ui

import com.example.tarearetrofit.recyclerview.Character
import com.example.tarearetrofit.recyclerview.Film

sealed class FilmUiState {
    object Idle : FilmUiState()
    object Loading : FilmUiState()
    data class Success(val film : Film) : FilmUiState()
    data class SuccessList(val films : List<Film>) : FilmUiState()
    data class Error(val message: String) : FilmUiState()
}