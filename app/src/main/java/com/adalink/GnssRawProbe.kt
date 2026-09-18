package com.adalink

import android.location.GnssMeasurementsEvent

import android.os.Build

object GnssRawProbe {
  fun analisar(event: GnssMeasurementsEvent): String {
        val r = StringBuilder()

        r.append("🔬 GNSS RAW PROBE\n\n")
        r.append("Android: ${Build.VERSION.RELEASE}\n")
        r.append("API: ${Build.VERSION.SDK_INT}\n")
        r.append("Medições: ${event.measurements.size}\n\n")
val clock = event.clock

        r.append("⏱️ GNSS CLOCK\n")
        r.append("TimeNanos: ${clock.timeNanos}\n")
        r.append("FullBiasNanos: ${clock.fullBiasNanos}\n\n")

        var gps = 0
        var glonass = 0
        var galileo = 0
        var beidou = 0
        var qzss = 0
        var irnss = 0
        var sbas = 0
        var unknown = 0

        for (m in event.measurements) {

            when (m.constellationType) {
                GnssStatus.CONSTELLATION_GPS -> gps++
                GnssStatus.CONSTELLATION_GLONASS -> glonass++
                GnssStatus.CONSTELLATION_GALILEO -> galileo++
                GnssStatus.CONSTELLATION_BEIDOU -> beidou++
                GnssStatus.CONSTELLATION_QZSS -> qzss++
                GnssStatus.CONSTELLATION_IRNSS -> irnss++
                GnssStatus.CONSTELLATION_SBAS -> sbas++
                else -> unknown++
            }
        }
        r.append("📡 CONSTELAÇÕES\n")
        r.append("GPS: $gps\n")
        r.append("GLONASS: $glonass\n")
        r.append("GALILEO: $galileo\n")
        r.append("BEIDOU: $beidou\n")
        r.append("QZSS: $qzss\n")
        r.append("IRNSS: $irnss\n")
        r.append("SBAS: $sbas\n")
        r.append("UNKNOWN: $unknown\n\n")

        r.append("📊 MEDIÇÕES RAW\n\n")

        for (m in event.measurements) {

            val nome = constellationNome(m.constellationType)

            r.append("$nome | SV ${m.svid}\n")
            r.append(
                "C/N0: %.1f dB-Hz\n".format(m.cn0DbHz)
            )

            r.append(
    "Pseudorange: não fornecida diretamente pela API\n"
)

            r.append(
                "Pseudorange Rate: %.3f m/s\n".format(
                    m.pseudorangeRateMetersPerSecond
                )
            )

            if (Build.VERSION.SDK_INT >= 26 && m.hasCarrierFrequencyHz()) {
                r.append(
                    "Frequência: %.3f MHz\n".format(
                        m.carrierFrequencyHz / 1_000_000.0
                    )
                )
            }

            r.append(
                "SV Time: ${m.receivedSvTimeNanos} ns\n"
            )

            r.append(
                "ADR: %.3f m\n".format(
                    m.accumulatedDeltaRangeMeters
                )
            )

            r.append(
                "State: ${m.state}\n\n"
            )
        }

        return r.toString()
    }

    private fun constellationNome(tipo: Int): String {
        return when (tipo) {
            GnssStatus.CONSTELLATION_GPS -> "🇺🇸 GPS"
            GnssStatus.CONSTELLATION_GLONASS -> "🇷🇺 GLONASS"
            GnssStatus.CONSTELLATION_GALILEO -> "🇪🇺 GALILEO"
            GnssStatus.CONSTELLATION_BEIDOU -> "🇨🇳 BEIDOU"
            GnssStatus.CONSTELLATION_QZSS -> "🇯🇵 QZSS"
            GnssStatus.CONSTELLATION_IRNSS -> "🇮🇳 IRNSS"
            GnssStatus.CONSTELLATION_SBAS -> "🌐 SBAS"
            else -> "❓ UNKNOWN"
        }
    }
}
