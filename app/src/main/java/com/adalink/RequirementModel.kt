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
            AdaRequirement(
                id = "R001",
                nome = "Interface virtual",
                descricao = "Possuir uma interface de comunicação virtual.",
                obrigatorio = true
            ),

            AdaRequirement(
                id = "R002",
                nome = "Endereço IP interno",
                descricao = "Possuir um endereço IP para comunicação interna.",
                obrigatorio = true
            ),

            AdaRequirement(
                id = "R003",
                nome = "Receber pacotes",
                descricao = "Ser capaz de receber pacotes destinados à interface.",
                obrigatorio = true
            ),

            AdaRequirement(
                id = "R004",
                nome = "Processar pacotes",
                descricao = "Processar os pacotes recebidos sem destruir os dados originais.",
                obrigatorio = true
            ),

            AdaRequirement(
                id = "R005",
                nome = "Preservar dados",
                descricao = "Manter os dados originais preservados.",
                obrigatorio = true
            ),

            AdaRequirement(
                id = "R006",
                nome = "Gerar pacote de teste",
                descricao = "Gerar pacotes controlados para validar a função.",
                obrigatorio = true
            ),

            AdaRequirement(
                id = "R007",
                nome = "Reinjetar/encaminhar pacote",
                descricao = "Permitir reinjeção ou encaminhamento de pacotes.",
                obrigatorio = true
            ),

            AdaRequirement(
                id = "R008",
                nome = "Medir o funcionamento",
                descricao = "Registrar métricas para validar o funcionamento.",
                obrigatorio = true
            ),

            AdaRequirement(
                id = "R009",
                nome = "Não apagar dados originais",
                descricao = "Impedir que a função consuma ou apague os dados preservados.",
                obrigatorio = true
            ),

            AdaRequirement(
                id = "R010",
                nome = "Encerrar com segurança",
                descricao = "Permitir encerramento seguro da função.",
                obrigatorio = true
            )
        )
    }

    fun quantidade(): Int {
        return requisitos().size
    }

    fun resumo(): String {
        val lista = requisitos()

        return buildString {
            append("🧠 ADA REQUIREMENT MODEL\n\n")
            append("FUNÇÃO: X001 — REDE IP INTERNA ADA LINK\n\n")
            append("Requisitos: ${lista.size}\n\n")

            lista.forEach { requisito ->
                append("${requisito.id} — ${requisito.nome}\n")
            }
        }
    }
}
