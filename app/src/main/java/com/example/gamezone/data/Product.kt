package com.example.gamezone.data

import androidx.compose.ui.graphics.Color

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: String,
    // --- CAMBIO AQUÍ ---
    // Cambiamos de 'imageResId: Int = 0' a 'imageUrl: String'
    val imageUrl: String
)

// He buscado URLs de Pinterest como solicitaste
private const val GOW1_URL = "https://i.pinimg.com/736x/b0/a6/1c/b0a61c3a3be8c2626e7a20d5ff9408bd.jpg"
private const val GOW2_URL = "https://i.pinimg.com/736x/a5/13/ac/a513acf7eaf24791953b3edfa75e2764.jpg"
private const val RE4_URL = "https://i.pinimg.com/736x/6c/fc/92/6cfc9279823b78bea232ef1408586ac5.jpg"


val FeaturedProducts = listOf(
    Product(
        id = 1,
        title = "God of War I",
        description = "Una épica aventura de acción protagonizada por Kratos en su misión de venganza contra los dioses griegos.",
        price = "$32.990",
        // --- CAMBIO AQUÍ ---
        imageUrl = GOW1_URL
    ),
    Product(
        id = 2,
        title = "God of War II",
        description = "Continuación de la saga, donde Kratos busca cambiar su destino enfrentándose a nuevos dioses y enemigos poderosos.",
        price = "$39.990",
        // --- CAMBIO AQUÍ ---
        imageUrl = GOW2_URL
    ),
    Product(
        id = 3,
        title = "Resident Evil 4 Remake",
        description = "Reimaginación del clásico survival horror con gráficos modernos y mecánicas mejoradas.",
        price = "$59.990",
        // --- CAMBIO AQUÍ ---
        imageUrl = RE4_URL
    )
)