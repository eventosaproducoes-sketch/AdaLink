package com.adalink.rayx

import kotlin.math.abs

object NTNEngine {

    fun simular(): String {

        val distanciaKm = 600.0
        val velocidadeRelativa = 7500.0
        val velocidadeLuz = 299792458.0
        val frequenciaHz = 1800000000.0

        val distanciaM = distanciaKm * 1000.0

        val atrasoMs =
            (distanciaM / velocidadeLuz) * 1000.0

        val dopplerHz =
            abs(
                (velocidadeRelativa / velocidadeLuz) *
                frequenciaHz
            )

        return """
🛰️ ADA-LINK NTN ENGINE

SATÉLITE VIRTUAL

Distância: ${distanciaKm} km
Velocidade relativa: ${velocidadeRelativa} m/s

Frequência simulada: ${frequenciaHz / 1_000_000} MHz

Atraso estimado:
${"%.3f".format(atrasoMs)} ms

Doppler estimado:
${"%.1f".format(dopplerHz)} Hz

STATUS:
🟡 SIMULAÇÃO NTN

Nenhum sinal RF foi transmitido.
""".trimIndent()
    }
}
