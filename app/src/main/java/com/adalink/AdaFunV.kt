package com.adalink

data class AdaFunVResult(
    val funcaoId: String,
    val requisitoId: String,
    val funcaoConstruida: Boolean,
    val composicaoExecutada: Boolean,
    val processamentoExecutado: Boolean,
    val capacidadesPresentes: Boolean,
    val dadosOriginaisPreservados: Boolean,
    val quantidadePreservada: Boolean,
    val bytesPreservados: Boolean,
    val pacotesAntes: Int,
    val pacotesDepois: Int,
    val bytesAntes: Long,
    val bytesDepois: Long,
    val pacotesProcessados: Int,
    val bytesProcessados: Long,
    val capacidadesUsadas: List<String>,
    val capacidadesAusentes: List<String>,
    val validacaoAprovada: Boolean,
    val descricao: String
)

object AdaFunV {

    fun validar(
        context: android.content.Context,
        requisitoId: String
    ): AdaFunVResult {

        return try {

            /*
             * ============================================================
             * 1. CAPTURA DO ESTADO ORIGINAL
             * ============================================================
             *
             * O AdaFunV primeiro lê o DataReservoir.
             *
             * Nenhum dado é apagado ou alterado.
             */

            val pacotesAntes =
                DataReservoir.listar(context)

            val quantidadeAntes =
                pacotesAntes.size

            val bytesAntes =
                pacotesAntes.sumOf {
                    it.size.toLong()
                }


            /*
             * ============================================================
             * 2. CONSTRUÇÃO DA FUNÇÃO
             * ============================================================
             *
             * AdaFunV não constrói a função diretamente.
             *
             * Ele solicita ao AdaFunB que construa a função.
             */

            val funcao =
                AdaFunB.construir(
                    context,
                    requisitoId
                )


            /*
             * ============================================================
             * 3. VERIFICAÇÃO DA COMPOSIÇÃO
             * ============================================================
             */

            val funcaoConstruida =
                funcao.composicaoExecutada

            val composicaoExecutada =
                funcao.composicaoExecutada

            val capacidadesPresentes =
                funcao.capacidadesUsadas.isNotEmpty() &&
                funcao.capacidadesAusentes.isEmpty()


            /*
             * ============================================================
             * 4. PROCESSAMENTO
             * ============================================================
             *
             * O AdaFunB utiliza o PacketEngine.
             *
             * Portanto, a execução do processamento é considerada
             * detectada quando a composição foi executada e o
             * PacketEngine retornou uma análise válida.
             */

            val processamentoExecutado =
                funcao.composicaoExecutada &&
                funcao.pacotesProcessados >= 0 &&
                funcao.bytesProcessados >= 0


            /*
             * ============================================================
             * 5. NOVA LEITURA DO RESERVATÓRIO
             * ============================================================
             *
             * Depois da execução, fazemos uma segunda leitura.
             *
             * Isso permite verificar se os registros originais
             * continuam presentes.
             */

            val pacotesDepois =
                DataReservoir.listar(context)

            val quantidadeDepois =
                pacotesDepois.size

            val bytesDepois =
                pacotesDepois.sumOf {
                    it.size.toLong()
                }


            /*
             * ============================================================
             * 6. VERIFICAÇÃO DA QUANTIDADE
             * ============================================================
             *
             * O processamento não pode fazer desaparecer registros.
             *
             * Como o aparelho pode continuar recebendo novos dados,
             * a quantidade pode aumentar.
             *
             * Portanto:
             *
             * depois >= antes
             */

            val quantidadePreservada =
                quantidadeDepois >= quantidadeAntes


            /*
             * ============================================================
             * 7. VERIFICAÇÃO DOS BYTES
             * ============================================================
             *
             * Pelo mesmo motivo, o tamanho total não pode diminuir.
             */

            val bytesPreservados =
                bytesDepois >= bytesAntes


            /*
             * ============================================================
             * 8. VERIFICAÇÃO INDIVIDUAL DOS REGISTROS ORIGINAIS
             * ============================================================
             *
             * Aqui fazemos uma verificação mais forte.
             *
             * Cada pacote existente antes da execução deve continuar
             * existindo depois da execução com os mesmos dados.
             */

            val dadosOriginaisPreservados =
                pacotesAntes.all { original ->

                    val atual =
                        pacotesDepois.firstOrNull {
                            it.id == original.id
                        }

                    atual != null &&
                    atual.timestamp == original.timestamp &&
                    atual.source == original.source &&
                    atual.type == original.type &&
                    atual.data == original.data &&
                    atual.size == original.size
                }


            /*
             * ============================================================
             * 9. RESULTADO FINAL
             * ============================================================
             */

            val validacaoAprovada =
                funcaoConstruida &&
                composicaoExecutada &&
                processamentoExecutado &&
                capacidadesPresentes &&
                quantidadePreservada &&
                bytesPreservados &&
                dadosOriginaisPreservados


            val descricao =

                if (validacaoAprovada) {

                    "A função ${funcao.funcaoId} foi construída e " +
                    "submetida à validação inicial. As capacidades " +
                    "necessárias estavam disponíveis, o processamento " +
                    "foi executado e os dados originais observados antes " +
                    "da execução permaneceram preservados."

                } else {

                    buildString {

                        append(
                            "A validação da função ${funcao.funcaoId} " +
                            "não foi confirmada integralmente."
                        )

                        append("\n\n")

                        if (!funcaoConstruida) {
                            append(
                                "• A função não foi construída.\n"
                            )
                        }

                        if (!capacidadesPresentes) {
                            append(
                                "• As capacidades necessárias não " +
                                "estavam integralmente disponíveis.\n"
                            )
                        }

                        if (!quantidadePreservada) {
                            append(
                                "• A quantidade de registros diminuiu.\n"
                            )
                        }

                        if (!bytesPreservados) {
                            append(
                                "• O tamanho total dos dados diminuiu.\n"
                            )
                        }

                        if (!dadosOriginaisPreservados) {
                            append(
                                "• Pelo menos um registro original " +
                                "não foi encontrado com o mesmo conteúdo.\n"
                            )
                        }
                    }
                }


            return AdaFunVResult(
                funcaoId =
                    funcao.funcaoId,

                requisitoId =
                    funcao.requisitoId,

                funcaoConstruida =
                    funcaoConstruida,

                composicaoExecutada =
                    composicaoExecutada,

                processamentoExecutado =
                    processamentoExecutado,

                capacidadesPresentes =
                    capacidadesPresentes,

                dadosOriginaisPreservados =
                    dadosOriginaisPreservados,

                quantidadePreservada =
                    quantidadePreservada,

                bytesPreservados =
                    bytesPreservados,

                pacotesAntes =
                    quantidadeAntes,

                pacotesDepois =
                    quantidadeDepois,

                bytesAntes =
                    bytesAntes,

                bytesDepois =
                    bytesDepois,

                pacotesProcessados =
                    funcao.pacotesProcessados,

                bytesProcessados =
                    funcao.bytesProcessados,

                capacidadesUsadas =
                    funcao.capacidadesUsadas,

                capacidadesAusentes =
                    funcao.capacidadesAusentes,

                validacaoAprovada =
                    validacaoAprovada,

                descricao =
                    descricao
            )

        } catch (e: Exception) {

            return AdaFunVResult(

                funcaoId =
                    "F001",

                requisitoId =
                    requisitoId,

                funcaoConstruida =
                    false,

                composicaoExecutada =
                    false,

                processamentoExecutado =
                    false,

                capacidadesPresentes =
                    false,

                dadosOriginaisPreservados =
                    false,

                quantidadePreservada =
                    false,

                bytesPreservados =
                    false,

                pacotesAntes =
                    0,

                pacotesDepois =
                    0,

                bytesAntes =
                    0,

                bytesDepois =
                    0,

                pacotesProcessados =
                    0,

                bytesProcessados =
                    0,

                capacidadesUsadas =
                    emptyList(),

                capacidadesAusentes =
                    emptyList(),

                validacaoAprovada =
                    false,

                descricao =
                    "Erro durante a validação: ${e.message}"
            )
        }
    }


