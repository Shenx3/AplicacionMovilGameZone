package com.example.gamezone.data

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: String,
    // Usamos 0 ya que no tenemos drawables
    val imageResId: Int = 0
)

val FeaturedProducts = listOf(
    Product(
        id = 1,
        title = "God of War I",
        description = "Una épica aventura de acción protagonizada por Kratos en su misión de venganza contra los dioses griegos.",
        price = "$32.990",
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
