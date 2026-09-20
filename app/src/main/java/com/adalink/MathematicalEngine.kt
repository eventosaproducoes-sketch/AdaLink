package com.adalink

enum class AdaRequirementStatus {
    ATENDIDO,
    PARCIALMENTE_ATENDIDO,
    NAO_ATENDIDO,
    NAO_ANALISADO
}

data class AdaMathematicalResult(
    val requisitoId: String,
    val status: AdaRequirementStatus,
    val capacidades: List<String>,
    val justificativa: String
    )
    data class AdaCompatibilityInput(
    val requisitoId: String,
    val capacidadeIds: List<String>
)

data class AdaCompatibilityResult(
    val requisitoId: String,
    val capacidadeIds: List<String>,
    val quantidadeCapacidades: Int,
    val possuiCandidata: Boolean
)


object MathematicalEngine {
    fun avaliarCandidatas(
        entrada: AdaCompatibilityInput
    ): AdaCompatibilityResult {

        val quantidade = entrada.capacidadeIds.size

        return AdaCompatibilityResult(
            requisitoId = entrada.requisitoId,
            capacidadeIds = entrada.capacidadeIds,
            quantidadeCapacidades = quantidade,
            possuiCandidata = quantidade > 0
        )
    }
        fun idsDasCapacidades(
        capacidades: List<AdaCapability>
    ): List<String> {

        return capacidades.map { capacidade ->
            capacidade.id
        }
        }
            fun resumoCapacidades(
        capacidades: List<AdaCapability>
    ): String {

        val ids = idsDasCapacidades(capacidades)

        return buildString {
            append("🧮 ADA MATHEMATICAL ENGINE\n\n")
            append("CAPACIDADES RECEBIDAS: ${ids.size}\n\n")

            ids.forEach { id ->
                append("$id\n")
            }
        }
            }
                fun capacidadesCandidatas(
        requisitoId: String
    ): List<String> {

        return when (requisitoId) {
            "R001" -> listOf("C016")
            "R002" -> listOf("C016")
            "R003" -> listOf("C016")
            "R004" -> listOf("C001", "C013")
            "R005" -> listOf("C012")
            "R006" -> emptyList()
            "R007" -> listOf("C016", "C011")
            "R008" -> listOf("C013")
            "R009" -> listOf("C012")
            "R010" -> emptyList()
            else -> emptyList()
        }
                }
    fun analisar(
            
        requisito: AdaRequirement,
        capacidades: List<AdaCapability>
    ): AdaMathematicalResult {

        if (capacidades.isEmpty()) {
            return AdaMathematicalResult(
                requisitoId = requisito.id,
                status = AdaRequirementStatus.NAO_ANALISADO,
                capacidades = emptyList(),
                justificativa = "Nenhuma capacidade foi fornecida para análise."
            )
        }

        return AdaMathematicalResult(
            requisitoId = requisito.id,
            status = AdaRequirementStatus.NAO_ANALISADO,
            capacidades = capacidades.map { it.id },
            justificativa = "Regra de compatibilidade ainda não definida."
        )
    }

    fun analisarTodos(
        requisitos: List<AdaRequirement>,
        capacidades: List<AdaCapability>
    ): List<AdaMathematicalResult> {

        return requisitos.map { requisito ->
            analisar(requisito, capacidades)
        }
    }

    fun resumo(
        requisitos: List<AdaRequirement>,
        capacidades: List<AdaCapability>
    ): String {

        val resultados = analisarTodos(requisitos, capacidades)

        return buildString {
            append("🧮 ADA MATHEMATICAL ENGINE\n\n")
            append("Requisitos analisados: ${resultados.size}\n")
            append("Capacidades fornecidas: ${capacidades.size}\n\n")

            resultados.forEach { resultado ->
                append("${resultado.requisitoId} → ")
                append("${resultado.status}\n")
                append("${resultado.justificativa}\n\n")
            }
        }
    }
}
