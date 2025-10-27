package com.example.gamezone.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamezone.viewmodels.HomeViewModel
import com.example.gamezone.data.Product
import com.example.gamezone.R
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.gamezone.viewmodels.CartViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla principal de GameZone mostrando Productos Destacados.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    vm: HomeViewModel = viewModel(),
    cartVm: CartViewModel, // Nuevo ViewModel inyectado
    showLoginSuccessSnackbar: Boolean
) {
    val products = vm.products.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ESTADO LOCAL: Bandera para asegurar que el mensaje solo se muestre una vez.
    var hasShownSnackbar by rememberSaveable { mutableStateOf(false) }

    // EFECTO LANZADO: Muestra el Snackbar solo si el login fue exitoso Y NO se ha mostrado antes.
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

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("GameZone", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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

            // Encabezado con descripción de la tienda
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

            // Grid de Productos
            items(products) { product ->
                ProductCard(
                    product = product,
                    onAddToCart = {
                        cartVm.addToCart(product) // Llama a la lógica de añadir al carrito
                        scope.launch {
                            snackbarHostState.showSnackbar("¡${product.title} añadido al carrito!")
                        }
                    }
                )
            }
        }
    }
}

// La función ProductCard se modifica para recibir la acción de añadir al carrito.
@Composable
fun ProductCard(product: Product, onAddToCart: () -> Unit) {
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = product.title,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

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
                onClick = onAddToCart, // Uso de la acción de añadir al carrito
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Añadir al carrito", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}