package com.adalink

import android.content.Context

object PacketEngine {

    data class Resultado(
        val pacotes: Int,
        val bytes: Long,
        val fontes: Int,
        val tipos: Int
    )

    fun analisar(context: Context): Resultado {

        val pacotes = DataReservoir.listar(context)

        val fontes = pacotes
            .map { it.source }
            .distinct()
            .size

        val tipos = pacotes
            .map { it.type }
            .distinct()
            .size

        val bytes = pacotes.sumOf {
            it.size.toLong()
        }

        return Resultado(
            pacotes = pacotes.size,
            bytes = bytes,
            fontes = fontes,
            tipos = tipos
        )
    }

    fun resumo(context: Context): String {

        val r = analisar(context)

        return """
            ⚙️ ADA PACKET ENGINE

            Pacotes preservados: ${r.pacotes}
            Dados preservados: ${r.bytes} bytes
            Fontes identificadas: ${r.fontes}
            Tipos identificados: ${r.tipos}

            🔐 RESERVOIR:
            PRESERVADO
            NÃO CONSUMIDO
            NÃO APAGADO
            NÃO TRANSMITIDO
        """.trimIndent()
    }
}
