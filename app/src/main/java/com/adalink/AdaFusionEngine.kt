package com.adalink

data class AdaFusionResult(
    val requisitoId: String,
    val capacidadesDisponiveis: List<String>,
    val capacidadesAusentes: List<String>,
    val composicaoPossivel: Boolean,
    val descricao: String
)

object AdaFusionEngine {

    fun compor(
        requisitoId: String,
        capacidades: List<AdaCapability>
    ): AdaFusionResult {

        val candidatas =
            MathematicalEngine.capacidadesCandidatas(requisitoId)

        val disponiveis = capacidades
            .map { it.id }
            .filter { id -> candidatas.contains(id) }

        val ausentes = candidatas
            .filter { id -> !disponiveis.contains(id) }

        val possivel =
            candidatas.isNotEmpty() &&
            ausentes.isEmpty()

        val descricao =
            if (possivel) {
                "Todas as capacidades candidatas estão disponíveis para composição."
            } else if (disponiveis.isNotEmpty()) {
                "Parte das capacidades candidatas está disponível. Composição ainda incompleta."
            } else {
                "Nenhuma capacidade candidata está disponível."
            }

        return AdaFusionResult(
            requisitoId = requisitoId,
            capacidadesDisponiveis = disponiveis,
            capacidadesAusentes = ausentes,
            composicaoPossivel = possivel,
            descricao = descricao
        )
    }

    fun resumo(
        requisitoId: String,
        capacidades: List<AdaCapability>
    ): String {

        val resultado = compor(
            requisitoId,
            capacidades
        )

        return buildString {
            append("🔗 ADA FUSION ENGINE\n\n")
            append("REQUISITO: ${resultado.requisitoId}\n\n")

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
            append(resultado.descricao)
        }
    }
}
