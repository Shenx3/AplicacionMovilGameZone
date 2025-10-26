package com.example.gamezone.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import android.util.Patterns
import com.example.gamezone.data.User
import com.example.gamezone.data.AppDatabase
import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * Define el estado de los campos de entrada de la pantalla de Registro.
 */
data class RegisterState(
    // DATOS PERSONALES
    val nombreCompleto: String = "",
    val email: String = "",
    val telefono: String = "", // Campo opcional

    // CREDENCIALES
    val nombreUsuario: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",

    // PREFERENCIAS
    val generoFavorito: String = "", // Para el campo de RadioButton/Checkbox

    // TÉRMINOS
    val aceptaTerminos: Boolean = false,
    val isLoading: Boolean = false
)

/**
 * Define los posibles errores para cada campo del formulario.
 */
data class RegisterErrors(
    val nombreCompleto: String? = null,
    val email: String? = null,
    val nombreUsuario: String? = null,
    val contrasena: String? = null,
    val confirmarContrasena: String? = null,
    val generoFavorito: String? = null,
    val aceptaTerminos: String? = null,
    val generalError: String? = null
)

class RegisterViewModel : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state

    private val _errors = MutableStateFlow(RegisterErrors())
    val errors: StateFlow<RegisterErrors> = _errors

    val generosVideojuego = listOf("Acción", "Aventura", "RPG", "Shooter", "Deportes", "Estrategia")

    // --- MANEJADORES DE CAMBIOS (UPDATERS) ---
    fun onNombreChange(v: String) { _state.update { it.copy(nombreCompleto = v) } }
    fun onEmailChange(v: String) { _state.update { it.copy(email = v) } }
    fun onTelefonoChange(v: String) { _state.update { it.copy(telefono = v) } }
    fun onUsuarioChange(v: String) { _state.update { it.copy(nombreUsuario = v) } }
    fun onContrasenaChange(v: String) { _state.update { it.copy(contrasena = v) } }
    fun onConfirmarContrasenaChange(v: String) { _state.update { it.copy(confirmarContrasena = v) } }
    fun onGeneroChange(v: String) { _state.update { it.copy(generoFavorito = v) } }
    fun onTerminosChange(v: Boolean) { _state.update { it.copy(aceptaTerminos = v) } }

    /**
     * Valida todos los campos del formulario y actualiza los mensajes de error.
     * @return true si es válido, false si hay errores.
     */
    fun validate(): Boolean {
        val s = _state.value
        var currentErrors = RegisterErrors()

        // 1. Nombre Completo
        currentErrors = if (s.nombreCompleto.length < 3) {
            currentErrors.copy(nombreCompleto = "El nombre debe tener al menos 3 caracteres.")
        } else {
            currentErrors.copy(nombreCompleto = null)
        }

        // 2. Email
        currentErrors = when {
            s.email.isBlank() -> currentErrors.copy(email = "El correo electrónico es obligatorio.")
            !Patterns.EMAIL_ADDRESS.matcher(s.email).matches() -> currentErrors.copy(email = "Email inválido.")
            else -> currentErrors.copy(email = null)
        }

        // 3. Nombre de Usuario
        currentErrors = when {
            s.nombreUsuario.length !in 3..20 -> currentErrors.copy(nombreUsuario = "Debe tener entre 3 y 20 caracteres.")
            else -> currentErrors.copy(nombreUsuario = null)
        }

        // 4. Contraseña
        val contrasenaRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+\$).{8,}") // 8+ chars, mayúsculas, minúsculas, número
        currentErrors = when {
            s.contrasena.length < 8 -> currentErrors.copy(contrasena = "Mínimo 8 caracteres.")
            !s.contrasena.matches(contrasenaRegex) -> currentErrors.copy(contrasena = "Debe incluir mayúscula, minúscula y número.")
            else -> currentErrors.copy(contrasena = null)
        }

        // 5. Confirmar Contraseña
        currentErrors = when {
            s.confirmarContrasena.isBlank() -> currentErrors.copy(confirmarContrasena = "Confirma la contraseña.")
            s.contrasena != s.confirmarContrasena -> currentErrors.copy(confirmarContrasena = "Las contraseñas no coinciden.")
            else -> currentErrors.copy(confirmarContrasena = null)
        }

        // 6. Preferencia (Género)
        currentErrors = if (s.generoFavorito.isBlank()) {
            currentErrors.copy(generoFavorito = "Selecciona un género favorito.")
        } else {
            currentErrors.copy(generoFavorito = null)
        }

        // 7. Términos y Condiciones
        currentErrors = if (!s.aceptaTerminos) {
            currentErrors.copy(aceptaTerminos = "Debes aceptar los Términos y Política de Privacidad.")
        } else {
            currentErrors.copy(aceptaTerminos = null)
        }

        _errors.value = currentErrors
        // El formulario es válido si todos los errores son null
        return listOf(currentErrors.nombreCompleto, currentErrors.email, currentErrors.nombreUsuario,
            currentErrors.contrasena, currentErrors.confirmarContrasena,
            currentErrors.generoFavorito, currentErrors.aceptaTerminos).all { it == null }
    }


    fun register(context: Context, onSuccess: () -> Unit) {
        if (validate()) {
            // CORRECCIÓN APLICADA: Solo actualiza isLoading en _state.
            _state.update { it.copy(isLoading = true) }
            _errors.update { it.copy(generalError = null) } // Limpiar el error general anterior.

            viewModelScope.launch {
                val s = _state.value
                val userDao = AppDatabase.getDatabase(context).userDao()

                // Crear la nueva entidad User para la DB
                val newUser = User(
                    nombreCompleto = s.nombreCompleto,
                    email = s.email,
                    telefono = s.telefono.ifBlank { null },
                    nombreUsuario = s.nombreUsuario,
                    contrasena = s.contrasena,
                    generoFavorito = s.generoFavorito
                    // profilePhotoUri se deja como null por defecto
                )

                try {
                    // 1. Verificar si el email o nombre de usuario ya existen (mejorar la UX)
                    val existingUser = userDao.findUserByIdentifier(s.email)
                    if (existingUser != null) {
                        _errors.update { it.copy(generalError = "El email ya está registrado.") }
                        _state.update { it.copy(isLoading = false) }
                        return@launch
                    }

                    // 2. Insertar en la DB
                    userDao.insertUser(newUser)

                    // 3. Éxito
                    onSuccess()
                    reset() // Limpia el formulario después del registro
                } catch (e: Exception) {
                    _errors.update { it.copy(generalError = "Error de registro: ${e.message}") }
                }

                _state.update { it.copy(isLoading = false) }
            }
        } else {
            _errors.update { it.copy(generalError = "Revisa los campos con errores.") }
        }
    }

    fun reset() {
        _state.value = RegisterState()
        _errors.value = RegisterErrors()
    }
}