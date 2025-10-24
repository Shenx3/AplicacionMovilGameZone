package com.example.gamezone.navigation

/**
 * Define las rutas de navegación para toda la aplicación.
 */
sealed class Route(val route: String) {
    data object Welcome : Route("welcome") // Vista de bienvenida
    data object Login : Route("login") // Vista de inicio de sesión
    data object Register : Route("register") // Vista de registro

    // Contenedor principal después del login (que tendrá el menú interno Home/Camera)
    data object MenuShell : Route("menu_shell")

    // Rutas internas de MenuShell
    data object Home : Route("home") // El Home actual
    data object Camera : Route("camera") // La nueva vista de Cámara

    // HOME ahora acepta un argumento opcional: showSnackbar
    data object HomeWithArgs : Route("menu_shell?showSnackbar={showSnackbar}") {
        fun build(showSnackbar: Boolean = false) = "menu_shell?showSnackbar=$showSnackbar"
    }
}