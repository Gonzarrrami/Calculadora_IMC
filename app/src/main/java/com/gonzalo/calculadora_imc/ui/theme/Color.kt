package com.gonzalo.calculadora_imc.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta sacada del diseño "Login Page" de Adarsh (comunidad de Figma, CC BY 4.0).
// El azul intenso (0xFF0154F6) es el del círculo decorativo del diseño.

// ---------- Esquema claro: lo usa el Resultado si el móvil está en modo claro ----------
val Primary = Color(0xFF0154F6)              // botones y textos destacados
val OnPrimary = Color(0xFFFFFFFF)            // texto sobre el color primario
val PrimaryContainer = Color(0xFFD9E2FF)     // fondo del logo y del marco de la ilustración
val OnPrimaryContainer = Color(0xFF001849)   // icono del logo
val Surface = Color(0xFFFAF9FF)              // fondo de la pantalla
val OnSurface = Color(0xFF1A1B20)            // texto principal
val OnSurfaceVariant = Color(0xFF44464F)     // texto secundario
val Outline = Color(0xFF757780)              // bordes
val OutlineVariant = Color(0xFFC5C6D0)       // líneas divisorias
val Secondary = Color(0xFF535F7D)
val Tertiary = Color(0xFF7A4FB5)

// ---------- Esquema oscuro: lo usa siempre el Formulario, y el Resultado en modo oscuro ----------
val DarkPrimary = Color(0xFFB3C7FF)            // borde del campo enfocado: se lee bien sobre el azul
val DarkOnPrimary = Color(0xFF002A78)
val DarkPrimaryContainer = Color(0xFF1F56CC)   // fondo del logo y del marco de la ilustración
val DarkOnPrimaryContainer = Color(0xFFFFFFFF)
val DarkSurface = Color(0xFF0E0E10)            // casi negro: final del degradado del Formulario
val DarkOnSurface = Color(0xFFFFFFFF)
val DarkSurfaceVariant = Color(0xFF0D3A9E)     // azul marino: inicio del degradado del Formulario
val DarkOnSurfaceVariant = Color(0xFFC5C6D0)
val DarkOutline = Color(0xFFD8DAE3)            // bordes blanquecinos de campos y botones
val DarkOutlineVariant = Color(0xFF44464F)
val DarkSecondary = Color(0xFF0154F6)          // círculo azul decorativo del Formulario
val DarkTertiary = Color(0xFF7CC4FF)
