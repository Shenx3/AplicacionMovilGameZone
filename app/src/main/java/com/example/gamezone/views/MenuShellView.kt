package com.example.gamezone.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gamezone.navigation.Route
import com.example.gamezone.viewmodels.CartViewModel

@Composable
fun MenuShellView(
    showLoginSuccessSnackbar: Boolean,
    onLogout: () -> Unit
) {
    val innerNavController = rememberNavController()
    val currentRoute = currentInnerRoute(innerNavController)

    // 1. INSTANCIA COMPARTIDA: Se crea el ViewModel aquí.
    val cartVm: CartViewModel = viewModel()

    // Usaremos Scaffold para contener la Bottom Bar y el NavHost
    Scaffold(
        bottomBar = {
            NavigationBar {
                // Item Home (Tienda)
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Tienda") },
                    label = { Text("Tienda") },
                    selected = currentRoute == Route.Home.route,
                    onClick = {
                        innerNavController.navigate(Route.Home.route) {
                            popUpTo(innerNavController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )

                // Item Carrito
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito") },
                    label = { Text("Carrito") },
                    selected = currentRoute == Route.Cart.route,
                    onClick = {
                        innerNavController.navigate(Route.Cart.route) {
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
            startDestination = Route.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Home.route) {
                // 2. Pasar la instancia compartida a HomeView
                HomeView(
                    showLoginSuccessSnackbar = showLoginSuccessSnackbar,
                    cartVm = cartVm
                )
            }
            composable(Route.Cart.route) {
                // 3. Pasar la instancia compartida a CartView
                CartView(vm = cartVm)
            }
            composable(Route.Camera.route) {
                CameraView(onLogout = onLogout)
            }
        }
    }

    // Lanzamos el efecto de Snackbar si se recibió el argumento.
    LaunchedEffect(key1 = showLoginSuccessSnackbar) {
        if (showLoginSuccessSnackbar) {
            innerNavController.currentBackStackEntry?.arguments?.putBoolean("showSnackbar", false)
        }
    }
}

@Composable
private fun currentInnerRoute(navController: NavHostController): String? {
    val entry by navController.currentBackStackEntryAsState()
    return entry?.destination?.route
}