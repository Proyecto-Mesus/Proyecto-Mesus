package com.example.tarearetrofit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tarearetrofit.repository.CharacterProvider
import com.example.tarearetrofit.repository.FilmProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FilmViewModel (private val provider: FilmProvider): ViewModel()
{
    //UIState
    private val _uiState = MutableStateFlow<FilmUiState>(FilmUiState.Idle)
    val uiState: StateFlow<FilmUiState> = _uiState

    fun loadFilm(id: Int)
    {
        viewModelScope.launch {

            _uiState.value = FilmUiState.Loading

            provider.getFilm(id)
                .onSuccess { film -> _uiState.value = FilmUiState.Success(film) }
                .onFailure { _ -> _uiState.value = FilmUiState.Error("Se produjo un error") }
        }
    }
    fun loadFilms(films: List<String>)
    {
        viewModelScope.launch {

            _uiState.value = FilmUiState.Loading

            provider.getFilms(films)
                .onSuccess { films -> _uiState.value = FilmUiState.SuccessList(films) }
                .onFailure { _ -> _uiState.value = FilmUiState.Error("Se produjo un error") }
        }
    }
}