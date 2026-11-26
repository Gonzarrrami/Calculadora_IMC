package com.gonzalo.calculadora_imc.ui

import android.app.Activity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.gonzalo.calculadora_imc.R
import com.gonzalo.calculadora_imc.imc.ResultadoImc
import com.gonzalo.calculadora_imc.imc.Sexo
import com.gonzalo.calculadora_imc.imc.calcularImc
import com.gonzalo.calculadora_imc.ui.theme.FormularioTheme

// Límites razonables para no calcular con datos imposibles.
private val PESO_VALIDO = 2.0..500.0        // kg
private val ESTATURA_VALIDA = 50.0..250.0   // cm

/*
 * PANTALLA 1: FORMULARIO
 *
 * Misma estructura que el Login del tutorial, pero en vez de correo y contraseña
 * pide nombre, sexo, peso y estatura, y el botón dice "Calcular IMC".
 *
 * Esta pantalla NO abre el resultado. Solo calcula y avisa con "onCalcular",
 * entregando el resultado. Quien la use (MainActivity) decide qué hacer con él.
 */
@Composable
fun FormularioScreen(
    onCalcular: (resultado: ResultadoImc) -> Unit,
    modifier: Modifier = Modifier,
) {
    // El diseño es siempre oscuro, aunque el móvil esté en modo claro.
    FormularioTheme {
        IconosDeBarrasClaros()

        // ---------- ESTADO ----------
        // Peso y estatura se guardan como TEXTO, tal cual los escribe el usuario.
        // Solo los convertimos a número al pulsar "Calcular IMC".
        var nombre by rememberSaveable { mutableStateOf("") }
        var sexo by rememberSaveable { mutableStateOf<Sexo?>(null) }
        var peso by rememberSaveable { mutableStateOf("") }
        var estatura by rememberSaveable { mutableStateOf("") }
        var errorPeso by rememberSaveable { mutableStateOf(false) }
        var errorEstatura by rememberSaveable { mutableStateOf(false) }

        // El botón solo se activa si está todo relleno.
        val formularioCompleto = nombre.isNotBlank() && sexo != null && peso.isNotBlank() && estatura.isNotBlank()

        // ---------- DISEÑO ----------
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.background,
                        ),
                    ),
                ),
        ) {
            CirculosDecorativos()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()                  // no se tapa con las barras ni con el teclado
                    .verticalScroll(rememberScrollState()) // con el móvil girado, se puede desplazar
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(32.dp))

                // --- Cabecera: logo + título ---
                LogoCircular(icono = R.drawable.ic_bascula, tamanoIcono = 36.dp)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Calculadora de IMC",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,   // si no cabe en una línea, las dos quedan centradas
                )
                Text(
                    text = "Índice de masa corporal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(24.dp))

                // --- Nombre ---
                EtiquetaDeCampo("Nombre")
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    singleLine = true,
                    shape = CircleShape,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Nombre" },
                )

                Spacer(Modifier.height(16.dp))

                // --- Sexo: dos opciones, solo se puede elegir una ---
                EtiquetaDeCampo("Sexo")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),   // los lectores de pantalla lo anuncian como un grupo
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    Sexo.entries.forEach { opcion ->
                        OpcionSexo(
                            texto = opcion.texto,
                            seleccionada = sexo == opcion,
                            onClick = { sexo = opcion },
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // --- Peso y estatura, uno al lado del otro ---
                // weight(1f) reparte el ancho: cada campo ocupa la mitad.
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        EtiquetaDeCampo("Peso")
                        CampoNumero(
                            valor = peso,
                            onValorChange = {
                                peso = it
                                errorPeso = false   // quitamos el error mientras el usuario corrige
                            },
                            unidad = "kg",
                            hayError = errorPeso,
                            mensajeError = "Entre 2 y 500",
                            teclado = KeyboardType.Decimal,
                            descripcion = "Peso en kilos",
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        EtiquetaDeCampo("Estatura")
                        CampoNumero(
                            valor = estatura,
                            onValorChange = {
                                estatura = it
                                errorEstatura = false
                            },
                            unidad = "cm",
                            hayError = errorEstatura,
                            mensajeError = "Entre 50 y 250",
                            teclado = KeyboardType.Number,
                            descripcion = "Estatura en centímetros",
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // --- Botón principal: AQUÍ ESTÁ LA LÓGICA ---
                OutlinedButton(
                    onClick = {
                        // Convertimos el texto a número. Aceptamos coma o punto: "72,5" o "72.5".
                        val pesoKg = peso.replace(',', '.').toDoubleOrNull()
                        val estaturaCm = estatura.replace(',', '.').toDoubleOrNull()
                        errorPeso = pesoKg == null || pesoKg !in PESO_VALIDO
                        errorEstatura = estaturaCm == null || estaturaCm !in ESTATURA_VALIDA

                        val sexoElegido = sexo
                        if (!errorPeso && !errorEstatura && pesoKg != null && estaturaCm != null && sexoElegido != null) {
                            onCalcular(
                                ResultadoImc(
                                    nombre = nombre.trim(),
                                    sexo = sexoElegido,
                                    imc = calcularImc(pesoKg, estaturaCm),
                                ),
                            )
                        }
                    },
                    enabled = formularioCompleto,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Calcular IMC")
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

/** Una opción de sexo: el círculo de selección y su texto. Se puede pulsar en cualquiera de los dos. */
@Composable
private fun OpcionSexo(texto: String, seleccionada: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .selectable(selected = seleccionada, onClick = onClick, role = Role.RadioButton)
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = seleccionada, onClick = null)   // null: el clic lo recoge la fila entera
        Spacer(Modifier.width(8.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

/** Campo para un número con su unidad al final ("kg", "cm") y un mensaje si es incorrecto. */
@Composable
private fun CampoNumero(
    valor: String,
    onValorChange: (String) -> Unit,
    unidad: String,
    hayError: Boolean,
    mensajeError: String,
    teclado: KeyboardType,
    descripcion: String,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        singleLine = true,
        isError = hayError,
        shape = CircleShape,
        suffix = { Text(unidad) },
        supportingText = { if (hayError) Text(mensajeError) },
        keyboardOptions = KeyboardOptions(keyboardType = teclado, imeAction = ImeAction.Next),
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = descripcion },
    )
}

/** Etiqueta pequeña encima de un campo, con la sangría que tiene en el diseño. */
@Composable
private fun EtiquetaDeCampo(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, bottom = 8.dp),
    )
}

/** Los dos círculos difuminados de la parte superior del diseño de Figma. */
@Composable
private fun CirculosDecorativos() {
    // Los colores se leen del tema ANTES de entrar en el Canvas.
    val blanco = MaterialTheme.colorScheme.onBackground
    val azul = MaterialTheme.colorScheme.secondary

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Medidas pensadas para un ancho de 360: "u" las adapta a cualquier pantalla.
        val u = size.width / 360f

        dibujarCirculo(Offset(24 * u, 66 * u), 100 * u, blanco)
        dibujarCirculo(Offset(288 * u, 72 * u), 56 * u, azul)
    }
}

/** Un círculo que se desvanece de arriba hacia abajo hasta hacerse transparente. */
private fun DrawScope.dibujarCirculo(centro: Offset, radio: Float, color: Color) {
    drawCircle(
        brush = Brush.verticalGradient(
            colors = listOf(color, color.copy(alpha = 0f)),
            startY = centro.y - radio,
            endY = centro.y + radio,
        ),
        radius = radio,
        center = centro,
    )
}

/** Con el fondo oscuro, los iconos de las barras del sistema deben ser claros. */
@Composable
private fun IconosDeBarrasClaros() {
    val view = LocalView.current
    if (view.isInEditMode) return   // en la @Preview no hay barras del sistema

    DisposableEffect(Unit) {
        val controller = WindowCompat.getInsetsController((view.context as Activity).window, view)
        val estabaClaroArriba = controller.isAppearanceLightStatusBars
        val estabaClaroAbajo = controller.isAppearanceLightNavigationBars
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
        // Al salir del formulario, las barras vuelven a como estaban.
        onDispose {
            controller.isAppearanceLightStatusBars = estabaClaroArriba
            controller.isAppearanceLightNavigationBars = estabaClaroAbajo
        }
    }
}

// Vista previa en Android Studio, con el tamaño del Samsung Galaxy S24 (360 x 780 dp).
@Preview(name = "Galaxy S24", showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun FormularioScreenPreview() {
    FormularioScreen(onCalcular = {})
}
