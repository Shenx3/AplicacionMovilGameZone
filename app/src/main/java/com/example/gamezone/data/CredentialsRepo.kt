package com.example.gamezone.data

/**
 * Repositorio simple en memoria (singleton) para almacenar el último usuario registrado.
 * Simula una base de datos temporal para fines de demostración.
 */
object CredentialsRepo {

    // --- USUARIO DE PRUEBA INICIALIZADO ---
    // Inicialmente guardamos el usuario de prueba (sys/sys)
    private var savedUser: String? = "sys"
    private var savedPassword: String? = "sys"
    // ---------------------------------------

    /**
     * Guarda las credenciales del usuario después de un registro exitoso.
     * Esto SOBREESCRIBIRÁ el usuario 'sys' con el usuario registrado.
     */
    fun saveCredentials(user: String, password: String) {
        savedUser = user
        savedPassword = password
        println("✅ CREDENCIALES GUARDADAS: $savedUser / $savedPassword")
    }

    /**
     * Verifica si el usuario y la contraseña coinciden con los guardados.
     */
    fun checkCredentials(userAttempt: String, passwordAttempt: String): Boolean {
        // La comparación debe ser insensible a mayúsculas/minúsculas para el usuario/email
        val userMatch = savedUser?.equals(userAttempt, ignoreCase = true) ?: false
        val passwordMatch = savedPassword == passwordAttempt

        return userMatch && passwordMatch
    }

    fun getCurrentUser(): String? { // Devuelve el usuario actual
        // Devolvemos el identificador guardado (que es el email o 'sys')
        return savedUser
    }
}