package com.adalink

data class AdaCorResult(
    val requisitoId: String,
    val capacidadesMapeadas: Int,
    val capacidadesDisponiveis: List<String>,
    val capacidadesAusentes: List<String>,
    val composicaoPossivel: Boolean,
    val pacotesPreservados: Int,
    val bytesPreservados: Long,
    val descricao: String
)

object AdaCor {

    fun executar(
        context: android.content.Context,
        requisitoId: String
    ): AdaCorResult {

        val capacidades =
            CapabilityMap.mapear(context)

        val requisito =
            RequirementModel
                .requisitos()
                .firstOrNull { it.id == requisitoId }

        if (requisito == null) {
            return AdaCorResult(
                requisitoId = requisitoId,
                capacidadesMapeadas = capacidades.size,
                capacidadesDisponiveis = emptyList(),
                capacidadesAusentes = emptyList(),
                composicaoPossivel = false,
                pacotesPreservados =
                    DataReservoir.quantidade(context),
                bytesPreservados =
                    DataReservoir.tamanhoTotal(context),
                descricao = "Requisito não encontrado."
            )
        }

        val candidatas =
            MathematicalEngine
                .capacidadesCandidatas(requisito.id)

        val idsDisponiveis =
            capacidades.map { it.id }

        val disponiveis =
            candidatas.filter {
                it in idsDisponiveis
            }

        val ausentes =
            candidatas.filter {
                it !in idsDisponiveis
            }

        val fusao =
            AdaFusionEngine.compor(
                requisito.id,
                capacidades
            )

        val pacotes =
            DataReservoir.quantidade(context)

        val bytes =
            DataReservoir.tamanhoTotal(context)

        return AdaCorResult(
            requisitoId = requisito.id,
            capacidadesMapeadas = capacidades.size,
            capacidadesDisponiveis = disponiveis,
            capacidadesAusentes = ausentes,
            composicaoPossivel =
                fusao.composicaoPossivel,
            pacotesPreservados = pacotes,
            bytesPreservados = bytes,
            descricao = fusao.descricao
        )
    }

    fun resumo(
        context: android.content.Context,
        requisitoId: String
    ): String {

        val resultado =
            executar(
                context,
                requisitoId
            )

        return buildString {

            append("🧠 ADACOR\n\n")

            append("REQUISITO: ")
            append(resultado.requisitoId)
            append("\n\n")

            append("CAPACIDADES MAPEADAS: ")
            append(resultado.capacidadesMapeadas)
            append("\n\n")

            append("CAPACIDADES DISPONÍVEIS:\n")

            if (resultado.capacidadesDisponiveis.isEmpty()) {
                append("NENHUMA\n")
            } else {
                resultado.capacidadesDisponiveis.forEach {
                    append("$it\n")
                }
            }

            append("\nCAPACIDADES AUSENTES:\n")

            if (resultado.capacidadesAusentes.isEmpty()) {
                append("NENHUMA\n")
            } else {
                resultado.capacidadesAusentes.forEach {
                    append("$it\n")
                }
            }

            append("\nCOMPOSIÇÃO POSSÍVEL: ")

            append(
                if (resultado.composicaoPossivel)
                    "SIM"
                else
                    "NÃO"
            )

            append("\n\n")

            append("DADOS PRESERVADOS:\n")
            append("Pacotes: ")
            append(resultado.pacotesPreservados)
            append("\n")

            append("Bytes: ")
            append(resultado.bytesPreservados)
            append("\n\n")

            append(resultado.descricao)
        }
    }
}
