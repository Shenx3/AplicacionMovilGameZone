package com.example.gamezone.viewmodels

import androidx.lifecycle.ViewModel
import com.example.gamezone.data.CredentialsRepo
import com.example.gamezone.data.FeaturedProducts
import com.example.gamezone.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Nuevo Data Class para exponer la información del usuario logueado
data class UserProfile(
    val username: String = "",
    val email: String = ""
)

class HomeViewModel : ViewModel() {
    private val _products = MutableStateFlow(FeaturedProducts)
    val products: StateFlow<List<Product>> = _products

    // Estado para el perfil del usuario (tomado del CredentialsRepo)
    private val _userProfile = MutableStateFlow(loadProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private fun loadProfile(): UserProfile {
        // En una aplicación real, se cargaría el perfil completo desde la red o Room.
        // Aquí usamos el CredentialsRepo para simular el usuario logueado.
        val user = CredentialsRepo.getCurrentUser() ?: "Usuario Desconocido"
        val email = if (user.contains("@")) user else "N/A"
        val username = if (user.contains("@")) "Usuario" else user

        return UserProfile(username, email)
    }
}