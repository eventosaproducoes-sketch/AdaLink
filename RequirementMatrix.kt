package com.adalink

data class AdaRequirementMapping(
    val requisitoId: String,
    val capacidadeIds: List<String>,
    val estado: String
)

object RequirementMatrix {

    fun criar(): List<AdaRequirementMapping> {
        return listOf(
            AdaRequirementMapping("R001", emptyList(), "NÃO ANALISADO"),
            AdaRequirementMapping("R002", emptyList(), "NÃO ANALISADO"),
            AdaRequirementMapping("R003", emptyList(), "NÃO ANALISADO"),
            AdaRequirementMapping("R004", emptyList(), "NÃO ANALISADO"),
            AdaRequirementMapping("R005", emptyList(), "NÃO ANALISADO"),
            AdaRequirementMapping("R006", emptyList(), "NÃO ANALISADO"),
            AdaRequirementMapping("R007", emptyList(), "NÃO ANALISADO"),
            AdaRequirementMapping("R008", emptyList(), "NÃO ANALISADO"),
            AdaRequirementMapping("R009", emptyList(), "NÃO ANALISADO"),
            AdaRequirementMapping("R010", emptyList(), "NÃO ANALISADO")
        )
    }

    fun resumo(): String {
        val matriz = criar()

        return buildString {
            append("🧩 ADA REQUIREMENT MATRIX\n\n")
            append("FUNÇÃO: X001 — REDE IP INTERNA ADA LINK\n\n")

            matriz.forEach { item ->
                append("${item.requisitoId} → ${item.estado}\n")
            }

            append("\nCAPACIDADES AINDA NÃO ASSOCIADAS.")
        }
    }
}
