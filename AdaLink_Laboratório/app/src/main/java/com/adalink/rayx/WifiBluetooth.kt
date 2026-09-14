package com.adalink.rayx

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.net.wifi.WifiManager

object WifiBluetooth {

    fun texto(context: Context): String {

        val r = StringBuilder()

        r.append("📶 WI-FI\n\n")

        try {
            val wm = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager

            val wi = wm.connectionInfo

            r.append("Wi-Fi ligado: ${wm.isWifiEnabled}\n")
            r.append("SSID: ${wi.ssid}\n")
            r.append("RSSI: ${wi.rssi} dBm\n")
            r.append("Velocidade: ${wi.linkSpeed} Mbps\n")

        } catch (e: Exception) {
            r.append("Wi-Fi: não disponível\n")
        }

        r.append("\n🔵 BLUETOOTH\n\n")

        try {
            val bt = BluetoothAdapter.getDefaultAdapter()

            if (bt == null) {
                r.append("Bluetooth: não disponível\n")
            } else {
                r.append("Bluetooth: disponível\n")
                r.append("Ligado: ${bt.isEnabled}\n")
            }

        } catch (e: Exception) {
            r.append("Bluetooth: acesso limitado\n")
        }

        return r.toString()
    }
}
