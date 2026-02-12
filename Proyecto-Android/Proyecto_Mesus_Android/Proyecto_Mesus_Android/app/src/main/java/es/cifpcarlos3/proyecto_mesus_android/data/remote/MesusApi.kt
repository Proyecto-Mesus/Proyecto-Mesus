package es.cifpcarlos3.proyecto_mesus_android.data.remote

import es.cifpcarlos3.proyecto_mesus_android.data.remote.dto.CartaDto
import es.cifpcarlos3.proyecto_mesus_android.data.remote.dto.ColeccionDto
import es.cifpcarlos3.proyecto_mesus_android.data.remote.dto.JuegoDto
import es.cifpcarlos3.proyecto_mesus_android.data.remote.dto.UsuarioDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MesusApi {

    @GET("api/juegos")
    suspend fun getJuegos(): List<JuegoDto>

    @GET("api/usuarios/buscar/{nombre}")
    suspend fun searchUsuarios(@Path("nombre") nombre: String): List<UsuarioDto>

    @POST("api/usuarios")
    suspend fun registerUsuario(@Body usuario: UsuarioDto): UsuarioDto

    @GET("api/usuarios/login/usuario/{login}/password/{password}")
    suspend fun login(@Path("login") login: String, @Path("password") password: String): UsuarioDto

    @GET("api/colecciones/usuario/{idUsuario}")
    suspend fun getColeccionesByUsuario(@Path("idUsuario") idUsuario: Int): List<ColeccionDto>

    @POST("api/colecciones")
    suspend fun createColeccion(@Body coleccion: ColeccionDto): ColeccionDto

    @PUT("api/colecciones/{id}")
    suspend fun updateColeccion(@Path("id") id: Int, @Body coleccion: ColeccionDto): ColeccionDto

    @GET("api/cartas/coleccion/{idColeccion}")
    suspend fun getCartasByColeccion(@Path("idColeccion") idColeccion: Int): List<CartaDto>

    @POST("api/cartas")
    suspend fun createCarta(@Body carta: CartaDto): CartaDto

    @PUT("api/cartas/{id}")
    suspend fun updateCarta(@Path("id") id: Int, @Body carta: CartaDto): CartaDto
}
