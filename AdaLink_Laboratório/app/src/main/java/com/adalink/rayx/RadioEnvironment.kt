package com.adalink.rayx

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.bluetooth.BluetoothAdapter
import android.location.LocationManager
import android.os.Build

object RadioEnvironment {

    fun texto(context: Context): String {

        val cm = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val lm = context.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

        val bluetooth = BluetoothAdapter.getDefaultAdapter()

        val rede = cm.activeNetwork
        val caps = rede?.let {
            cm.getNetworkCapabilities(it)
        }

        val internet =
            caps?.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET
            ) == true

        val validada =
            caps?.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            ) == true

        val celular =
            caps?.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            ) == true

        val wifi =
            caps?.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            ) == true

        val gnss =
            try {
                lm.isProviderEnabled(
                    LocationManager.GPS_PROVIDER
                )
            } catch (e: Exception) {
                false
            }

        return """
📡 RADIO ENVIRONMENT
==============================

📱 DISPOSITIVO
${Build.MANUFACTURER} ${Build.MODEL}
Android ${Build.VERSION.RELEASE}
API ${Build.VERSION.SDK_INT}

🛰️ GNSS
Disponível: $gnss

📡 REDE CELULAR
Transporte ativo: $celular

📶 WI-FI
Transporte ativo: $wifi

🔵 BLUETOOTH
Disponível: ${bluetooth != null}

🌐 INTERNET
Capacidade: $internet
Validada: $validada

🛰️ NTN
Transporte real:
${if (Build.VERSION.SDK_INT >= 35)
    "API moderna disponível"
else
    "API NTN nativa não disponível no Android 10"}

STATUS:
🟡 AMBIENTE DE RÁDIO MAPEADO
""".trimIndent()
    }
}
