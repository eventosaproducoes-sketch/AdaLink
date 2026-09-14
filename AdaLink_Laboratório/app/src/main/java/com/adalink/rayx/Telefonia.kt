package com.adalink.rayx

import android.telephony.*

object Telefonia {

    fun texto(tm: TelephonyManager): String {

        val r = StringBuilder()

        r.append("📡 TELEFONIA\n\n")

        try {
            r.append("Operadora: ${tm.networkOperatorName}\n")
            r.append("SIMs: ${tm.phoneCount}\n")
            r.append("Rede: ${tipo(tm.dataNetworkType)}\n\n")

            val celulas = tm.allCellInfo

            if (celulas.isNullOrEmpty()) {
                r.append("Células: não disponíveis agora\n")
                return r.toString()
            }

            r.append("Células detectadas: ${celulas.size}\n")

            celulas.forEachIndexed { i, c ->

                r.append("\nCélula ${i + 1}: ")

                when (c) {

                    is CellInfoLte -> {
                        r.append("LTE/4G\n")
                        r.append("CI: ${c.cellIdentity.ci}\n")
                        r.append("TAC: ${c.cellIdentity.tac}\n")
                        r.append("PCI: ${c.cellIdentity.pci}\n")
                        r.append("EARFCN: ${c.cellIdentity.earfcn}\n")
                        r.append("dBm: ${c.cellSignalStrength.dbm}\n")
                    }

                    is CellInfoWcdma -> {
                        r.append("WCDMA/3G\n")
                        r.append("CID: ${c.cellIdentity.cid}\n")
                        r.append("LAC: ${c.cellIdentity.lac}\n")
                        r.append("PSC: ${c.cellIdentity.psc}\n")
                        r.append("UARFCN: ${c.cellIdentity.uarfcn}\n")
                        r.append("dBm: ${c.cellSignalStrength.dbm}\n")
                    }

                    is CellInfoGsm -> {
                        r.append("GSM/2G\n")
                        r.append("CID: ${c.cellIdentity.cid}\n")
                        r.append("LAC: ${c.cellIdentity.lac}\n")
                        r.append("ARFCN: ${c.cellIdentity.arfcn}\n")
                        r.append("dBm: ${c.cellSignalStrength.dbm}\n")
                    }

                    is CellInfoCdma -> {
                        r.append("CDMA\n")
                        r.append("dBm: ${c.cellSignalStrength.dbm}\n")
                    }

                    else -> r.append("Outro tipo\n")
                }
            }

        } catch (e: Exception) {
            r.append("Erro: ${e.javaClass.simpleName}\n")
        }

        return r.toString()
    }

    private fun tipo(t: Int): String {
        return when (t) {
            TelephonyManager.NETWORK_TYPE_GSM -> "GSM/2G"
            TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS/2G"
            TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE/2G"
            TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS/3G"
            TelephonyManager.NETWORK_TYPE_HSDPA -> "HSDPA/3G"
            TelephonyManager.NETWORK_TYPE_HSUPA -> "HSUPA/3G"
            TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA/3G"
            TelephonyManager.NETWORK_TYPE_LTE -> "LTE/4G"
            TelephonyManager.NETWORK_TYPE_NR -> "NR/5G"
            else -> "Tipo $t"
        }
    }
}
