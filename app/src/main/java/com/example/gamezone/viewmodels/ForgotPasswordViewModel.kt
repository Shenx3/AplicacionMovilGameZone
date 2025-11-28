package com.example.gamezone.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.gamezone.network.RetrofitClient
import com.example.gamezone.network.PasswordResetRequest
import com.example.gamezone.network.MessageResponse

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.gson.Gson

/**
 * Estado de la UI para "Olvidé mi contraseña".
 */
data class ForgotState(
    val identifier: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null
)

class ForgotPasswordViewModel : ViewModel() {

    private val _state = MutableStateFlow(ForgotState())
    val state: StateFlow<ForgotState> = _state

    // Instancia del servicio API
    private val apiService = RetrofitClient.instance

    fun onIdentifierChange(v: String) = _state.update { it.copy(identifier = v, error = null, success = null) }
    fun onNewPasswordChange(v: String) = _state.update { it.copy(newPassword = v, error = null, success = null) }
    fun onConfirmChange(v: String) = _state.update { it.copy(confirmPassword = v, error = null, success = null) }

    /**
     * Valida y actualiza la contraseña del usuario en el Backend.
     */
    fun reset(context: Context, onSuccess: () -> Unit) { // <-- Se mantiene Context pero ya no se usa para DB
        val s = _state.value

        // 1) Validaciones rápidas (se mantienen)
        val pwdRegex = Regex(pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+\$).{8,}$")
        when {
            s.identifier.isBlank() -> {
                _state.update { it.copy(error = "Ingresa tu correo o usuario.") }; return
            }
            s.newPassword.isBlank() -> {
                _state.update { it.copy(error = "Ingresa la nueva contraseña.") }; return
            }
            !s.newPassword.matches(pwdRegex) -> {
                _state.update { it.copy(error = "Debe tener 8+ caracteres, mayúscula, minúscula y número.") }; return
            }
            s.confirmPassword != s.newPassword -> {
                _state.update { it.copy(error = "Las contraseñas no coinciden.") }; return
            }
        }

        // 2) Operación en el Backend
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, success = null) }

            val request = PasswordResetRequest(s.identifier, s.newPassword)

            try {
                // Llamada a la API (PUT /api/users/password)
                val response = apiService.resetPassword(request)

                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "Contraseña actualizada."
                    _state.update { it.copy(isLoading = false, success = message) }
                    onSuccess()
                } else {
                    // Manejo de errores HTTP (ej. 404 Not Found)
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = try {
                        Gson().fromJson(errorBody, MessageResponse::class.java).message
                    } catch (e: Exception) {
                        "Usuario no encontrado o error del servidor."
                    }
                    _state.update { it.copy(isLoading = false, error = errorMsg) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Error de conexión con el servidor.") }
            }
        }
    }
}