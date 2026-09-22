package com.adalink

import android.content.Context

object AdaGnssPacketInspector {

    fun inspecionar(context: Context): String {

        val pacotes = DataReservoir.listar(context)

        if (pacotes.isEmpty()) {
            return """
                🔎 ADA GNSS PACKET INSPECTOR

                Nenhum pacote encontrado.

                O Reservoir permanece preservado.
            """.trimIndent()
        }

        val ultimos = pacotes.takeLast(5)

        return buildString {

            append("🔎 ADA GNSS PACKET INSPECTOR\n\n")

            append("TOTAL DE PACOTES: ${pacotes.size}\n")
            append("TOTAL DE BYTES: ${pacotes.sumOf { it.size.toLong() }}\n\n")

            append("ÚLTIMOS PACOTES\n")
            append("────────────────────\n\n")

            ultimos.forEachIndexed { indice, pacote ->

                append("PACOTE ${pacotes.size - ultimos.size + indice + 1}\n")

                append("ID: ${pacote.id}\n")
                append("TIMESTAMP: ${pacote.timestamp}\n")
                append("SOURCE: ${pacote.source}\n")
                append("TYPE: ${pacote.type}\n")
                append("SIZE: ${pacote.size} bytes\n")

                append("\nDATA ORIGINAL:\n")

                val dados = pacote.data

                if (dados.length <= 1000) {
                    append(dados)
                } else {
                    append(dados.take(1000))
                    append("\n...[DADOS CONTINUAM]")
                }

                append("\n\n────────────────────\n\n")
            }

            append("🔐 RESERVOIR:\n")
            append("ORIGINAL PRESERVADO\n")
            append("NÃO MODIFICADO\n")
            append("NÃO APAGADO\n")
            append("NÃO TRANSMITIDO\n")
        }
    }
}
