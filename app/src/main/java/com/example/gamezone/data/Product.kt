package com.example.gamezone.data

/**
 * Representa un producto destacado en la tienda GameZone.
 * Usamos Int para imageResId para simular la carga desde recursos locales (Drawable).
 */
data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: String,
    // imageResId simula la referencia a la imagen local (ej: R.drawable.godofwar)
    val imageResId: Int = 0
)

/**
 * Simulación de datos para la vista principal.
 */
val FeaturedProducts = listOf(
    Product( // Simulamos productos destacados
        id = 1, // Id único del producto
        title = "God of War I", // Título del producto
        description = "Una épica aventura de acción protagonizada por Kratos en su misión de venganza contra los dioses griegos.", // Descripción del producto
        price = "$32.990", // Precio del producto
        // imageResId = R.drawable.godofwar // Referencia a la imagen local (drawable)
    ),
    Product(
        id = 2,
        title = "God of War II",
        description = "Continuación de la saga, donde Kratos busca cambiar su destino enfrentándose a nuevos dioses y enemigos poderosos.",
        price = "$39.990",
    ),
    Product(
        id = 3,
        title = "Resident Evil 4 Remake",
        description = "Reimaginación del clásico survival horror con gráficos modernos y mecánicas mejoradas.",
        price = "$59.990",
    )
)