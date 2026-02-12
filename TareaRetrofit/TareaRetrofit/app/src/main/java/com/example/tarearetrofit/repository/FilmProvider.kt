package com.example.tarearetrofit.repository

import android.net.Uri
import com.example.tarearetrofit.recyclerview.Film
import com.example.tarearetrofit.retrofit.StarWarsApi

class FilmProvider(private val api: StarWarsApi) {
    suspend fun getFilm(id: Int): Result<Film>
    {
        try
        {
            val film: Film = api.getFilm(id)
            return Result.success(film)
        }
        catch (e: Exception)
        {
            return Result.failure(e)
        }
    }
    suspend fun getFilms(films: List<String>): Result<List<Film>>
    {
        try
        {
            val filmsList = mutableListOf<Film>()
            for (film in films) {
                if (film.isNotEmpty()) {
                    val uri = Uri.parse(film)
                    val segments = uri.pathSegments
                    if (segments.isNotEmpty()) {
                        val idStr = segments.last()
                        try {
                            val id = idStr.toInt()
                            val filmData = api.getFilm(id)
                            filmsList.add(filmData)
                        } catch (e: NumberFormatException) {
                            return Result.failure(e)
                        }
                    }
                }
            }

            return Result.success(filmsList)
        }
        catch (e: Throwable)
        {
            return Result.failure(e)
        }
    }
}