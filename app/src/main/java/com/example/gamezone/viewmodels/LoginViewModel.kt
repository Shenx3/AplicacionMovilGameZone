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


// Representa el estado actual de la UI de Login.
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


    // Lógica de inicio de sesión asíncrona con el Backend de Spring Boot.

    fun login(onLoginSuccess: () -> Unit) {
        // 1. Validación de campos (asumida)

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, loginError = null) }

            // Obtener la instancia actual de la API aquí
            val apiService = RetrofitClient.instance


            val request = LoginRequest(
                identifier = state.value.emailOrUsername,
                contrasena = state.value.password
            )

            try {
                // 2. Usar la instancia obtenida
                val response = apiService.login(request)

                // Éxito:
                SessionManager.currentUserId = response.id
                _state.update { it.copy(isLoading = false) }
                onLoginSuccess()

            } catch (e: HttpException) {
                // Manejo de errores 4xx (asumido)
                if (e.code() == 401) {
                    _state.update {
                        it.copy(
                            loginError = "Credenciales inválidas. Intenta de nuevo.",
                            isLoading = false
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            loginError = "Error en el servidor: HTTP ${e.code()}.",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                // Manejo de errores de red (IP incorrecta, backend caído, etc.)
                val errorMessage = if (e is java.net.ConnectException) {
                    "No se pudo conectar al servidor. Verifica la IP: ${RetrofitClient.currentBaseUrl}"
                } else {
                    "Error de conexión: ${e.message}"
                }
                _state.update { it.copy(loginError = errorMessage, isLoading = false) }
            }
        }
    }
}