package com.adalink.rayx

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object ConnectivityScanner {

    fun texto(context: Context): String {

        val cm = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val n = cm.activeNetwork
        val c = cm.getNetworkCapabilities(n)

        if (c == null)
            return "🌐 CONNECTIVITY\n\nNenhuma rede ativa"

        val celular = c.hasTransport(
            NetworkCapabilities.TRANSPORT_CELLULAR
        )

        val wifi = c.hasTransport(
            NetworkCapabilities.TRANSPORT_WIFI
        )

        return """
🌐 CONNECTIVITY SCANNER

Internet: ${
            c.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET
            )
        }

Validada: ${
            c.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            )
        }

Celular: $celular
Wi-Fi: $wifi

Download:
${c.linkDownstreamBandwidthKbps} Kbps

Upload:
${c.linkUpstreamBandwidthKbps} Kbps
""".trimIndent()
    }
}
