package com.example.gamezone.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // <-- IMPORT NECESARIO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay // <-- IMPORT NECESARIO
import kotlinx.coroutines.launch // <-- IMPORT NECESARIO

/**
 * Representa el estado actual de la UI de Login.
 */
data class LoginState(
    val emailOrUsername: String = "",
    val password: String = "",
    val isLoading: Boolean = false, // <-- REINTRODUCIDO
    val loginError: String? = null
)

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    fun onEmailOrUsernameChange(v: String) {
        _state.update { it.copy(emailOrUsername = v, loginError = null) }
    }

    fun onPasswordChange(v: String) {
        _state.update { it.copy(password = v, loginError = null) }
    }

    /**
     * Lógica de inicio de sesión asíncrona con simulación de retardo.
     */
    fun login(onSuccess: () -> Unit) {
        val currentState = _state.value

        // Validación de campos vacíos
        if (currentState.emailOrUsername.isBlank() || currentState.password.isBlank()) {
            _state.update { it.copy(loginError = "Por favor, ingresa correo/usuario y contraseña.") }
            return
        }

        // Lanzar la operación de red en una corrutina
        viewModelScope.launch {
            // 1. Iniciar la carga
            _state.update { it.copy(isLoading = true, loginError = null) }

            // SIMULACIÓN DE RED
            delay(3000) // Retardo de 3 segundos

            // 2. Validación de credenciales
            val loginSuccessful = CredentialsRepo.checkCredentials(
                userAttempt = currentState.emailOrUsername,
                passwordAttempt = currentState.password
            )

            if (loginSuccessful) {
                // Éxito
                _state.update { it.copy(isLoading = false) }
                onSuccess()
            } else {
                // Error
                _state.update { it.copy(isLoading = false, loginError = "Credenciales incorrectas. El usuario no está registrado o la contraseña es inválida.") }
            }
        }
    }
}