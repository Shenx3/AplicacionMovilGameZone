package com.example.gamezone.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,

    // --- COLORES DE GAMEZONE ---
    background = Black,         // Fondo principal muy oscuro (0xFF1A1A2E)
    surface = GameZoneDarkBg,   // Superficies como Cards o TextField background
    onPrimary = White,          // Texto sobre el color primario
    onSecondary = White,        // Texto sobre el color secundario
    onTertiary = White,         // Texto sobre el color terciario
    onBackground = White,       // Texto sobre el fondo (White)
    onSurface = White,          // Texto sobre las superficies (White)
    error = GameZoneError       // Color para errores (Rojo)
    // ----------------------------
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Otros colores por defecto para el tema claro */
)

@Composable
fun GameZoneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            // Nota: Aquí el tema dinámico mantiene la preferencia de color del sistema (oscuro/claro)
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}