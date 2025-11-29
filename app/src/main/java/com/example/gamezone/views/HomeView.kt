package com.example.gamezone.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamezone.viewmodels.HomeViewModel
import com.example.gamezone.data.Product
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.gamezone.viewmodels.CartViewModel
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material3.rememberTopAppBarState

// Imports para vibracion a la hora de anadir un producto al carrito
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.os.Vibrator
import android.os.Build
import kotlinx.coroutines.delay

import android.util.Log // Verificar si el celular vibra

// Función auxiliar para disparar la vibración (compatible con APIs modernas).
private fun triggerVibration(context: Context) {

    Log.d("VIBRATION_TEST", "Vibration service called successfully.")

    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Usar el servicio del sistema en APIs 31+
        context.getSystemService(Vibrator::class.java)
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Usar patrón de vibración para APIs 26+ (un pulso corto de 100ms)
        vibrator.vibrate(android.os.VibrationEffect.createOneShot(100, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(100)
    }
}
// -------------------------------------------------------------------


// Pantalla principal de GameZone mostrando Productos Destacados.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    vm: HomeViewModel = viewModel(),
    cartVm: CartViewModel,
    showLoginSuccessSnackbar: Boolean
) {
    val products = vm.products.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current // <<< OBTENER CONTEXTO PARA VIBRACIÓN

    var hasShownSnackbar by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(showLoginSuccessSnackbar) {
        if (showLoginSuccessSnackbar && !hasShownSnackbar) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Iniciaste sesión correctamente.",
                    duration = SnackbarDuration.Short
                )
            }
            hasShownSnackbar = true
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    AnimatedVisibility(
                        visible = scrollBehavior.state.collapsedFraction == 0f,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            "GameZone",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = paddingValues,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {

            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(Modifier.padding(top = 8.dp, bottom = 16.dp)) {
                    Text(
                        text = "Nuestros Productos Destacados",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Es una tienda en línea que ofrece una amplia variedad de videojuegos para diferentes plataformas. Títulos nuevos y clásicos, con descargas digitales y contenido exclusivo.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = Color.LightGray
                    )
                }
            }

            items(products) { product ->
                ProductCard(
                    product = product,
                    onAddToCart = {
                        cartVm.addToCart(product)

                        // 1. DISPARAR VIBRACIÓN (Segundo recurso nativo)
                        triggerVibration(context)

                        // 2. Mostrar Snackbar
                        scope.launch {
                            snackbarHostState.showSnackbar("¡${product.title} añadido al carrito!")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onAddToCart: () -> Unit) {
    // Estado local para la carga de este botón
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.medium)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = product.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 4.dp),
                maxLines = 3
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = product.price,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        onAddToCart() // Llama a la lógica original (VM + Vibración + Snackbar)
                        delay(500) // Simula una carga corta
                        isLoading = false
                    }
                },
                enabled = !isLoading, // El botón se deshabilita mientras carga
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                if (isLoading) {
                    // Muestra el indicador de carga
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    // Muestra el texto normal
                    Text("Añadir al carrito", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}