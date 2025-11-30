package com.example.gamezone.viewmodels

import androidx.lifecycle.ViewModel
import com.example.gamezone.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.net.Uri
import android.content.Context
import androidx.core.net.toUri
import com.example.gamezone.SessionManager
import com.example.gamezone.network.RetrofitClient
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.util.Log

// Data Class para exponer la información del usuario logueado
data class UserProfile(
    val username: String = "",
    val email: String = ""
)

class HomeViewModel : ViewModel() {

    private val apiService = RetrofitClient.instance

    // 1. Productos: Se inicializa vacío y se llena con la API
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    // Estado para el perfil del usuario (se mantiene)
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Estado para la foto de perfil (se mantiene)
    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri.asStateFlow()

    init {
        // Cargar productos al inicializar el ViewModel (desde el backend)
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                // Llama al endpoint GET /api/products
                val productList = apiService.getProducts()
                _products.value = productList
            } catch (e: Exception) {
                // error específico en Logcat
                Log.e("HomeViewModel", "Error al cargar productos: ${e.message}", e)
                _products.value = emptyList()
            }
        }
    }


    // Carga el perfil del usuario (incluyendo la foto) desde el Backend.

    fun loadProfile(context: Context) {
        viewModelScope.launch {
            val userId = SessionManager.currentUserId

            if (userId != null) {
                try {
                    // 1. Llama al endpoint GET /api/users/{userId}
                    val user = apiService.getUserProfile(userId)

                    _userProfile.value = UserProfile(
                        username = user.nombreUsuario,
                        email = user.email
                    )
                    // Convertir la String Uri del Backend a Uri para mostrar
                    _photoUri.value = user.profilePhotoUri?.toUri()
                } catch (e: Exception) {
                    // error específico en Logcat
                    Log.e("HomeViewModel", "Error al cargar perfil del usuario $userId: ${e.message}", e)
                    _userProfile.value = UserProfile("Error", "Error al cargar perfil (Revisa el Backend)")
                    _photoUri.value = null
                }
            } else {
                _userProfile.value = UserProfile("Invitado", "N/A")
                _photoUri.value = null
            }
        }
    }


    // Guarda la Uri de la foto enviándola al backend.

    suspend fun setPhotoUri(context: Context, uri: Uri?) {
        val userId = SessionManager.currentUserId
        if (userId != null) {
            val uriString = uri?.toString() ?: ""
            try {
                // Llama al endpoint PUT /api/users/{userId}/photo
                apiService.updateProfilePhoto(userId, mapOf("profilePhotoUri" to uriString))
                _photoUri.value = uri // Actualizar en ViewModel local
            } catch (e: Exception) {
                // Manejo de errores de red o backend
                Log.e("HomeViewModel", "Error al actualizar foto de perfil: ${e.message}", e)
            }
        }
    }


     // Cierra la sesión del usuario.

    fun logout() {
        SessionManager.currentUserId = null // Limpiar el ID de la sesión
    }
}