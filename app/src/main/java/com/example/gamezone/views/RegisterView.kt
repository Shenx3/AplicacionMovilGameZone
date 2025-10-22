package com.example.gamezone.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.gamezone.viewmodels.RegisterViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterView(
    vm: RegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit
) {
    val state = vm.state.collectAsState().value
    val errors = vm.errors.collectAsState().value
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { Spacer(Modifier.height(16.dp)) }

            // TÍTULO
            item {
                Text(
                    text = "Crear cuenta",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Completa los campos. Todos son obligatorios salvo el Teléfono.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // --- DATOS PERSONALES ---
            item { Text("Datos personales", style = MaterialTheme.typography.titleLarge) }

            // NOMBRE COMPLETO
            item {
                OutlinedTextField(
                    value = state.nombreCompleto,
                    onValueChange = vm::onNombreChange,
                    label = { Text("Nombre completo") },
                    placeholder = { Text("Ej: Camila Perez") },
                    isError = errors.nombreCompleto != null,
                    supportingText = { if (errors.nombreCompleto != null) Text(errors.nombreCompleto!!) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // CORREO ELECTRÓNICO
            item {
                OutlinedTextField(
                    value = state.email,
                    onValueChange = vm::onEmailChange,
                    label = { Text("Correo electrónico") },
                    placeholder = { Text("nombre@dominio.com") },
                    isError = errors.email != null,
                    supportingText = { if (errors.email != null) Text(errors.email!!) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // TELÉFONO (Opcional)
            item {
                OutlinedTextField(
                    value = state.telefono,
                    onValueChange = vm::onTelefonoChange,
                    label = { Text("Teléfono (opcional)") },
                    placeholder = { Text("+56912345678") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // --- CREDENCIALES ---
            item { Text("Credenciales", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp)) }

            // NOMBRE DE USUARIO
            item {
                OutlinedTextField(
                    value = state.nombreUsuario,
                    onValueChange = vm::onUsuarioChange,
                    label = { Text("Nombre de usuario") },
                    placeholder = { Text("gamer_pro") },
                    isError = errors.nombreUsuario != null,
                    supportingText = { if (errors.nombreUsuario != null) Text(errors.nombreUsuario!!) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // CONTRASEÑA
            item { PasswordField(
                value = state.contrasena,
                onValueChange = vm::onContrasenaChange,
                label = "Contraseña",
                error = errors.contrasena
            ) }

            // CONFIRMAR CONTRASEÑA
            item { PasswordField(
                value = state.confirmarContrasena,
                onValueChange = vm::onConfirmarContrasenaChange,
                label = "Confirmar contraseña",
                error = errors.confirmarContrasena
            ) }

            // --- PREFERENCIAS (Radio Buttons) ---
            item { Text("Preferencias", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) }

            items(vm.generosVideojuego.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    rowItems.forEach { genero ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { vm.onGeneroChange(genero) }
                        ) {
                            RadioButton(
                                selected = state.generoFavorito == genero,
                                onClick = { vm.onGeneroChange(genero) }
                            )
                            Text(genero, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                    if (rowItems.size == 1) {
                        Spacer(Modifier.weight(1f)) // Para mantener la alineación de la fila
                    }
                }
            }
            // Error de preferencia
            if (errors.generoFavorito != null) {
                item {
                    Text(
                        errors.generoFavorito!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // --- TÉRMINOS Y CONDICIONES ---
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = state.aceptaTerminos,
                        onCheckedChange = vm::onTerminosChange
                    )
                    Text(
                        text = buildString {
                            append("Al registrarte aceptas los ")
                            append("Términos") // Simulación de enlace
                            append(" y ")
                            append("Política de Privacidad.") // Simulación de enlace
                        },
                        modifier = Modifier.clickable { vm.onTerminosChange(!state.aceptaTerminos) }
                    )
                }
                if (errors.aceptaTerminos != null) {
                    Text(
                        errors.aceptaTerminos!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }

            // --- BOTÓN ENVIAR ---
            item {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        vm.register {
                            scope.launch {
                                snackbarHostState.showSnackbar("¡Cuenta creada exitosamente!")
                            }
                            onRegisterSuccess()
                        }
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Crear cuenta")
                    }
                }
            }

            // Pie de formulario
            item {
                if (errors.generalError != null) {
                    Text(errors.generalError!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

/**
 * Componente reutilizable para los campos de contraseña.
 */
@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = { if (error != null) Text(error) else Text("8+ caracteres, mayúscula, minúscula y número.") },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = "Mostrar/Ocultar contraseña")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
