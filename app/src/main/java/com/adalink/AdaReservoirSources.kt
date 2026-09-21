package com.adalink

import android.content.Context

object AdaReservoirSources {

    fun analisar(context: Context): String {

        val pacotes = DataReservoir.listar(context)

        if (pacotes.isEmpty()) {
            return """
                🔎 ADA RESERVOIR SOURCES

                Nenhum pacote encontrado.
            """.trimIndent()
        }

        val porFonte = pacotes
            .groupBy { it.source }
            .toSortedMap()

        val porTipo = pacotes
            .groupBy { it.type }
            .toSortedMap()

        return buildString {

            append("🔎 ADA RESERVOIR SOURCES\n\n")

            append("TOTAL DE PACOTES: ${pacotes.size}\n")
            append("TOTAL DE BYTES: ${pacotes.sumOf { it.size.toLong() }}\n\n")

            append("📡 POR FONTE\n\n")

            porFonte.forEach { (fonte, lista) ->

                val bytes = lista.sumOf { it.size.toLong() }

                append("$fonte\n")
                append("  Pacotes: ${lista.size}\n")
                append("  Bytes: $bytes\n\n")
            }

            append("🧩 POR TIPO\n\n")

            porTipo.forEach { (tipo, lista) ->

                val bytes = lista.sumOf { it.size.toLong() }

                append("$tipo\n")
                append("  Pacotes: ${lista.size}\n")
                append("  Bytes: $bytes\n\n")
            }
        }
    }
}
