package com.gonzalo.calculadora_imc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.gonzalo.calculadora_imc.imc.ResultadoImc
import com.gonzalo.calculadora_imc.ui.FormularioScreen
import com.gonzalo.calculadora_imc.ui.ResultadoScreen
import com.gonzalo.calculadora_imc.ui.theme.Calculadora_IMCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // La app se dibuja bajo la barra de estado y la de navegación (pantalla completa).
        enableEdgeToEdge()
        // setContent: aquí empieza Compose. Todo lo de dentro es interfaz declarativa.
        setContent {
            Calculadora_IMCTheme {
                App()
            }
        }
    }
}

/**
 * Decide QUÉ pantalla se ve, usando un único estado: el resultado del cálculo.
 *
 *   resultado == null   ->  todavía no se ha calculado  ->  mostramos el FORMULARIO
 *   resultado != null   ->  ya hay un IMC               ->  mostramos el RESULTADO
 *
 * Igual que en el login del tutorial: el formulario avisa con el resultado, lo
 * guardamos en el estado y Compose cambia de pantalla solo. "Volver" lo pone a null.
 */
@Composable
private fun App() {
    // stateSaver: le explica a rememberSaveable cómo guardar un ResultadoImc al girar el móvil.
    var resultado by rememberSaveable(stateSaver = ResultadoImc.Saver) { mutableStateOf<ResultadoImc?>(null) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val actual = resultado   // copia local para que Kotlin sepa que no es null en el "else"

        if (actual == null) {
            // El formulario no usa innerPadding: su degradado llega hasta detrás de las barras
            // y él mismo protege su contenido con safeDrawingPadding().
            FormularioScreen(
                onCalcular = { resultado = it },
            )
        } else {
            ResultadoScreen(
                resultado = actual,
                onVolver = { resultado = null },
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
