package com.example.tarearetrofit.repository

import com.example.tarearetrofit.retrofit.StarWarsApi
import com.example.tarearetrofit.recyclerview.Character

class CharacterProvider(private val api: StarWarsApi)
{
    suspend fun getCharacter(id: Int): Result<Character>
    {
        try
        {
            val character: Character = api.getCharacter(id)
            return Result.success(character)
        }
        catch (e: Exception)
        {
            return Result.failure(e)
        }
    }
    suspend fun getCharacters(numeroInput: String): Result<List<Character>>
    {
        try
        {
            val numero = numeroInput.toInt()
            val characters = mutableListOf<Character>()
            for(id in 1..numero){
                val character: Character = api.getCharacter(id)
                characters.add(character)
            }
            return Result.success(characters)
        }
        catch (e: Exception)
        {
            return Result.failure(e)
        }
    }
}