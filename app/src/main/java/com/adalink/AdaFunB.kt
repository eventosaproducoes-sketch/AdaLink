package com.adalink

data class AdaFunBResult(
    val funcaoId: String,
    val requisitoId: String,
    val nomeFuncao: String,
    val capacidadesUsadas: List<String>,
    val capacidadesAusentes: List<String>,
    val pacotesProcessados: Int,
    val bytesProcessados: Long,
    val dadosOriginaisPreservados: Boolean,
    val composicaoExecutada: Boolean,
    val validacaoInicial: Boolean,
    val descricao: String
)

object AdaFunB {

    fun construir(
        context: android.content.Context,
        requisitoId: String
    ): AdaFunBResult {

        val capacidades =
            CapabilityMap.mapear(context)

        val fusao =
            AdaFusionEngine.compor(
                requisitoId,
                capacidades
            )

        if (!fusao.composicaoPossivel) {

            return AdaFunBResult(
                funcaoId = "F001",
                requisitoId = requisitoId,
                nomeFuncao = "PROCESSADOR COMPOSTO DE PACOTES",
                capacidadesUsadas =
                    fusao.capacidadesDisponiveis,
                capacidadesAusentes =
                    fusao.capacidadesAusentes,
                pacotesProcessados = 0,
                bytesProcessados = 0,
                dadosOriginaisPreservados = true,
                composicaoExecutada = false,
                validacaoInicial = false,
                descricao =
                    "A função não foi construída porque as capacidades necessárias não estão integralmente disponíveis."
            )
        }

        val analise =
            PacketEngine.analisar(context)

        return AdaFunBResult(
            funcaoId = "F001",
            requisitoId = requisitoId,
            nomeFuncao = "PROCESSADOR COMPOSTO DE PACOTES",
            capacidadesUsadas =
                fusao.capacidadesDisponiveis,
            capacidadesAusentes =
                fusao.capacidadesAusentes,
            pacotesProcessados =
                analise.pacotes,
            bytesProcessados =
                analise.bytes,
            dadosOriginaisPreservados = true,
            composicaoExecutada = true,
            validacaoInicial = true,
            descricao =
                "Função composta construída utilizando as capacidades disponíveis. " +
                "O processamento ocorre por leitura dos dados preservados. " +
                "Nenhum registro original é apagado ou consumido."
        )
    }

    fun resumo(
        context: android.content.Context,
        requisitoId: String
    ): String {

        val resultado =
            construir(
                context,
                requisitoId
            )

        return buildString {

            append("⚙️ ADAFUNB\n\n")

            append("FUNÇÃO: ")
            append(resultado.funcaoId)
            append("\n")

            append("NOME: ")
            append(resultado.nomeFuncao)
            append("\n\n")

            append("REQUISITO: ")
            append(resultado.requisitoId)
            append("\n\n")

            append("CAPACIDADES UTILIZADAS:\n")

            if (resultado.capacidadesUsadas.isEmpty()) {

                append("NENHUMA\n")

            } else {

                resultado.capacidadesUsadas.forEach {
                    append("$it\n")
                }
            }

            append("\n")

            append("CAPACIDADES AUSENTES:\n")

            if (resultado.capacidadesAusentes.isEmpty()) {

                append("NENHUMA\n")

            } else {

                resultado.capacidadesAusentes.forEach {
                    append("$it\n")
                }
            }

            append("\n")

            append("COMPOSIÇÃO EXECUTADA: ")

            append(
                if (resultado.composicaoExecutada)
                    "SIM"
                else
                    "NÃO"
            )

            append("\n")

            append("VALIDAÇÃO INICIAL: ")

            append(
                if (resultado.validacaoInicial)
                    "SIM"
                else
                    "NÃO"
            )

            append("\n\n")

            append("PACOTES PROCESSADOS: ")
            append(resultado.pacotesProcessados)
            append("\n")

            append("BYTES PROCESSADOS: ")
            append(resultado.bytesProcessados)
            append("\n\n")

            append("DADOS ORIGINAIS PRESERVADOS: ")

            append(
                if (resultado.dadosOriginaisPreservados)
                    "SIM"
                else
                    "NÃO"
            )

            append("\n\n")

            append(resultado.descricao)
        }
    }
}
