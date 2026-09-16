package com.adalink.rayx

import android.location.GnssClock
import android.location.GnssMeasurement
import android.location.GnssMeasurementsEvent
import android.location.GnssNavigationMessage
import android.location.GnssStatus
import android.location.LocationManager
import android.os.Build

object GnssDeepScan {

    fun cabecalho(): String {
        return """
🛰️ GNSS DEEP SCAN
==============================

📱 PLATAFORMA
Android: ${Build.VERSION.RELEASE}
API: ${Build.VERSION.SDK_INT}

OBJETIVO:
Descobrir quais dados GNSS reais
o Redmi fornece ao Android.

Aguardando medições...
""".trimIndent()
    }

    fun satelite(
        status: GnssStatus
    ): String {

        val texto = StringBuilder()

        texto.append(
            "\n🛰️ SATÉLITES GNSS\n"
        )

        texto.append(
            "Total: ${status.satelliteCount}\n"
        )

        var usados = 0

        for (i in 0 until status.satelliteCount) {

            if (status.usedInFix(i)) {
                usados++
            }
        }

        texto.append(
            "Usados no fix: $usados\n\n"
        )

        for (i in 0 until status.satelliteCount) {

            val cn0 = status.getCn0DbHz(i)
            val az = status.getAzimuthDegrees(i)
            val el = status.getElevationDegrees(i)
            val freq =
                if (Build.VERSION.SDK_INT >= 26)
                    status.getCarrierFrequencyHz(i)
                else
                    Float.NaN

            texto.append(
                "SV ${status.getSvid(i)} | " +
                "Const ${status.getConstellationType(i)} | " +
                "C/N0 %.1f dB-Hz | ".format(cn0) +
                "Az %.1f° | ".format(az) +
                "El %.1f° | ".format(el)
            )

            if (freq.isNaN()) {
                texto.append("Freq: indisponível")
            } else {
                texto.append(
                    "Freq: %.3f MHz".format(
                        freq / 1_000_000.0
                    )
                )
            }

            texto.append(
                " | Fix: ${status.usedInFix(i)}\n"
            )
        }

        return texto.toString()
    }

    fun medicao(
        event: GnssMeasurementsEvent
    ): String {

        val texto = StringBuilder()

        texto.append(
            "\n📡 GNSS RAW\n"
        )

        texto.append(
            "Medições: ${event.measurements.size}\n\n"
        )

        var pseudorange = 0
        var pseudorangeRate = 0
        var carrierPhase = 0
        var agc = 0

        for (m in event.measurements) {

            if (m.state != 0) {

                if (m.receivedSvTimeNanos != 0L) {
                    pseudorange++
                }

                if (m.pseudorangeRateMetersPerSecond.isFinite()) {
                    pseudorangeRate++
                }

                if (
                    Build.VERSION.SDK_INT >= 26 &&
                    m.accumulatedDeltaRangeState != 0
                ) {
                    carrierPhase++
                }

                if (
                    Build.VERSION.SDK_INT >= 26 &&
                    m.hasAutomaticGainControlLevelDb()
                ) {
                    agc++
                }
            }
        }

        texto.append(
            "Pseudorange candidato: $pseudorange\n"
        )

        texto.append(
            "Pseudorange rate: $pseudorangeRate\n"
        )

        texto.append(
            "ADR/fase: $carrierPhase\n"
        )

        texto.append(
            "AGC: $agc\n"
        )

        texto.append(
            "\nSTATUS: medições recebidas\n"
        )

        return texto.toString()
    }

    fun relogio(
        clock: GnssClock
    ): String {

        return """
⏱️ GNSS CLOCK

TimeNanos:
${clock.timeNanos}

Bias:
${clock.biasNanos}

Drift:
${clock.fullBiasNanos}

Uncertainty:
${clock.timeUncertaintyNanos}

STATUS:
Relógio GNSS recebido
""".trimIndent()
    }

    fun navegacao(
        message: GnssNavigationMessage
    ): String {

        return """
📨 GNSS NAVIGATION MESSAGE

Constelação:
${message.type}

SVID:
${message.svid}

Mensagem:
${message.messageId}

Submensagem:
${message.submessageId}

Status:
${message.status}

Dados:
${message.data.size} bytes

STATUS:
Mensagem GNSS recebida
""".trimIndent()
    }
}
