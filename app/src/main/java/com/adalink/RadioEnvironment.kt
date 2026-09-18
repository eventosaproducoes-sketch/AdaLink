package com.adalink

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object RadioEnvironment {

    fun analisar(context: Context): String {
        val r = StringBuilder()

        r.append("📡 RADIO ENVIRONMENT\n\n")
        r.append("Android: ${Build.VERSION.RELEASE}\n")
        r.append("API: ${Build.VERSION.SDK_INT}\n\n")

        val cm = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val network = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(network)

        r.append("🌐 CONECTIVIDADE\n")

        if (capabilities == null) {
            r.append("Estado: SEM REDE ATIVA\n")
        } else {
            r.append("Estado: REDE ATIVA\n")

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                r.append("Celular: ATIVO\n")
            }

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                r.append("Wi-Fi: ATIVO\n")
            }

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
                r.append("Bluetooth: ATIVO\n")
            }

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                r.append("Ethernet: ATIVO\n")
            }

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                r.append("VPN: ATIVO\n")
            }

            if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                r.append("Internet: DISPONÍVEL\n")
            } else {
                r.append("Internet: NÃO CONFIRMADA\n")
            }
        }

        return r.toString()
    }
}
