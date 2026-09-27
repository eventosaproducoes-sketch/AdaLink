package SEU_PACOTE

enum class AdaTestStatus {
    PASS,
    FAIL,
    UNTESTED,
    ERROR
}

data class AdaTestResult(
    val id: String,
    val nome: String,
    val status: AdaTestStatus,
    val evidencia: String,
    val detalhes: String = ""
)

data class AdaConnectivityReport(
    val inicioMs: Long,
    val fimMs: Long,
    val resultados: List<AdaTestResult>,
    val tunCriado: Boolean,
    val ipInterno: String?,
    val pacotesGerados: Int,
    val pacotesRecebidos: Int,
    val pacotesProcessados: Int,
    val pacotesReinjetados: Int,
    val bytesRecebidos: Long,
    val bytesProcessados: Long,
    val bytesReinjetados: Long,
    val encerramentoSeguro: Boolean
) {

    fun duracaoMs(): Long {
        return fimMs - inicioMs
    }

    fun aprovados(): Int {
        return resultados.count { it.status == AdaTestStatus.PASS }
    }

    fun falhas(): Int {
        return resultados.count { it.status == AdaTestStatus.FAIL }
    }

    fun pendentes(): Int {
        return resultados.count {
            it.status == AdaTestStatus.UNTESTED
        }
    }

    fun texto(): String {
        val sb = StringBuilder()

        sb.appendLine("════════════════════════════════")
        sb.appendLine("      ADALINK CONNECTIVITY LAB")
        sb.appendLine("════════════════════════════════")
        sb.appendLine()

        sb.appendLine("DURAÇÃO: ${duracaoMs()} ms")
        sb.appendLine()

        for (resultado in resultados) {

            val simbolo = when (resultado.status) {
                AdaTestStatus.PASS -> "[OK]"
                AdaTestStatus.FAIL -> "[FALHA]"
                AdaTestStatus.UNTESTED -> "[PENDENTE]"
                AdaTestStatus.ERROR -> "[ERRO]"
            }

            sb.appendLine(
                "$simbolo ${resultado.id} — ${resultado.nome}"
            )

            sb.appendLine(
                "   Evidência: ${resultado.evidencia}"
            )

            if (resultado.detalhes.isNotBlank()) {
                sb.appendLine(
                    "   ${resultado.detalhes}"
                )
            }

            sb.appendLine()
        }

        sb.appendLine("──────── MÉTRICAS ────────")

        sb.appendLine(
            "TUN criada: $tunCriado"
        )

        sb.appendLine(
            "IP interno: ${ipInterno ?: "NÃO DEFINIDO"}"
        )

        sb.appendLine(
            "Pacotes gerados: $pacotesGerados"
        )

        sb.appendLine(
            "Pacotes recebidos: $pacotesRecebidos"
        )

        sb.appendLine(
            "Pacotes processados: $pacotesProcessados"
        )

        sb.appendLine(
            "Pacotes reinjetados: $pacotesReinjetados"
        )

        sb.appendLine(
            "Bytes recebidos: $bytesRecebidos"
        )

        sb.appendLine(
            "Bytes processados: $bytesProcessados"
        )

        sb.appendLine(
            "Bytes reinjetados: $bytesReinjetados"
        )

        sb.appendLine(
            "Encerramento seguro: $encerramentoSeguro"
        )

        sb.appendLine()
        sb.appendLine("──────── RESULTADO ────────")

        sb.appendLine(
            "Testes OK: ${aprovados()}"
        )

        sb.appendLine(
            "Falhas: ${falhas()}"
        )

        sb.appendLine(
            "Pendentes: ${pendentes()}"
        )

        sb.appendLine()

        if (falhas() == 0 && pendentes() == 0) {
            sb.appendLine("LABORATÓRIO: TODOS OS TESTES CONCLUÍDOS")
        } else {
            sb.appendLine(
                "LABORATÓRIO: EXISTEM TESTES QUE AINDA PRECISAM DE EVIDÊNCIA"
            )
        }

        sb.appendLine()
        sb.appendLine("════════════════════════════════")

        return sb.toString()
    }
}
