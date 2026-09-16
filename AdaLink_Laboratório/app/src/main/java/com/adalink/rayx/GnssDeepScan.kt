package com.adalink.rayx

import android.location.GnssStatus
import android.os.Build

object GnssDeepScan {

    fun satelites(status: GnssStatus): String {

        val r = StringBuilder()

        r.append("🛰️ GNSS DEEP SCAN\n\n")
        r.append("Android: ${Build.VERSION.RELEASE}\n")
        r.append("API: ${Build.VERSION.SDK_INT}\n\n")

        r.append("SATÉLITES: ${status.satelliteCount}\n\n")

        for (i in 0 until status.satelliteCount) {

            r.append(
                "SV ${status.getSvid(i)} | " +
                "Const ${status.getConstellationType(i)}\n"
            )

            r.append(
                "C/N0: %.1f dB-Hz\n".format(
                    status.getCn0DbHz(i)
                )
            )

            r.append(
                "Az: %.1f° | El: %.1f°\n".format(
                    status.getAzimuthDegrees(i),
                    status.getElevationDegrees(i)
                )
            )

            if (Build.VERSION.SDK_INT >= 26) {
                r.append(
                    "Freq: %.3f MHz\n".format(
                        status.getCarrierFrequencyHz(i) /
                            1_000_000.0
                    )
                )
            }

            r.append(
                "Usado no fix: ${status.usedInFix(i)}\n\n"
            )
        }


        return r.toString()
    }
}
