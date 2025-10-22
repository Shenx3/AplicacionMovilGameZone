package com.example.gamezone.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gamezone.navigation.Route

@Composable
fun MenuShellView(
    showLoginSuccessSnackbar: Boolean
) {
    val innerNavController = rememberNavController()
    val currentRoute = currentInnerRoute(innerNavController)

    // Usaremos Scaffold para contener la Bottom Bar y el NavHost
    Scaffold(
        bottomBar = {
            NavigationBar {
                // Item Home
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Tienda") },
                    selected = currentRoute == Route.Home.route,
                    onClick = {
                        innerNavController.navigate(Route.Home.route) {
                            // Limpia el back stack si es necesario
                            popUpTo(innerNavController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
                // Item Cámara/Perfil
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    selected = currentRoute == Route.Camera.route,
                    onClick = {
                        innerNavController.navigate(Route.Camera.route) {
                            popUpTo(innerNavController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        // NavHost interno para las opciones del menú
        NavHost(
            navController = innerNavController,
            // Si el login es exitoso, navegamos al Home, si no, al Home directamente
            startDestination = Route.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Home.route) {
                // Pasamos el argumento del Snackbar a HomeView
                HomeView(showLoginSuccessSnackbar = showLoginSuccessSnackbar)
            }
            composable(Route.Camera.route) {
                CameraView()
            }
        }
    }

    // Lanzamos el efecto de Snackbar si se recibió el argumento.
    // Esto se hace aquí para que HomeView pueda ejecutar LaunchedEffect con showLoginSuccessSnackbar.
    LaunchedEffect(key1 = showLoginSuccessSnackbar) {
        if (showLoginSuccessSnackbar) {
            // Reiniciamos el argumento para que no se muestre al rotar/volver
            innerNavController.currentBackStackEntry?.arguments?.putBoolean("showSnackbar", false)
        }
    }
}

@Composable
private fun currentInnerRoute(navController: NavHostController): String? {
    val entry by navController.currentBackStackEntryAsState()
    return entry?.destination?.route
}
