package com.example.gamezone.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamezone.data.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la UI para "Olvidé mi contraseña".
 */
data class ForgotState(
    val identifier: String = "",      // email o nombreUsuario
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null
)

class ForgotPasswordViewModel : ViewModel() {

    private val _state = MutableStateFlow(ForgotState())
    val state: StateFlow<ForgotState> = _state

    fun onIdentifierChange(v: String) = _state.update { it.copy(identifier = v, error = null, success = null) }
    fun onNewPasswordChange(v: String) = _state.update { it.copy(newPassword = v, error = null, success = null) }
    fun onConfirmChange(v: String) = _state.update { it.copy(confirmPassword = v, error = null, success = null) }

    /**
     * Valida y actualiza la contraseña del usuario en Room.
     * Busca por email O nombreUsuario (coincide con UserDao.findUserByIdentifier).
     */
    fun reset(context: Context, onSuccess: () -> Unit) {
        val s = _state.value

        // 1) Validaciones rápidas
        val pwdRegex = Regex(pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+\$).{8,}$") // 8+, mayús, minús y número
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

        // 2) Operación en DB
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, success = null) }

            val dao = AppDatabase.getDatabase(context).userDao()
            val user = dao.findUserByIdentifier(s.identifier) // busca por email o nombreUsuario

            if (user == null) {
                _state.update { it.copy(isLoading = false, error = "No existe un usuario con ese correo/usuario.") }
                return@launch
            }

            // Actualiza usando @Update (ya existe en tu UserDao)
            dao.updateUser(user.copy(contrasena = s.newPassword))

            _state.update { it.copy(isLoading = false, success = "Contraseña actualizada. Inicia sesión con tu nueva contraseña.") }
            onSuccess()
        }
    }
}
