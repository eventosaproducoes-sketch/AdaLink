package com.adalink.rayx

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object Conectividade {

    fun texto(context: Context): String {

        val r = StringBuilder()

        r.append("🌐 CONECTIVIDADE\n\n")

        try {
            val cm = context.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager

            val n = cm.activeNetwork
            val caps = cm.getNetworkCapabilities(n)

            if (caps == null) {
                r.append("Rede ativa: nenhuma\n")
                return r.toString()
            }

            r.append("Internet: ")
            r.append(
                caps.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                )
            )
            r.append("\n")

            r.append("Validada: ")
            r.append(
                caps.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_VALIDATED
                )
            )
            r.append("\n")

            r.append("Wi-Fi: ")
            r.append(
                caps.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI
                )
            )
            r.append("\n")

            r.append("Celular: ")
            r.append(
                caps.hasTransport(
                    NetworkCapabilities.TRANSPORT_CELLULAR
                )
            )
            r.append("\n")

            r.append("VPN: ")
            r.append(
                caps.hasTransport(
                    NetworkCapabilities.TRANSPORT_VPN
                )
            )
            r.append("\n")

            val lp = cm.getLinkProperties(n)

            if (lp != null) {
                r.append("\nInterface: ${lp.interfaceName}\n")
                r.append("Endereços: ${lp.linkAddresses}\n")
                r.append("DNS: ${lp.dnsServers}\n")
                r.append("Rotas: ${lp.routes}\n")
            }

        } catch (e: Exception) {
            r.append("Erro: ${e.javaClass.simpleName}\n")
        }

        return r.toString()
    }
}
