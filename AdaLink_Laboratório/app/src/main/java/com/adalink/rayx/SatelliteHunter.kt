package com.adalink.rayx

import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoWcdma
import android.telephony.TelephonyManager

object SatelliteHunter {

    fun texto(tm: TelephonyManager): String {

        val r = StringBuilder()

        r.append("🔎 SATELLITE CELL HUNTER\n\n")

        try {

            val celulas = tm.allCellInfo

            if (celulas.isNullOrEmpty()) {
                r.append("Nenhuma célula encontrada.")
                return r.toString()
            }

            var total = 0

            for (cell in celulas) {

                when (cell) {

                    is CellInfoLte -> {

                        total++

                        val id = cell.cellIdentity
                        val sinal = cell.cellSignalStrength

                        r.append("📡 LTE / 4G #$total\n")
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

                    is CellInfoWcdma -> {

                        total++

                        val id = cell.cellIdentity
                        val sinal = cell.cellSignalStrength

                        r.append("📡 WCDMA / 3G #$total\n")
                        r.append("CID: ${id.cid}\n")
                        r.append("LAC: ${id.lac}\n")
                        r.append("PSC: ${id.psc}\n")
                        r.append("UARFCN: ${id.uarfcn}\n")
                        r.append("MCC: ${id.mccString}\n")
                        r.append("MNC: ${id.mncString}\n")
                        r.append("Sinal: ${sinal.dbm} dBm\n")
                        r.append("Servidora: ${cell.isRegistered}\n\n")
                    }

                    is CellInfoGsm -> {

                        total++

                        val id = cell.cellIdentity
                        val sinal = cell.cellSignalStrength

                        r.append("📡 GSM / 2G #$total\n")
                        r.append("CID: ${id.cid}\n")
                        r.append("LAC: ${id.lac}\n")
                        r.append("ARFCN: ${id.arfcn}\n")
                        r.append("BSIC: ${id.bsic}\n")
                        r.append("MCC: ${id.mccString}\n")
                        r.append("MNC: ${id.mncString}\n")
                        r.append("Sinal: ${sinal.dbm} dBm\n")
                        r.append("Servidora: ${cell.isRegistered}\n\n")
                    }
                }
            }

            r.append("==============================\n")
            r.append("TOTAL DE CÉLULAS: $total\n\n")

            r.append("⚠️ RESULTADO OBSERVACIONAL\n")
            r.append("As células identificadas são\n")
            r.append("redes terrestres do aparelho.\n\n")

            r.append("🛰️ NTN\n")
            r.append("A detecção de uma célula celular\n")
            r.append("não confirma comunicação via satélite.\n")

        } catch (e: SecurityException) {

            r.append("🔒 Acesso às células bloqueado.\n")
            r.append("Permissão de telefonia não disponível.")

        } catch (e: Exception) {

            r.append("Erro: ${e.javaClass.simpleName}\n")
            r.append(e.message ?: "")
        }

        return r.toString()
    }
}
