package com.example.gamezone.viewmodels

import androidx.lifecycle.ViewModel
import com.example.gamezone.data.CredentialsRepo
import com.example.gamezone.data.FeaturedProducts
import com.example.gamezone.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.net.Uri // <<< NUEVO IMPORT
import com.example.gamezone.data.PhotoPrefsRepo // <<< NUEVO IMPORT
import kotlinx.coroutines.flow.first // <<< NUEVO IMPORT
import android.content.Context // <<< NUEVO IMPORT
import androidx.core.net.toUri // <<< NUEVO IMPORT

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

    // Estado para la foto de perfil
    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri.asStateFlow()

    /**
     * Guarda el Uri en el ViewModel y en DataStore (persistencia).
     */
    suspend fun setPhotoUri(context: Context, uri: Uri?) {
        val uriString = uri?.toString()
        PhotoPrefsRepo.setPhotoUriString(context, uriString) // GUARDAR EN DISCO
        _photoUri.value = uri
    }

    /**
     * Carga el Uri desde DataStore si no está en memoria.
     */
    suspend fun loadPhotoUri(context: Context) {
        if (_photoUri.value == null) {
            val uriString = PhotoPrefsRepo.photoUriStringFlow(context).first()
            _photoUri.value = uriString?.toUri()
        }
    }

    private fun loadProfile(): UserProfile {
        // En una aplicación real, se cargaría el perfil completo desde la red o Room.
        // Aquí usamos el CredentialsRepo para simular el usuario logueado.
        val user = CredentialsRepo.getCurrentUser() ?: "Usuario Desconocido"
        val email = if (user.contains("@")) user else "N/A"
        val username = if (user.contains("@")) "Usuario" else user

        return UserProfile(username, email)
    }
}