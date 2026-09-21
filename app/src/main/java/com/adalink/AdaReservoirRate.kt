package com.adalink

import android.content.Context

object AdaReservoirRate {

    private var pacotesAnterior = 0
    private var bytesAnterior = 0L
    private var tempoAnterior = 0L

    fun iniciar(context: Context) {
        pacotesAnterior = DataReservoir.quantidade(context)
        bytesAnterior = DataReservoir.tamanhoTotal(context)
        tempoAnterior = System.currentTimeMillis()
    }

    fun medir(context: Context): String {
        val agora = System.currentTimeMillis()

        val pacotesAtuais = DataReservoir.quantidade(context)
        val bytesAtuais = DataReservoir.tamanhoTotal(context)

        if (tempoAnterior == 0L) {
            iniciar(context)
            return """
                📊 ADA RESERVOIR RATE

                Estado inicial registrado.

                Pacotes: $pacotesAtuais
                Bytes: $bytesAtuais
            """.trimIndent()
        }

        val novosPacotes = pacotesAtuais - pacotesAnterior
        val novosBytes = bytesAtuais - bytesAnterior

        val intervaloMs = agora - tempoAnterior
        val intervaloMinutos = intervaloMs / 60000.0

        val pacotesPorMinuto =
            if (intervaloMinutos > 0) {
                novosPacotes / intervaloMinutos
            } else {
                0.0
            }

        val bytesPorMinuto =
            if (intervaloMinutos > 0) {
                novosBytes / intervaloMinutos
            } else {
                0.0
            }

        pacotesAnterior = pacotesAtuais
        bytesAnterior = bytesAtuais
        tempoAnterior = agora

        return """
            📊 ADA RESERVOIR RATE

            Pacotes atuais: $pacotesAtuais
            Bytes atuais: $bytesAtuais

            Novos pacotes: $novosPacotes
            Novos bytes: $novosBytes

            Intervalo: %.1f minutos

            Taxa:
            %.2f pacotes/min
            %.2f bytes/min
        """.trimIndent().format(
            intervaloMinutos,
            pacotesPorMinuto,
            bytesPorMinuto
        )
    }
}
