package com.example.tarearetrofit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tarearetrofit.repository.CharacterProvider

class CharacterViewModelFactory (private val provider: CharacterProvider) : ViewModelProvider.Factory
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(CharacterViewModel::class.java))
        return CharacterViewModel(provider) as T
    }
}