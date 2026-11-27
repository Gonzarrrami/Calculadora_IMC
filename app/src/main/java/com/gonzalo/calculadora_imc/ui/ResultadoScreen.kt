package com.gonzalo.calculadora_imc.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gonzalo.calculadora_imc.R
import com.gonzalo.calculadora_imc.imc.ResultadoImc
import com.gonzalo.calculadora_imc.imc.Sexo
import com.gonzalo.calculadora_imc.imc.formatear
import com.gonzalo.calculadora_imc.ui.theme.Calculadora_IMCTheme

/*
 * PANTALLA 2: RESULTADO
 *
 * Recibe DOS cosas desde fuera:
 *   - "resultado": nombre, sexo e IMC calculado (parámetro de DATOS).
 *   - "onVolver": una función que llamamos al pulsar el botón (parámetro de ACCIÓN).
 * La pantalla no sabe qué pasa al volver: eso lo decide quien la usa.
 *
 * Los textos fijos, las cajas y el botón son DIBUJOS A MANO (res/drawable-nodpi/res_*.png).
 * Solo el nombre y el número del IMC se escriben con letra normal, porque cambian cada vez.
 */

/** Los estados dibujados, en el MISMO orden que RANGOS_IMC (0 = Bajo peso ... 5 = Obesidad de grado III). */
private val ESTADOS_DIBUJADOS = listOf(
    R.drawable.res_estado_1, R.drawable.res_estado_2, R.drawable.res_estado_3,
    R.drawable.res_estado_4, R.drawable.res_estado_5, R.drawable.res_estado_6,
)

/** Los intervalos dibujados ("I.M.C. entre 18,5 y 24,9"...), en el mismo orden. */
private val INTERVALOS_DIBUJADOS = listOf(
    R.drawable.res_intervalo_1, R.drawable.res_intervalo_2, R.drawable.res_intervalo_3,
    R.drawable.res_intervalo_4, R.drawable.res_intervalo_5, R.drawable.res_intervalo_6,
)

@Composable
fun ResultadoScreen(
    resultado: ResultadoImc,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Con el móvil girado no cabe todo en una columna: ponemos el dibujo a la izquierda
    // y los datos a la derecha. Si no, el dibujo quedaría diminuto.
    val horizontal = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (horizontal) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Ilustracion(resultado, Modifier.weight(1f).fillMaxHeight())
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Cabecera(resultado)
                Spacer(Modifier.height(16.dp))
                Estado(resultado, onVolver)
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(24.dp))
            Cabecera(resultado)
            Spacer(Modifier.height(16.dp))
            // weight(1f): el dibujo ocupa TODO el espacio libre, y así el botón queda pegado abajo.
            Ilustracion(resultado, Modifier.weight(1f).fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Estado(resultado, onVolver)
            Spacer(Modifier.height(16.dp))
        }
    }
}

/** Nombre del usuario y el IMC dentro de la caja dibujada. */
@Composable
private fun Cabecera(resultado: ResultadoImc) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // --- Nombre (letra normal: lo ha escrito el usuario) ---
        Text(
            text = resultado.nombre,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(16.dp))

        // --- "I.M.C.:" dibujado y el número dentro de la caja dibujada ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Dibujo(
                id = R.drawable.res_imc,
                descripcion = "IMC",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.height(28.dp),
            )
            Spacer(Modifier.width(12.dp))
            Box(contentAlignment = Alignment.Center) {
                // matchParentSize: la caja se estira al tamaño del número, sea cual sea.
                Image(
                    painter = painterResource(R.drawable.res_caja_imc),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize(),
                )
                Text(
                    text = formatear(resultado.imc),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 10.dp),
                )
            }
        }
    }
}

/**
 * El dibujo que corresponde al sexo y al estado, dentro del marco dibujado.
 * Dentro del marco dejamos el fondo de color suave para que los muñecos se vean bien también en modo oscuro.
 */
@Composable
private fun Ilustracion(resultado: ResultadoImc, modifier: Modifier) {
    // En un Box lo que va DESPUÉS se pinta ENCIMA: primero el fondo, luego el muñeco y al final el marco.
    // El fondo (res_marco_fondo) es el interior del marco dibujado, relleno: tiene su misma forma
    // y se estira igual, así que nunca sobresale.
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.res_marco_fondo),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primaryContainer),
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize(),
        )
        Image(
            painter = painterResource(resultado.ilustracion),
            contentDescription = "Ilustración: ${resultado.sexo.texto.lowercase()}, ${resultado.rango.nombre.lowercase()}",
            modifier = Modifier
                .matchParentSize()
                .padding(24.dp),
        )
        Image(
            painter = painterResource(R.drawable.res_marco),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize(),
        )
    }
}

/** El valor cualitativo (estado según la tabla de la OMS), su intervalo y el botón Volver, todo dibujado. */
@Composable
private fun Estado(resultado: ResultadoImc, onVolver: () -> Unit) {
    val rango = resultado.rango

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Dibujo(
            id = ESTADOS_DIBUJADOS[resultado.indice],
            descripcion = rango.nombre,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.height(32.dp),
        )
        Spacer(Modifier.height(4.dp))
        Dibujo(
            id = INTERVALOS_DIBUJADOS[resultado.indice],
            descripcion = textoIntervalo(rango.minimo, rango.maximo),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.height(22.dp),
        )

        Spacer(Modifier.height(16.dp))

        // Botón dibujado: la imagen ES el botón. clip redondea la zona del "toque" y el efecto al pulsar.
        Dibujo(
            id = R.drawable.res_volver,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .height(52.dp)
                .clip(RoundedCornerShape(50))
                .clickable(role = Role.Button, onClick = onVolver)
                .semantics { contentDescription = "Volver" },
        )
    }
}

/** Texto del intervalo del rango, para los lectores de pantalla: "IMC entre 25,0 y 29,9", "IMC menor de 18,5"... */
private fun textoIntervalo(minimo: Double, maximo: Double): String = when {
    minimo <= 0.0 -> "IMC menor de ${formatear(maximo)}"
    maximo.isInfinite() -> "IMC de ${formatear(minimo)} o más"
    else -> "IMC entre ${formatear(minimo)} y ${formatear(maximo - 0.1)}"
}

// Vista previa con el tamaño del Samsung Galaxy S24. Los datos son solo de ejemplo.
@Preview(name = "Galaxy S24", showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun ResultadoScreenPreview() {
    Calculadora_IMCTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ResultadoScreen(
                resultado = ResultadoImc(nombre = "Juan Carlos", sexo = Sexo.HOMBRE, imc = 27.4),
                onVolver = {},
            )
        }
    }
}
