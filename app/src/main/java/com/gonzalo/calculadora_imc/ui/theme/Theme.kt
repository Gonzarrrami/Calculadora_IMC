package com.gonzalo.calculadora_imc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Un "ColorScheme" asigna cada color a su ROL dentro de Material 3.
// Los componentes (Button, OutlinedTextField...) leen estos roles solos,
// por eso no hace falta pintar cada uno a mano.
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    secondary = Secondary,
    tertiary = Tertiary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    surface = Surface,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    // El fondo del Scaffold usa "background": lo igualamos a "surface".
    background = Surface,
    onBackground = OnSurface,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    tertiary = DarkTertiary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    background = DarkSurface,
    onBackground = DarkOnSurface,
)

/** Tema de la app: claro u oscuro según la configuración del móvil. Sin color dinámico. */
@Composable
fun Calculadora_IMCTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}

/**
 * Tema del formulario: copia el diseño del login, que es siempre oscuro,
 * así que esta pantalla usa el esquema oscuro aunque el móvil esté en modo claro.
 */
@Composable
fun FormularioTheme(content: @Composable () -> Unit) {
    Calculadora_IMCTheme(darkTheme = true, content = content)
}
