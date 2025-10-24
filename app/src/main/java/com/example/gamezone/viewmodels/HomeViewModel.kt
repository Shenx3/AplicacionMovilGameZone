package com.example.gamezone.viewmodels

import androidx.lifecycle.ViewModel
import com.example.gamezone.data.FeaturedProducts
import com.example.gamezone.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.net.Uri
import android.content.Context
import androidx.core.net.toUri
import com.example.gamezone.data.AppDatabase
import com.example.gamezone.SessionManager
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// Data Class para exponer la información del usuario logueado
data class UserProfile(
    val username: String = "",
    val email: String = ""
)

class HomeViewModel : ViewModel() {
    // Los productos se importan automáticamente de data/Product.kt
    private val _products = MutableStateFlow(FeaturedProducts) // ERROR CORREGIDO AQUÍ
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    // Estado para el perfil del usuario
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Estado para la foto de perfil (se actualiza desde la DB)
    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri.asStateFlow()

    /**
     * Carga el perfil del usuario (incluyendo la foto) desde la base de datos.
     */
    fun loadProfile(context: Context) {
        viewModelScope.launch {
            val userId = SessionManager.currentUserId

            if (userId != null) {
                val userDao = AppDatabase.getDatabase(context).userDao()
                val user = userDao.getUserById(userId)

                if (user != null) {
                    _userProfile.value = UserProfile(
                        username = user.nombreUsuario,
                        email = user.email
                    )
                    // Convertir la String Uri de la DB a Uri para mostrar
                    _photoUri.value = user.profilePhotoUri?.toUri()
                }
            } else {
                // Si no hay sesión, usa el perfil por defecto
                _userProfile.value = UserProfile("Invitado", "N/A")
                _photoUri.value = null
            }
        }
    }

    /**
     * Guarda la Uri de la foto en la base de datos para el usuario logueado.
     */
    suspend fun setPhotoUri(context: Context, uri: Uri?) {
        val userId = SessionManager.currentUserId
        if (userId != null) {
            val userDao = AppDatabase.getDatabase(context).userDao()
            val user = userDao.getUserById(userId)

            if (user != null) {
                val updatedUser = user.copy(profilePhotoUri = uri?.toString())
                userDao.updateUser(updatedUser) // Guardar en DB
                _photoUri.value = uri // Actualizar en ViewModel
            }
        }
    }
}