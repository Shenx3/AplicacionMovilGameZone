package com.example.gamezone.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamezone.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(
    vm: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val state = vm.state.collectAsState().value
    var passwordVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .imePadding()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                text = "Ingresa con tu correo o usuario y contraseña.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 24.dp)
            )

            // CAMPO CORREO O USUARIO
            OutlinedTextField(
                value = state.emailOrUsername,
                onValueChange = vm::onEmailOrUsernameChange,
                label = { Text("Correo o usuario") },
                placeholder = { Text("correo@dominio.com o gamer_pro") },
                enabled = !state.isLoading, // DESHABILITADO durante la carga
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // CAMPO CONTRASEÑA
            OutlinedTextField(
                value = state.password,
                onValueChange = vm::onPasswordChange,
                label = { Text("Contraseña") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    IconButton(onClick = { passwordVisible = !passwordVisible }, enabled = !state.isLoading) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                enabled = !state.isLoading, // DESHABILITADO durante la carga
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // MOSTRAR ERRORES DEL VIEWMODEL
            if (state.loginError != null) {
                Text(
                    text = state.loginError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            // BOTÓN INGRESAR CON INDICADOR DE CARGA
            Button(
                onClick = { vm.login(onLoginSuccess) },
                enabled = !state.isLoading, // <-- DESHABILITADO durante la carga
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (state.isLoading) {
                    // MUESTRA EL ICONO DE CARGA
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Ingresar")
                }
            }

            // Enlaces de ayuda/registro
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(enabled = !state.isLoading) { /* Acción */ }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("¿No tienes cuenta? ")
                Text(
                    text = "¡Regístrate!",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(enabled = !state.isLoading, onClick = onRegisterClick)
                )
            }
        }
    }
}