package com.example.gamezone.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.gamezone.data.AppDatabase // Importar DB
import com.example.gamezone.SessionManager // Importar SessionManager
import android.content.Context // Necesario para acceder a la base de datos
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    fun onEmailOrUsernameChange(v: String) {
        _state.update { it.copy(emailOrUsername = v, loginError = null) }
    }

    fun onPasswordChange(v: String) {
        _state.update { it.copy(password = v, loginError = null) }
    }

    /**
     * Lógica de inicio de sesión asíncrona con Room.
     */
    fun login(context: Context, onSuccess: () -> Unit) {
        val currentState = _state.value

        // Validación de campos vacíos
        if (currentState.emailOrUsername.isBlank() || currentState.password.isBlank()) {
            _state.update { it.copy(loginError = "Por favor, ingresa correo/usuario y contraseña.") }
            return
        }

        // Lanzar la operación de base de datos en una corrutina
        viewModelScope.launch {
            // 1. Iniciar la carga
            _state.update { it.copy(isLoading = true, loginError = null) }
            delay(3000) // Simulación de retardo de red/DB

            val userDao = AppDatabase.getDatabase(context).userDao()
            val identifier = currentState.emailOrUsername

            // 2. Buscar usuario por email o nombre de usuario
            val user = userDao.findUserByIdentifier(identifier)

            if (user == null) {
                // Usuario no encontrado
                _state.update { it.copy(isLoading = false, loginError = "Credenciales incorrectas. Usuario no registrado.") }
                return@launch
            }

            // 3. Verificar contraseña
            if (user.contrasena == currentState.password) {
                // Éxito: Guardar sesión y notificar
                SessionManager.currentUserId = user.id // Guardar ID de sesión
                _state.update { it.copy(isLoading = false) }
                onSuccess()
            } else {
                // Error: Contraseña inválida
                _state.update { it.copy(isLoading = false, loginError = "Contraseña incorrecta.") }
            }
        }
    }
}