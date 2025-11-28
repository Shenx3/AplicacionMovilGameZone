package com.example.gamezone.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import android.util.Patterns


import com.example.gamezone.network.RetrofitClient
import com.example.gamezone.network.RegisterRequest
import com.example.gamezone.network.MessageResponse

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.google.gson.Gson
import retrofit2.HttpException

/**
 * Define el estado de los campos de entrada de la pantalla de Registro.
 */
data class RegisterState(
    val nombreCompleto: String = "",
    val email: String = "",
    val telefono: String = "",
    val nombreUsuario: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val generoFavorito: String = "",
    val aceptaTerminos: Boolean = false,
    val isLoading: Boolean = false
)

/**
 * Define los posibles errores para cada campo del formulario. (Se mantiene)
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

    // Instancia del servicio API
    private val apiService = RetrofitClient.instance

    val generosVideojuego = listOf("Acción", "Aventura", "RPG", "Shooter", "Deportes", "Estrategia")

    // MANEJADORES DE CAMBIOS (UPDATERS) y VALIDATE()

    fun onNombreChange(v: String) { _state.update { it.copy(nombreCompleto = v) } }
    fun onEmailChange(v: String) { _state.update { it.copy(email = v) } }
    fun onTelefonoChange(v: String) { _state.update { it.copy(telefono = v) } }
    fun onUsuarioChange(v: String) { _state.update { it.copy(nombreUsuario = v) } }
    fun onContrasenaChange(v: String) { _state.update { it.copy(contrasena = v) } }
    fun onConfirmarContrasenaChange(v: String) { _state.update { it.copy(confirmarContrasena = v) } }
    fun onGeneroChange(v: String) { _state.update { it.copy(generoFavorito = v) } }
    fun onTerminosChange(v: Boolean) { _state.update { it.copy(aceptaTerminos = v) } }

    /**
     * Valida todos los campos del formulario. (Lógica se mantiene)
     */
    fun validate(): Boolean {
        val s = _state.value
        var currentErrors = RegisterErrors()

        val contrasenaRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+\$).{8,}")

        // Simplemente asegurando que las validaciones estén completas para el scope del ViewModel
        currentErrors = if (s.nombreCompleto.length < 3) currentErrors.copy(nombreCompleto = "El nombre debe tener al menos 3 caracteres.") else currentErrors.copy(nombreCompleto = null)
        currentErrors = when { s.email.isBlank() -> currentErrors.copy(email = "El correo electrónico es obligatorio.")
            !Patterns.EMAIL_ADDRESS.matcher(s.email).matches() -> currentErrors.copy(email = "Email inválido.")
            else -> currentErrors.copy(email = null) }
        currentErrors = when { s.nombreUsuario.length !in 3..20 -> currentErrors.copy(nombreUsuario = "Debe tener entre 3 y 20 caracteres.")
            else -> currentErrors.copy(nombreUsuario = null) }
        currentErrors = when { s.contrasena.length < 8 -> currentErrors.copy(contrasena = "Mínimo 8 caracteres.")
            !s.contrasena.matches(contrasenaRegex) -> currentErrors.copy(contrasena = "Debe incluir mayúscula, minúscula y número.")
            else -> currentErrors.copy(contrasena = null) }
        currentErrors = when { s.confirmarContrasena.isBlank() -> currentErrors.copy(confirmarContrasena = "Confirma la contraseña.")
            s.contrasena != s.confirmarContrasena -> currentErrors.copy(confirmarContrasena = "Las contraseñas no coinciden.")
            else -> currentErrors.copy(confirmarContrasena = null) }
        currentErrors = if (s.generoFavorito.isBlank()) currentErrors.copy(generoFavorito = "Selecciona un género favorito.") else currentErrors.copy(generoFavorito = null)
        currentErrors = if (!s.aceptaTerminos) currentErrors.copy(aceptaTerminos = "Debes aceptar los Términos y Política de Privacidad.") else currentErrors.copy(aceptaTerminos = null)

        _errors.value = currentErrors
        return listOf(currentErrors.nombreCompleto, currentErrors.email, currentErrors.nombreUsuario,
            currentErrors.contrasena, currentErrors.confirmarContrasena,
            currentErrors.generoFavorito, currentErrors.aceptaTerminos).all { it == null }
    }


    /**
     * Envía los datos de registro al backend de Spring Boot.
     */
    fun register(onSuccess: () -> Unit) {
        if (validate()) {
            _state.update { it.copy(isLoading = true) }
            _errors.update { it.copy(generalError = null) }

            viewModelScope.launch {
                delay(1000)

                val s = _state.value
                val request = RegisterRequest(
                    nombreCompleto = s.nombreCompleto,
                    email = s.email,
                    telefono = s.telefono.ifBlank { null },
                    nombreUsuario = s.nombreUsuario,
                    contrasena = s.contrasena,
                    generoFavorito = s.generoFavorito
                )

                try {
                    // 1. Llamada a la API de Registro
                    val response = apiService.register(request)

                    if (response.isSuccessful) {
                        // 2. Éxito
                        onSuccess()
                        reset()
                    } else {
                        // 3. Manejo de error HTTP (ej. 400 Bad Request por email/usuario ya existente)
                        val errorBody = response.errorBody()?.string()
                        val errorMsg = try {
                            // Intenta parsear la respuesta del backend para el mensaje de error
                            Gson().fromJson(errorBody, MessageResponse::class.java).message
                        } catch (e: Exception) {
                            "Error desconocido. Revisa el email y usuario."
                        }
                        _errors.update { it.copy(generalError = errorMsg) }
                    }
                } catch (e: Exception) {
                    _errors.update { it.copy(generalError = "Error de conexión con el servidor: ${e.message}") }
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