    fun resumo(
        context: android.content.Context,
        requisitoId: String
    ): String {

        val resultado =
            validar(
                context,
                requisitoId
            )


        return buildString {

            append("✅ ADAFUNV\n\n")

            append("VALIDADOR DE FUNÇÕES\n\n")

            append("FUNÇÃO: ")
            append(resultado.funcaoId)
            append("\n")

            append("REQUISITO: ")
            append(resultado.requisitoId)
            append("\n\n")


            append("FUNÇÃO CONSTRUÍDA: ")

            append(
                if (resultado.funcaoConstruida)
                    "SIM"
                else
                    "NÃO"
            )

            append("\n")


            append("COMPOSIÇÃO EXECUTADA: ")

            append(
                if (resultado.composicaoExecutada)
                    "SIM"
                else
                    "NÃO"
            )

            append("\n")


            append("PROCESSAMENTO EXECUTADO: ")

            append(
                if (resultado.processamentoExecutado)
                    "SIM"
                else
                    "NÃO"
            )

            append("\n")


            append("CAPACIDADES PRESENTES: ")

            append(
                if (resultado.capacidadesPresentes)
                    "SIM"
                else
                    "NÃO"
            )

            append("\n\n")


            append("CAPACIDADES UTILIZADAS:\n")

            if (resultado.capacidadesUsadas.isEmpty()) {

                append("NENHUMA\n")

            } else {

                resultado.capacidadesUsadas.forEach {

                    append(it)
                    append("\n")
                }
            }


            append("\n")


            append("CAPACIDADES AUSENTES:\n")

            if (resultado.capacidadesAusentes.isEmpty()) {

                append("NENHUMA\n")

            } else {

                resultado.capacidadesAusentes.forEach {

                    append(it)
                    append("\n")
                }
            }


            append("\n")

            append("RESERVATÓRIO ANTES:\n")

            append("Pacotes: ")
            append(resultado.pacotesAntes)
            append("\n")

            append("Bytes: ")
            append(resultado.bytesAntes)
            append("\n\n")


            append("RESERVATÓRIO DEPOIS:\n")

            append("Pacotes: ")
            append(resultado.pacotesDepois)
            append("\n")

            append("Bytes: ")
            append(resultado.bytesDepois)
            append("\n\n")


            append("QUANTIDADE PRESERVADA: ")

            append(
                if (resultado.quantidadePreservada)
                    "SIM"
                else
                    "NÃO"
            )

            append("\n")


            append("BYTES PRESERVADOS: ")

            append(
                if (resultado.bytesPreservados)
                    "SIM"
                else
                    "NÃO"
            )

            append("\n")


            append("DADOS ORIGINAIS PRESERVADOS: ")

            append(
                if (resultado.dadosOriginaisPreservados)
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


            append("VALIDAÇÃO FINAL: ")

            append(
                if (resultado.validacaoAprovada)
                    "APROVADA"
                else
                    "NÃO CONFIRMADA"
            )

            append("\n\n")


            append(resultado.descricao)
        }
    }
}
