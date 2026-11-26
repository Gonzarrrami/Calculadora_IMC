package com.gonzalo.calculadora_imc.imc

import androidx.annotation.DrawableRes
import androidx.compose.runtime.saveable.Saver
import com.gonzalo.calculadora_imc.R
import java.util.Locale
import kotlin.math.roundToInt

/*
 * LÓGICA DEL IMC (sin nada de interfaz).
 *
 * IMC = peso (kg) / estatura (m)²
 *
 * El sexo NO entra en la fórmula: solo lo usamos para elegir la ilustración.
 */

/** Sexo del usuario. Solo decide qué dibujo se muestra. */
enum class Sexo(val texto: String) {
    HOMBRE("Hombre"),
    MUJER("Mujer"),
}

/**
 * Un rango del IMC: su nombre y el intervalo [minimo, maximo).
 * "maximo" no se incluye: un IMC de 25,0 ya es "Sobrepeso".
 */
data class RangoImc(
    val nombre: String,
    val minimo: Double,
    val maximo: Double,
)

/**
 * LISTA INMUTABLE con la clasificación oficial de la OMS (Organización Mundial
 * de la Salud) para adultos: 6 clases.
 * "listOf" crea una lista que no se puede modificar: los rangos nunca cambian.
 */
val RANGOS_IMC: List<RangoImc> = listOf(
    RangoImc("Bajo peso", 0.0, 18.5),
    RangoImc("Peso normal", 18.5, 25.0),
    RangoImc("Sobrepeso", 25.0, 30.0),
    RangoImc("Obesidad de grado I", 30.0, 35.0),
    RangoImc("Obesidad de grado II", 35.0, 40.0),
    RangoImc("Obesidad de grado III", 40.0, Double.POSITIVE_INFINITY),
)

/**
 * LISTAS INMUTABLES con una ilustración por rango, en el MISMO orden que RANGOS_IMC.
 * Para poner tus propios dibujos, sustituye los archivos de res/drawable
 * manteniendo el mismo nombre (imc_hombre_1 ... imc_mujer_6).
 */
val ILUSTRACIONES: Map<Sexo, List<Int>> = mapOf(
    Sexo.HOMBRE to listOf(
        R.drawable.imc_hombre_1, R.drawable.imc_hombre_2, R.drawable.imc_hombre_3,
        R.drawable.imc_hombre_4, R.drawable.imc_hombre_5, R.drawable.imc_hombre_6,
    ),
    Sexo.MUJER to listOf(
        R.drawable.imc_mujer_1, R.drawable.imc_mujer_2, R.drawable.imc_mujer_3,
        R.drawable.imc_mujer_4, R.drawable.imc_mujer_5, R.drawable.imc_mujer_6,
    ),
)

/** Calcula el IMC redondeado a un decimal. La estatura llega en centímetros. */
fun calcularImc(pesoKg: Double, estaturaCm: Double): Double {
    val estaturaM = estaturaCm / 100
    val imc = pesoKg / (estaturaM * estaturaM)
    return (imc * 10).roundToInt() / 10.0   // redondeamos ANTES de clasificar, para que número y estado coincidan
}

/** Posición del rango que corresponde a un IMC (0 = Bajo peso ... 5 = Obesidad de grado III). */
fun indiceRango(imc: Double): Int = RANGOS_IMC.indexOfFirst { imc < it.maximo }

/** Escribe un número con coma decimal, como se hace en España: 24.3 -> "24,3". */
fun formatear(numero: Double): String = String.format(Locale.forLanguageTag("es-ES"), "%.1f", numero)

/** El resultado que viaja del formulario a la pantalla de resultado. */
data class ResultadoImc(
    val nombre: String,
    val sexo: Sexo,
    val imc: Double,
) {
    val indice: Int get() = indiceRango(imc)
    val rango: RangoImc get() = RANGOS_IMC[indice]
    @get:DrawableRes
    val ilustracion: Int get() = ILUSTRACIONES.getValue(sexo)[indice]

    companion object {
        /**
         * Explica a rememberSaveable cómo guardar un ResultadoImc (por ejemplo, al girar el móvil):
         * lo convertimos en una lista de valores simples y luego lo reconstruimos.
         */
        val Saver: Saver<ResultadoImc?, ArrayList<Any>> = Saver(
            save = { r -> r?.let { arrayListOf(it.nombre, it.sexo.name, it.imc) } },
            restore = { l -> ResultadoImc(l[0] as String, Sexo.valueOf(l[1] as String), l[2] as Double) },
        )
    }
}
