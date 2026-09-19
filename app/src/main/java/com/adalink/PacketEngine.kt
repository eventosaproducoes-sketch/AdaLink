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
    private fun formatarDados(bytes: Long): String {

        val unidades = arrayOf(
            "B",
            "KB",
            "MB",
            "GB",
            "TB",
            "PB",
            "EB"
        )

        var valor = bytes.toDouble()
        var indice = 0

        while (valor >= 1000.0 && indice < unidades.lastIndex) {
            valor /= 1000.0
            indice++
        }

        val bits = bytes.toDouble() * 8.0

        var valorBits = bits
        var indiceBits = 0

        while (
            valorBits >= 1000.0 &&
            indiceBits < unidades.lastIndex
        ) {
            valorBits /= 1000.0
            indiceBits++
        }

        return "%.2f %s\n%.2f %sb".format(
            valor,
            unidades[indice],
            valorBits,
            unidades[indiceBits]
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
