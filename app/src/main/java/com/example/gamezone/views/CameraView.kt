package com.example.gamezone.views

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.gamezone.viewmodels.HomeViewModel
import com.example.gamezone.viewmodels.UserProfile
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch // Importante para scope.launch

/**
 * Vista para capturar una foto de perfil y mostrar los datos del usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraView(
    vm: HomeViewModel = viewModel() // Usamos HomeViewModel para obtener el perfil
) {
    val context = LocalContext.current
    val userProfile = vm.userProfile.collectAsState().value
    val photoUriState = vm.photoUri.collectAsState().value

    val scope = rememberCoroutineScope()

    // Estado: permiso, Uri actual del archivo para capturar
    var hasCameraPermission by rememberSaveable { mutableStateOf(false) }
    var pendingImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }


    // Cargar URI persistida de DataStore al iniciar la vista
    LaunchedEffect(Unit) {
        vm.loadPhotoUri(context)
    }

    // 1) Launcher para TakePicture
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // <<< CÓDIGO MODIFICADO: ENVOLVER LLAMADA SUSPEND EN scope.launch {} >>>
            scope.launch {
                vm.setPhotoUri(context, pendingImageUri)
            }
            // <<< FIN CÓDIGO MODIFICADO >>>
        } else {
            pendingImageUri = null
        }
    }

    // 2) Launcher para pedir permiso de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (granted) {
            launchCamera(context) { tempUri ->
                pendingImageUri = tempUri
                takePictureLauncher.launch(tempUri)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- BLOQUE DE PERFIL (FOTO Y DATOS) ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. FOTO DE PERFIL CIRCULAR
                ProfilePicture(photoUriState)

                // 2. DATOS DEL USUARIO
                Column {
                    Text(
                        text = userProfile.username,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = userProfile.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (hasCameraPermission) {
                                launchCamera(context) { takePictureLauncher.launch(it) }
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Tomar Foto", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            // --- FIN BLOQUE DE PERFIL ---

            Divider(Modifier.padding(vertical = 8.dp))

            Text(
                "Tus ajustes y compras recientes irían aquí.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun ProfilePicture(uri: Uri?) {
// ... (sin cambios)
    val size = 96.dp

    Card(
        shape = CircleShape, // <-- Aplica la forma circular
        modifier = Modifier
            .size(size)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape), // Borde púrpura
    ) {
        if (uri != null) {
            AsyncImage(
                model = uri,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Placeholder si no hay foto
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Placeholder",
                    modifier = Modifier.size(size * 0.6f),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/* Utilidades para la cámara (sin cambios) */
private fun createImageUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val image = File.createTempFile(
        "IMG_${timeStamp}_",
        ".jpg",
        storageDir
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        image
    )
}

private fun launchCamera(context: Context, onUriReady: (Uri) -> Unit) {
    val uri = createImageUri(context)
    onUriReady(uri)
}