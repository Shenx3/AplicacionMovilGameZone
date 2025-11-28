package com.example.gamezone.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.gamezone.SessionManager
import com.example.gamezone.network.LoginRequest
import com.example.gamezone.network.RetrofitClient
import com.example.gamezone.network.MessageResponse

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.google.gson.Gson

/**
 * Representa el estado actual de la UI de Login.
 */
data class LoginState(
    val emailOrUsername: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val loginError: String? = null
)

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    // Instancia del servicio API
    private val apiService = RetrofitClient.instance

    fun onEmailOrUsernameChange(v: String) {
        _state.update { it.copy(emailOrUsername = v, loginError = null) }
    }

    fun onPasswordChange(v: String) {
        _state.update { it.copy(password = v, loginError = null) }
    }

    /**
     * Lógica de inicio de sesión asíncrona con el Backend de Spring Boot.
     */
    fun login(onSuccess: () -> Unit) {
        val currentState = _state.value

        // Validación de campos vacíos
        if (currentState.emailOrUsername.isBlank() || currentState.password.isBlank()) {
            _state.update { it.copy(loginError = "Por favor, ingresa correo/usuario y contraseña.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, loginError = null) }
            delay(500)

            try {
                val request = LoginRequest(currentState.emailOrUsername, currentState.password)

                // 1. Llamada a la API de Login
                val response = apiService.login(request)

                // 2. Éxito: Guardar sesión y notificar
                SessionManager.currentUserId = response.id // Guardar ID retornado
                _state.update { it.copy(isLoading = false) }
                onSuccess()

            } catch (e: HttpException) {
                // Manejo de errores 4xx (ej. 401 Unauthorized)
                _state.update { it.copy(
                    isLoading = false,
                    loginError = "Credenciales incorrectas."
                ) }
            } catch (e: Exception) {
                // Manejo de otros errores (ej. red, servidor caído)
                _state.update { it.copy(
                    isLoading = false,
                    loginError = "Error de conexión con el servidor. Asegúrate de que el backend está corriendo en :8080."
                ) }
            }
        }
    }
}