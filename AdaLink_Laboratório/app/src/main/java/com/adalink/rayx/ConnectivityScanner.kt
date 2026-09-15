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

        return """
🌐 CONNECTIVITY SCANNER

Internet: ${
            c.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET
            )
        }

Celular: ${
            c.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            )
        }

Wi-Fi: ${
            c.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            )
        }

Download:
${c.linkDownstreamBandwidthKbps} Kbps

Upload:
${c.linkUpstreamBandwidthKbps} Kbps
""".trimIndent()
    }
}
