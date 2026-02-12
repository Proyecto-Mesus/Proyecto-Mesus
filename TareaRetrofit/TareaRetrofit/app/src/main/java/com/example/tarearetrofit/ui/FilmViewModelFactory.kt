package com.example.tarearetrofit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tarearetrofit.repository.CharacterProvider
import com.example.tarearetrofit.repository.FilmProvider

class FilmViewModelFactory(private val provider: FilmProvider) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(FilmViewModel::class.java))
        return FilmViewModel(provider) as T
    }
}