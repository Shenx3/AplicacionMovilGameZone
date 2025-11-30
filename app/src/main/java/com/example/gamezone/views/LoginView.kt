package com.example.gamezone.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamezone.network.RetrofitClient
import com.example.gamezone.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(
    vm: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotClick: () -> Unit
) {
    val state = vm.state.collectAsState().value
    var passwordVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // ESTADOS PARA EL DIÁLOGO DE CONFIGURACIÓN DE IP
    var showIpDialog by remember { mutableStateOf(false) }
    // Inicializa el campo de texto con la IP actual (sin http:// y / al final)
    var newIpAddress by remember {
        mutableStateOf(RetrofitClient.currentBaseUrl.removePrefix("http://").removePrefix("https://").removeSuffix("/"))
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        // Usamos Box para superponer el Column de contenido y el botón flotante
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .statusBarsPadding()
        ) {
            // Contenido principal de la vista (Login)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
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
                    placeholder = { Text("correo@dominio.com") },
                    enabled = !state.isLoading,
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
                    enabled = !state.isLoading,
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
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    if (state.isLoading) {
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
                        modifier = Modifier.clickable(enabled = !state.isLoading) { onForgotClick() }
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

            // BOTÓN FLOTANTE PARA CAMBIAR LA IP EN LA ESQUINA INFERIOR DERECHA
            FloatingActionButton(
                onClick = {
                    // Cargar la IP actual en el campo antes de abrir el diálogo
                    newIpAddress = RetrofitClient.currentBaseUrl.removePrefix("http://").removePrefix("https://").removeSuffix("/")
                    showIpDialog = true
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd) // Posicionamiento en la esquina inferior derecha
                    .padding(bottom = 16.dp, end = 16.dp)
                    .imePadding(),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer // Un color distinto para destacarlo
            ) {
                Icon(Icons.Filled.Settings, contentDescription = "Configurar IP del Backend")
            }
        }
    }

    // DIÁLOGO PARA CONFIGURAR LA IP
    if (showIpDialog) {
        AlertDialog(
            onDismissRequest = { showIpDialog = false },
            title = { Text("Configurar IP del Backend") },
            text = {
                Column {
                    Text("Ingresa la nueva IP y puerto (ej: 192.168.1.100:8080).")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newIpAddress,
                        onValueChange = { newIpAddress = it },
                        label = { Text("IP:Puerto") },
                        placeholder = { Text("192.168.1.100:8080") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "URL Base Actual: ${RetrofitClient.currentBaseUrl}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newIpAddress.isNotBlank()) {
                            RetrofitClient.updateBaseUrl(newIpAddress)
                            showIpDialog = false
                            scope.launch {
                                snackbarHostState.showSnackbar("IP del Backend actualizada a: ${RetrofitClient.currentBaseUrl}")
                            }
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showIpDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}