package com.example.gamezone.network

import com.example.gamezone.data.Product
import com.example.gamezone.data.User
import retrofit2.Response
import retrofit2.http.*

// DTOs del lado del móvil
data class LoginRequest(val identifier: String, val contrasena: String)
data class LoginResponse(val id: Long, val nombreUsuario: String)
data class RegisterRequest(
    val nombreCompleto: String, val email: String, val telefono: String?,
    val nombreUsuario: String, val contrasena: String, val generoFavorito: String
)
data class MessageResponse(val message: String)
data class PasswordResetRequest(val identifier: String, val newPassword: String)

interface GameZoneApiService {

    // RUTAS DE AUTENTICACIÓN
    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/users/register")
    suspend fun register(@Body request: RegisterRequest): Response<MessageResponse>

    @PUT("api/users/password")
    suspend fun resetPassword(@Body request: PasswordResetRequest): Response<MessageResponse>

    // RUTAS DE PRODUCTO
    @GET("api/products")
    suspend fun getProducts(): List<Product>

    // RUTAS DE PERFIL
    @GET("api/users/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: Long): User

    @PUT("api/users/{userId}/photo")
    suspend fun updateProfilePhoto(@Path("userId") userId: Long, @Body photoData: Map<String, String>): Response<MessageResponse>
}