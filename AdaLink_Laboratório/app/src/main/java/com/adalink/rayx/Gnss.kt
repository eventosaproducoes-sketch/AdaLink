package com.adalink.rayx

import android.location.GnssStatus
import android.location.LocationManager

object Gnss {

    fun texto(lm: LocationManager): String {

        return try {

            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                "🛰️ GNSS\n\nGPS: desligado"
            } else {
                "🛰️ GNSS\n\nGPS: ligado\nAguardando status dos satélites..."
            }

        } catch (e: Exception) {
            "🛰️ GNSS\n\nAcesso limitado"
        }
    }
}
