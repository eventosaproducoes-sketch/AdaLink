package com.adalink.rayx

import kotlin.math.*

object NTNEngine {

    fun simular(
        latitude: Double,
        longitude: Double,
        altitude: Double
    ): String {

        val terraKm = 6371.0
        val satAltitudeKm = 600.0
        val c = 299792458.0
        val frequenciaHz = 1800000000.0

        val lat = Math.toRadians(latitude)
        val lon = Math.toRadians(longitude)

        val raio = terraKm + altitude / 1000.0

        val x = raio * cos(lat) * cos(lon)
        val y = raio * cos(lat) * sin(lon)
        val z = raio * sin(lat)

        val satRaio = terraKm + satAltitudeKm

        val sx = satRaio
        val sy = 0.0
        val sz = 0.0

        val dx = sx - x
        val dy = sy - y
        val dz = sz - z

        val distanciaKm =
            sqrt(dx * dx + dy * dy + dz * dz)

        val atrasoMs =
            (distanciaKm * 1000.0 / c) * 1000.0

        val velocidadeModelo = 7500.0

        val dopplerHz =
            abs(
                (velocidadeModelo / c) *
                frequenciaHz
            )

        return """
🛰️ ADA-LINK NTN ENGINE

SATÉLITE VIRTUAL

Posição GNSS:
Latitude: %.6f°
Longitude: %.6f°
Altitude: %.1f m

Satélite virtual:
Altitude orbital: %.0f km

Distância calculada:
%.2f km

Atraso estimado:
%.3f ms

Doppler do modelo:
%.1f Hz

STATUS:
🟡 SIMULAÇÃO NTN

GNSS real será usado como entrada.

Nenhum sinal RF foi transmitido.
""".trimIndent().format(
            latitude,
            longitude,
            altitude,
            satAltitudeKm,
            distanciaKm,
            atrasoMs,
            dopplerHz
        )
    }
}
