package com.gonzalo.calculadora_imc.ui

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
 * PANTALLA 1: FORMULARIO (dibujado a mano)
 *
 * Misma estructura que el Login del tutorial, pero en vez de correo y contraseña
 * pide nombre, sexo, peso y estatura, y el botón dice "Calcular IMC".
 *
 * Los textos, las cajas, los círculos y el botón son DIBUJOS hechos a mano
 * (res/drawable-nodpi/form_*.png): tinta negra sobre fondo transparente.
 * Aquí se tiñen del color que toque (blanco, rojo si hay error...) con ColorFilter.
 * Lo que escribe el usuario sí va con letra normal, dentro de las cajas dibujadas.
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
                    .verticalScroll(rememberScrollState()), // con el móvil girado, se puede desplazar
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Ancho máximo: con el móvil girado, las cajas dibujadas no se estiran de lado a lado.
                Column(
                    modifier = Modifier
                        .widthIn(max = 480.dp)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(32.dp))

                    // --- Cabecera: logo + título ---
                    LogoCircular(icono = R.drawable.form_logo, tamanoIcono = 44.dp)
                    Spacer(Modifier.height(16.dp))
                    // widthIn(max): con el móvil girado, el título no crece sin parar.
                    Dibujo(
                        id = R.drawable.form_titulo,
                        descripcion = "Calculadora de IMC",
                        modifier = Modifier
                            .widthIn(max = 320.dp)
                            .fillMaxWidth(0.9f),
                    )
                    Spacer(Modifier.height(6.dp))
                    Dibujo(
                        id = R.drawable.form_subtitulo,
                        descripcion = "Índice de masa corporal",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.height(18.dp),
                    )

                    Spacer(Modifier.height(24.dp))

                    // --- Nombre ---
                    EtiquetaDeCampo(R.drawable.form_nombre, "Nombre")
                    CampoDibujado(
                        valor = nombre,
                        onValorChange = { nombre = it },
                        caja = R.drawable.form_caja_larga,
                        descripcion = "Nombre",
                        teclado = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next,
                        ),
                    )

                    Spacer(Modifier.height(16.dp))

                    // --- Sexo: dos opciones, solo se puede elegir una ---
                    EtiquetaDeCampo(R.drawable.form_sexo, "Sexo")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectableGroup(),   // los lectores de pantalla lo anuncian como un grupo
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                    ) {
                        Sexo.entries.forEach { opcion ->
                            OpcionSexo(
                                texto = opcion.texto,
                                dibujo = if (opcion == Sexo.HOMBRE) R.drawable.form_hombre else R.drawable.form_mujer,
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
                            EtiquetaDeCampo(R.drawable.form_peso, "Peso")
                            CampoDibujado(
                                valor = peso,
                                onValorChange = {
                                    peso = it
                                    errorPeso = false   // quitamos el error mientras el usuario corrige
                                },
                                caja = R.drawable.form_caja_corta,
                                unidad = R.drawable.form_kg,
                                hayError = errorPeso,
                                descripcion = "Peso en kilos",
                                teclado = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                            )
                            AvisoDeError(errorPeso, R.drawable.form_error_peso, "Entre 2 y 500")
                        }
                        Column(Modifier.weight(1f)) {
                            EtiquetaDeCampo(R.drawable.form_estatura, "Estatura")
                            CampoDibujado(
                                valor = estatura,
                                onValorChange = {
                                    estatura = it
                                    errorEstatura = false
                                },
                                caja = R.drawable.form_caja_corta,
                                unidad = R.drawable.form_cm,
                                hayError = errorEstatura,
                                descripcion = "Estatura en centímetros",
                                teclado = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            )
                            AvisoDeError(errorEstatura, R.drawable.form_error_estatura, "Entre 50 y 250")
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // --- Botón principal: AQUÍ ESTÁ LA LÓGICA ---
                    BotonDibujado(
                        habilitado = formularioCompleto,
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
                    )

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

/**
 * Muestra uno de los dibujos a mano, teñido del color indicado.
 * Los PNG son tinta negra sobre transparente: ColorFilter.tint pinta la tinta del color que queramos.
 * Si solo le damos alto (o ancho), el otro lado se calcula solo y el dibujo no se deforma.
 */
@Composable
internal fun Dibujo(
    @DrawableRes id: Int,
    modifier: Modifier = Modifier,
    descripcion: String? = null,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    Image(
        painter = painterResource(id),
        contentDescription = descripcion,
        colorFilter = ColorFilter.tint(color),
        contentScale = ContentScale.Fit,
        modifier = modifier,
    )
}

/** Una opción de sexo: el círculo dibujado y su palabra. Se puede pulsar en cualquiera de los dos. */
@Composable
private fun OpcionSexo(texto: String, @DrawableRes dibujo: Int, seleccionada: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .selectable(selected = seleccionada, onClick = onClick, role = Role.RadioButton)
            .semantics { contentDescription = texto }
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Dibujo(
            id = if (seleccionada) R.drawable.form_circulo_marcado else R.drawable.form_circulo_vacio,
            modifier = Modifier.height(26.dp),
        )
        Spacer(Modifier.width(8.dp))
        Dibujo(id = dibujo, modifier = Modifier.height(24.dp))
    }
}

/**
 * Campo de texto dentro de una caja dibujada.
 * BasicTextField es un campo "desnudo", sin bordes: la caja la ponemos nosotros
 * en decorationBox, estirada al tamaño del campo (ContentScale.FillBounds).
 * Si hay un error, la caja se tiñe de rojo.
 */
@Composable
private fun CampoDibujado(
    valor: String,
    onValorChange: (String) -> Unit,
    @DrawableRes caja: Int,
    descripcion: String,
    teclado: KeyboardOptions,
    @DrawableRes unidad: Int? = null,
    hayError: Boolean = false,
) {
    val colorTinta = if (hayError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground

    BasicTextField(
        value = valor,
        onValueChange = onValorChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
        keyboardOptions = teclado,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = descripcion },
        decorationBox = { campo ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Image(
                    painter = painterResource(caja),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(colorTinta),
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize(),
                )
                Row(
                    modifier = Modifier.padding(start = 20.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.weight(1f)) { campo() }   // aquí va el texto que escribe el usuario
                    if (unidad != null) {
                        Spacer(Modifier.width(6.dp))
                        Dibujo(id = unidad, color = colorTinta, modifier = Modifier.height(20.dp))
                    }
                }
            }
        },
    )
}

/** Aviso dibujado en rojo debajo de un campo. Siempre reserva su hueco para que nada salte al aparecer. */
@Composable
private fun AvisoDeError(visible: Boolean, @DrawableRes dibujo: Int, texto: String) {
    Box(
        modifier = Modifier
            .height(24.dp)
            .padding(start = 12.dp, top = 6.dp),
    ) {
        if (visible) {
            Dibujo(
                id = dibujo,
                descripcion = texto,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.height(16.dp),
            )
        }
    }
}

/** El botón "Calcular IMC" dibujado. Desactivado se ve clarito y no responde. */
@Composable
private fun BotonDibujado(habilitado: Boolean, onClick: () -> Unit) {
    Dibujo(
        id = R.drawable.form_boton,
        modifier = Modifier
            .widthIn(max = 360.dp)   // con el móvil girado no se hace gigante
            .fillMaxWidth()
            .alpha(if (habilitado) 1f else 0.38f)
            .clip(RoundedCornerShape(50))
            .clickable(enabled = habilitado, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = "Calcular IMC" },
    )
}

/** Etiqueta dibujada encima de un campo, con la sangría que tiene en el diseño. */
@Composable
private fun EtiquetaDeCampo(@DrawableRes dibujo: Int, texto: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, bottom = 8.dp),
    ) {
        Dibujo(id = dibujo, descripcion = texto, modifier = Modifier.height(22.dp))
    }
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
