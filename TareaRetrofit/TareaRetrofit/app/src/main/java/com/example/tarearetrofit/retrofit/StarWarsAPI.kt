package com.example.tarearetrofit.retrofit

import retrofit2.http.GET
import retrofit2.http.Path
import com.example.tarearetrofit.recyclerview.Character
import com.example.tarearetrofit.recyclerview.Film


interface StarWarsApi {
    @GET("people/{id}")
    suspend fun getCharacter(@Path("id") id: Int): Character

    @GET("films/{id}")
    suspend fun getFilm(@Path("id") id: Int): Film
}
