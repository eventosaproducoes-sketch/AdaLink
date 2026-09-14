package com.adalink.rayx

import android.telephony.CellInfoLte
import android.telephony.TelephonyManager

object SatelliteHunter {

    fun texto(tm: TelephonyManager): String {

        val r = StringBuilder()

        r.append("🔎 SATELLITE CELL HUNTER\n\n")

        try {

            val celulas = tm.allCellInfo

            if (celulas == null || celulas.isEmpty()) {
                r.append("Nenhuma célula LTE encontrada.")
                return r.toString()
            }

            var total = 0

            for (cell in celulas) {

                if (cell is CellInfoLte) {

                    total++

                    val id = cell.cellIdentity
                    val sinal = cell.cellSignalStrength

                    r.append("📡 LTE #$total\n")
                    r.append("EARFCN: ${id.earfcn}\n")
                    r.append("PCI: ${id.pci}\n")
                    r.append("CI: ${id.ci}\n")
                    r.append("TAC: ${id.tac}\n")
                    r.append("MCC: ${id.mccString}\n")
                    r.append("MNC: ${id.mncString}\n")
                    r.append("RSRP: ${sinal.rsrp} dBm\n")
                    r.append("RSRQ: ${sinal.rsrq} dB\n")
                    r.append("RSSNR: ${sinal.rssnr}\n")
                    r.append("TA: ${sinal.timingAdvance}\n")
                    r.append("Servidora: ${cell.isRegistered}\n\n")
                }
            }

            r.append("Total LTE: $total\n")
            r.append("\n⚠️ Resultado é observacional.\n")
            r.append("Não confirma NTN sozinho.")

        } catch (e: Exception) {

            r.append("Erro: ${e.javaClass.simpleName}\n")
            r.append(e.message ?: "")
        }

        return r.toString()
    }
}
