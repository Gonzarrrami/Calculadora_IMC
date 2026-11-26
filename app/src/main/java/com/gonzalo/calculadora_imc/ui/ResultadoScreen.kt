package com.gonzalo.calculadora_imc.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
 */
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

/** Nombre del usuario y el IMC dentro de un recuadro. */
@Composable
private fun Cabecera(resultado: ResultadoImc) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // --- Nombre ---
        Text(
            text = resultado.nombre,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(16.dp))

        // --- "IMC:" y el número dentro de un recuadro ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "IMC:",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = formatear(resultado.imc),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp, vertical = 6.dp),
            )
        }
    }
}

/** El dibujo que corresponde al sexo y al estado, dentro de un marco de color suave. */
@Composable
private fun Ilustracion(resultado: ResultadoImc, modifier: Modifier) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(24.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(resultado.ilustracion),
            contentDescription = "Ilustración: ${resultado.sexo.texto.lowercase()}, ${resultado.rango.nombre.lowercase()}",
            modifier = Modifier.fillMaxSize(),
        )
    }
}

/** El valor cualitativo (estado según la tabla de la SEEDO), su intervalo y el botón Volver. */
@Composable
private fun Estado(resultado: ResultadoImc, onVolver: () -> Unit) {
    val rango = resultado.rango

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = rango.nombre,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = textoIntervalo(rango.minimo, rango.maximo),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onVolver,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
        ) {
            Text("Volver")
        }
    }
}

/** Texto del intervalo del rango: "IMC entre 25,0 y 26,9", "IMC menor de 18,5"... */
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
