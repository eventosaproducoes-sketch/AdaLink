package com.adalink.rayx

import kotlin.math.*

object NTNOrbit {

    private const val EARTH_KM = 6371.0
    private const val ALTITUDE_KM = 600.0
    private const val MU = 398600.4418

    fun posicao(
        tempoSeg: Double
    ): DoubleArray {

        val r = EARTH_KM + ALTITUDE_KM

        val velocidade =
            sqrt(MU / r)

        val angulo =
            (velocidade / r) * tempoSeg

        val x = r * cos(angulo)
        val y = r * sin(angulo)
                val z = 0.0

        return doubleArrayOf(x, y, z)
    }

    fun velocidadeOrbital(): Double {
        val r = EARTH_KM + ALTITUDE_KM
        return sqrt(MU / r)
    }
