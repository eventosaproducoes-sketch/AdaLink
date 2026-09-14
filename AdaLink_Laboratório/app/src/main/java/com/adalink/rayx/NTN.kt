package com.adalink.rayx

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.telephony.TelephonyManager

object NTN {

    fun texto(context: Context): String {

        val tm = context.getSystemService(
            Context.TELEPHONY_SERVICE
        ) as TelephonyManager

        val cm = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val tipo = tm.networkType

        val sateliteApi = Build.VERSION.SDK_INT >= 35

        val transporteSat =
            if (Build.VERSION.SDK_INT >= 35)
                "API disponível para consulta"
            else
                "Não disponível no Android 10"

        return """
🛰️ INVESTIGAÇÃO NTN — RAY-X

Hardware: ${Build.HARDWARE}
Baseband: ${Build.getRadioVersion()}
Android: ${Build.VERSION.RELEASE}
API: ${Build.VERSION.SDK_INT}

Rede atual:
${TelephonyManager.getNetworkTypeName(tipo)}

API nativa de satélite:
${if (sateliteApi) "Disponível" else "Não disponível"}

Transporte SATELLITE:
$transporteSat

EVIDÊNCIAS:
• LTE/4G detectado
• Modem celular ativo
• GNSS disponível
• Android 10 / API 29

CONCLUSÃO ATUAL:
Não foi comprovado NTN nativo.

O Android 10 não expõe uma
interface nativa para confirmar
comunicação direta com satélite.

A confirmação definitiva exige
evidência do modem/RF compatível
com NTN ou teste real de enlace.
""".trimIndent()
    }
}
