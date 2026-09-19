package com.adalink

data class AdaRequirement(
    val id: String,
    val nome: String,
    val descricao: String,
    val obrigatorio: Boolean
)

object RequirementModel {

    fun requisitos(): List<AdaRequirement> {
        return listOf(
            AdaRequirement("R001", "Interface virtual", "Possuir uma interface de comunicação virtual.", true),
            AdaRequirement("R002", "Endereço IP interno", "Possuir um endereço IP para comunicação interna.", true),
            AdaRequirement("R003", "Receber pacotes", "Ser capaz de receber pacotes destinados à interface.", true),
            AdaRequirement("R004", "Processar pacotes", "Processar os pacotes recebidos sem destruir os dados originais.", true),
            AdaRequirement("R005", "Preservar dados", "Manter os dados originais preservados.", true),
            AdaRequirement("R006", "Gerar pacote de teste", "Gerar pacotes controlados para validar a função.", true),
            AdaRequirement("R007", "Reinjetar/encaminhar pacote", "Permitir reinjeção ou encaminhamento de pacotes.", true),
            AdaRequirement("R008", "Medir o funcionamento", "Registrar métricas para validar o funcionamento.", true),
            AdaRequirement("R009", "Não apagar dados originais", "Impedir que a função consuma ou apague os dados preservados.", true),
            AdaRequirement("R010", "Encerrar com segurança", "Permitir encerramento seguro da função.", true)
        )
    }

    fun quantidade(): Int {
        return requisitos().size
    }

    fun resumo(): String {
        return buildString {
            append("🧠 ADA REQUIREMENT MODEL\n\n")
            append("FUNÇÃO: X001 — REDE IP INTERNA ADA LINK\n\n")
            append("Requisitos: ${requisitos().size}\n\n")

            requisitos().forEach { requisito ->
                append("${requisito.id} — ${requisito.nome}\n")
            }
        }
    }
}

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
