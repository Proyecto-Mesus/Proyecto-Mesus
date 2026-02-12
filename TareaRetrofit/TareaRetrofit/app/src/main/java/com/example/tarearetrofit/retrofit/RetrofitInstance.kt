package com.example.tarearetrofit.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/*  En Kotlin, object se utiliza para declarar un singleton, es decir,
    una única instancia de esta clase que se crea
    y se utiliza en toda la aplicación.
*/
object RetrofitInstance
{
    private const val BASE_URL = "https://swapi.py4e.com/api/"

    /*  Se inicializa de manera perezosa (lazy),
        lo que significa que su valor se calculará la primera vez
        que se acceda a ella,y no antes.*/

    val api: StarWarsApi by lazy {

        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StarWarsApi::class.java)

    }
}
