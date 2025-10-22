package com.example.gamezone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gamezone.navigation.Route
import com.example.gamezone.ui.theme.GameZoneTheme
import com.example.gamezone.views.LoginView
import com.example.gamezone.views.RegisterView
import com.example.gamezone.views.WelcomeView
import com.example.gamezone.views.MenuShellView // <-- NUEVO IMPORT

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GameZoneTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Route.Welcome.route,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(Route.Welcome.route) {
                            WelcomeView(
                                onStartClick = { navController.navigate(Route.Login.route) }
                            )
                        }

                        // PANTALLA DE LOGIN
                        composable(Route.Login.route) {
                            LoginView(
                                onLoginSuccess = {
                                    // Navegamos a MenuShell y pasamos el argumento 'showSnackbar=true'
                                    navController.navigate(Route.HomeWithArgs.build(true)) { // <-- RUTA MODIFICADA
                                        popUpTo(Route.Login.route) { inclusive = true }
                                    }
                                },
                                onRegisterClick = { navController.navigate(Route.Register.route) }
                            )
                        }

                        // PANTALLA DE REGISTRO
                        composable(Route.Register.route) {
                            RegisterView(
                                onRegisterSuccess = {
                                    navController.navigate(Route.Login.route) {
                                        popUpTo(Route.Register.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // CONTENEDOR PRINCIPAL (MENU SHELL)
                        composable(
                            route = Route.HomeWithArgs.route, // <-- RUTA CON ARGUMENTOS
                            arguments = listOf(
                                navArgument("showSnackbar") {
                                    type = NavType.BoolType
                                    defaultValue = false
                                }
                            )
                        ) { backStackEntry ->
                            val showSnackbar = backStackEntry.arguments?.getBoolean("showSnackbar") ?: false

                            // MenuShellView contiene la navegación interna (Home y Camera)
                            MenuShellView(
                                showLoginSuccessSnackbar = showSnackbar
                            )
                        }
                    }
                }
            }
        }
    }
}