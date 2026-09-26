package com.adalink

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager

data class AdaEnergLabResult(
    val dispositivo: String,
    val bateriaDisponivel: Boolean,
    val nivelBateria: Int?,
    val tensaoMilivolts: Int?,
    val temperaturaCelsius: Float?,
    val correnteMicroampere: Long?,
    val energiaNanonWh: Long?,
    val cargaMicroampereHora: Long?,
    val carregando: Boolean,
    val fonteCarregamento: String,
    val saudeBateria: String,
    val economiaEnergiaAtiva: Boolean,
    val estadoTermico: String,

    val sensorLuzDisponivel: Boolean,
    val sensorMovimentoDisponivel: Boolean,

    val sensoresEnergeticos: List<String>,

    val geracaoEnergiaDemonstrada: Boolean,
    val recuperacaoEnergiaDemonstrada: Boolean,
    val armazenamentoEnergiaDemonstrado: Boolean,
    val autonomiaEnergeticaDemonstrada: Boolean,

    val etapasInvestigacao: List<String>,
    val recursosDisponiveis: List<String>,
    val recursosNaoDisponiveis: List<String>,
    val proximosExperimentos: List<String>,

    val conclusao: String
)

object AdaEnergLab {

    fun investigar(
        context: Context
    ): AdaEnergLabResult {

        val batteryManager =
            context.getSystemService(
                Context.BATTERY_SERVICE
            ) as BatteryManager

        val batteryIntent =
            context.registerReceiver(
                null,
                IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED
                )
            )

        val nivel =
            batteryIntent?.getIntExtra(
                BatteryManager.EXTRA_LEVEL,
                -1
            )?.takeIf { it >= 0 }

        val escala =
            batteryIntent?.getIntExtra(
                BatteryManager.EXTRA_SCALE,
                -1
            )?.takeIf { it > 0 }

        val nivelPercentual =
            if (
                nivel != null &&
                escala != null
            ) {
                (nivel * 100f / escala)
                    .toInt()
            } else {
                null
            }

        val temperatura =
            batteryIntent?.getIntExtra(
                BatteryManager.EXTRA_TEMPERATURE,
                Int.MIN_VALUE
            )?.takeIf {
                it != Int.MIN_VALUE
            }?.div(10f)

        val tensao =
            batteryIntent?.getIntExtra(
                BatteryManager.EXTRA_VOLTAGE,
                -1
            )?.takeIf {
                it > 0
            }

        val status =
            batteryIntent?.getIntExtra(
                BatteryManager.EXTRA_STATUS,
                -1
            ) ?: -1

        val carregando =
            status ==
                BatteryManager.BATTERY_STATUS_CHARGING ||
            status ==
                BatteryManager.BATTERY_STATUS_FULL

        val fontePlugada =
            batteryIntent?.getIntExtra(
                BatteryManager.EXTRA_PLUGGED,
                0
            ) ?: 0

        val fonte =
            when (fontePlugada) {

                BatteryManager.BATTERY_PLUGGED_AC ->
                    "REDE ELÉTRICA"

                BatteryManager.BATTERY_PLUGGED_USB ->
                    "USB"

                BatteryManager.BATTERY_PLUGGED_WIRELESS ->
                    "SEM FIO"

                else ->
                    "NENHUMA / NÃO IDENTIFICADA"
            }

        val saude =
            when (
                batteryIntent?.getIntExtra(
                    BatteryManager.EXTRA_HEALTH,
                    -1
                )
            ) {

                BatteryManager.BATTERY_HEALTH_GOOD ->
                    "BOA"

                BatteryManager.BATTERY_HEALTH_OVERHEAT ->
                    "SUPERAQUECIMENTO"

                BatteryManager.BATTERY_HEALTH_DEAD ->
                    "CRÍTICA"

                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE ->
                    "SOBRETENSÃO"

                BatteryManager.BATTERY_HEALTH_COLD ->
                    "FRIA"

                else ->
                    "NÃO INFORMADA"
            }

        val corrente =
            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.LOLLIPOP
            ) {
                batteryManager
                    .getLongProperty(
                        BatteryManager.BATTERY_PROPERTY_CURRENT_NOW
                    )
                    .takeIf {
                        it != Long.MIN_VALUE
                    }
            } else {
                null
            }

        val energia =
            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.LOLLIPOP
            ) {
                batteryManager
                    .getLongProperty(
                        BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER
                    )
                    .takeIf {
                        it != Long.MIN_VALUE
                    }
            } else {
                null
            }

        val carga =
            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.LOLLIPOP
            ) {
                batteryManager
                    .getLongProperty(
                        BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER
                    )
                    .takeIf {
                        it != Long.MIN_VALUE
                    }
            } else {
                null
            }

        val powerManager =
            context.getSystemService(
                Context.POWER_SERVICE
            ) as PowerManager

        val economia =
            powerManager.isPowerSaveMode

        val estadoTermico =
            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.Q
            ) {

                when (
                    powerManager.currentThermalStatus
                ) {

                    PowerManager.THERMAL_STATUS_NONE ->
                        "NORMAL"

                    PowerManager.THERMAL_STATUS_LIGHT ->
                        "LEVE"

                    PowerManager.THERMAL_STATUS_MODERATE ->
                        "MODERADO"

                    PowerManager.THERMAL_STATUS_SEVERE ->
                        "SEVERO"

                    PowerManager.THERMAL_STATUS_CRITICAL ->
                        "CRÍTICO"

                    PowerManager.THERMAL_STATUS_EMERGENCY ->
                        "EMERGÊNCIA"

                    PowerManager.THERMAL_STATUS_SHUTDOWN ->
                        "DESLIGAMENTO"

                    else ->
                        "DESCONHECIDO"
                }

            } else {
                "NÃO DISPONÍVEL"
            }

        val sensorManager =
            context.getSystemService(
                Context.SENSOR_SERVICE
            ) as SensorManager

        val sensorLuz =
            sensorManager.getDefaultSensor(
                Sensor.TYPE_LIGHT
            )

        val sensorAcelerometro =
            sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            )

        val sensores =
            sensorManager
                .getSensorList(
                    Sensor.TYPE_ALL
                )

        val nomesSensores =
            sensores.map {
                "${it.name} — ${it.vendor}"
            }

        val recursosDisponiveis =
            mutableListOf<String>()

        val recursosNaoDisponiveis =
            mutableListOf<String>()

        if (nivelPercentual != null) {
            recursosDisponiveis.add(
                "Nível da bateria"
            )
        } else {
            recursosNaoDisponiveis.add(
                "Nível da bateria"
            )
        }

        if (tensao != null) {
            recursosDisponiveis.add(
                "Tensão da bateria"
            )
        } else {
            recursosNaoDisponiveis.add(
                "Tensão da bateria"
            )
        }

        if (temperatura != null) {
            recursosDisponiveis.add(
                "Temperatura da bateria"
            )
        } else {
            recursosNaoDisponiveis.add(
                "Temperatura da bateria"
            )
        }

        if (corrente != null) {
            recursosDisponiveis.add(
                "Corrente da bateria"
            )
        } else {
            recursosNaoDisponiveis.add(
                "Corrente da bateria"
            )
        }

        if (energia != null) {
            recursosDisponiveis.add(
                "Contador energético"
            )
        } else {
            recursosNaoDisponiveis.add(
                "Contador energético"
            )
        }

        if (carga != null) {
            recursosDisponiveis.add(
                "Contador de carga"
            )
        } else {
            recursosNaoDisponiveis.add(
                "Contador de carga"
            )
        }

        if (sensorLuz != null) {
            recursosDisponiveis.add(
                "Sensor de luz"
            )
        } else {
            recursosNaoDisponiveis.add(
                "Sensor de luz"
            )
        }

        if (sensorAcelerometro != null) {
            recursosDisponiveis.add(
                "Acelerômetro"
            )
        } else {
            recursosNaoDisponiveis.add(
                "Acelerômetro"
            )
        }

        /*
         * ESTADO ATUAL DA PESQUISA
         *
         * Nenhuma destas condições será marcada
         * como demonstrada apenas pela existência
         * de sensores.
         */

        val geracaoDemonstrada =
            false

        val recuperacaoDemonstrada =
            false

        val armazenamentoDemonstrado =
            false

        val autonomiaDemonstrada =
            false

        val etapas =
            listOf(
                "1. Identificar recursos energéticos internos.",
                "2. Medir o estado energético do aparelho.",
                "3. Identificar fontes físicas observáveis.",
                "4. Verificar quais grandezas elétricas estão disponíveis.",
                "5. Comparar energia antes e depois de experimentos.",
                "6. Investigar possibilidade de recuperação energética.",
                "7. Investigar armazenamento energético.",
                "8. Calcular o balanço entre energia recuperada e energia consumida.",
                "9. Avaliar autonomia energética.",
                "10. Preservar a distinção entre medição, cálculo e hipótese."
            )

        val proximosExperimentos =
            listOf(
                "Medir luz em diferentes condições.",
                "Medir movimento/aceleração durante diferentes atividades.",
                "Registrar temperatura durante períodos controlados.",
                "Comparar bateria antes e depois de cada experimento.",
                "Verificar se corrente elétrica é disponibilizada pelo aparelho.",
                "Verificar se o contador energético é disponibilizado.",
                "Determinar quais grandezas podem ser calculadas.",
                "Determinar quais hipóteses permanecem sem demonstração.",
                "Avaliar o balanço energético do próprio software.",
                "Determinar o limite de autonomia possível somente com os recursos existentes."
            )

        val conclusao =
            if (
                recursosDisponiveis.isNotEmpty()
            ) {

                "O Redmi possui recursos internos que podem ser investigados " +
                "energeticamente. Entretanto, a existência de sensores " +
                "não demonstra geração de energia. A geração, recuperação, " +
                "armazenamento e autonomia energética permanecem como " +
                "hipóteses experimentais até que exista medição elétrica " +
                "suficiente para demonstrá-las."

            } else {

                "Não foram encontrados recursos energéticos suficientes " +
                "para iniciar a investigação."
            }

        return AdaEnergLabResult(

            dispositivo =
                "Redmi 9i",

            bateriaDisponivel =
                batteryIntent != null,

            nivelBateria =
                nivelPercentual,

            tensaoMilivolts =
                tensao,

            temperaturaCelsius =
                temperatura,

            correnteMicroampere =
                corrente,

            energiaNanonWh =
                energia,

            cargaMicroampereHora =
                carga,

            carregando =
                carregando,

            fonteCarregamento =
                fonte,

            saudeBateria =
                saude,

            economiaEnergiaAtiva =
                economia,

            estadoTermico =
                estadoTermico,

            sensorLuzDisponivel =
                sensorLuz != null,

            sensorMovimentoDisponivel =
                sensorAcelerometro != null,

            sensoresEnergeticos =
                nomesSensores,

            geracaoEnergiaDemonstrada =
                geracaoDemonstrada,

            recuperacaoEnergiaDemonstrada =
                recuperacaoDemonstrada,

            armazenamentoEnergiaDemonstrado =
                armazenamentoDemonstrado,

            autonomiaEnergeticaDemonstrada =
                autonomiaDemonstrada,

            etapasInvestigacao =
                etapas,

            recursosDisponiveis =
                recursosDisponiveis,

            recursosNaoDisponiveis =
                recursosNaoDisponiveis,

            proximosExperimentos =
                proximosExperimentos,

            conclusao =
                conclusao
        )
    }

    fun relatorio(
        context: Context
    ): String {

        val resultado =
            investigar(context)

        return buildString {

            append("⚡ ADAENERGLAB\n\n")
            append(
                "LABORATÓRIO DE PESQUISA DE AUTONOMIA ENERGÉTICA\n\n"
            )

            append(
                "DISPOSITIVO: ${resultado.dispositivo}\n\n"
            )

            append("════════════════════\n")
            append("ESTADO ENERGÉTICO\n")
            append("════════════════════\n\n")

            append("Bateria: ")
            append(
                resultado.nivelBateria
                    ?.let { "$it%" }
                    ?: "NÃO DISPONÍVEL"
            )
            append("\n")

            append("Tensão: ")
            append(
                resultado.tensaoMilivolts
                    ?.let { "$it mV" }
                    ?: "NÃO DISPONÍVEL"
            )
            append("\n")

            append("Temperatura: ")
            append(
                resultado.temperaturaCelsius
                    ?.let { "$it °C" }
                    ?: "NÃO DISPONÍVEL"
            )
            append("\n")

            append("Corrente: ")
            append(
                resultado.correnteMicroampere
                    ?.let { "$it µA" }
                    ?: "NÃO DISPONÍVEL"
            )
            append("\n")

            append("Energia: ")
            append(
                resultado.energiaNanonWh
                    ?.let { "$it nWh" }
                    ?: "NÃO DISPONÍVEL"
            )
            append("\n")

            append("Carga: ")
            append(
                resultado.cargaMicroampereHora
                    ?.let { "$it µAh" }
                    ?: "NÃO DISPONÍVEL"
            )
            append("\n")

            append(
                "Carregando: ${if (resultado.carregando) "SIM" else "NÃO"}\n"
            )

            append(
                "Fonte: ${resultado.fonteCarregamento}\n"
            )

            append(
                "Saúde: ${resultado.saudeBateria}\n"
            )

            append(
                "Economia de energia: ${
                    if (
                        resultado.economiaEnergiaAtiva
                    ) "ATIVA" else "INATIVA"
                }\n"
            )

            append(
                "Estado térmico: ${resultado.estadoTermico}\n\n"
            )

            append("════════════════════\n")
            append("RECURSOS ENERGÉTICOS\n")
            append("════════════════════\n\n")

            append(
                "Sensor de luz: ${
                    if (
                        resultado.sensorLuzDisponivel
                    ) "DISPONÍVEL" else "NÃO DISPONÍVEL"
                }\n"
            )

            append(
                "Acelerômetro: ${
                    if (
                        resultado.sensorMovimentoDisponivel
                    ) "DISPONÍVEL" else "NÃO DISPONÍVEL"
                }\n\n"
            )

            append("SENSORES ENCONTRADOS:\n")

            if (
                resultado.sensoresEnergeticos.isEmpty()
            ) {

                append(
                    "Nenhum sensor informado.\n"
                )

            } else {

                resultado.sensoresEnergeticos
                    .forEach {
                        append("• $it\n")
                    }
            }

            append("\n════════════════════\n")
            append("O QUE JÁ FOI DEMONSTRADO\n")
            append("════════════════════\n\n")

            append(
                "Geração de energia: ${
                    if (
                        resultado.geracaoEnergiaDemonstrada
                    ) "SIM" else "NÃO DEMONSTRADA"
                }\n"
            )

            append(
                "Recuperação de energia: ${
                    if (
                resultado.recuperacaoEnergiaDemonstrada
                    ) "SIM" else "NÃO DEMONSTRADA"
                }\n"
            )

            append(
                "Armazenamento autônomo: ${
                    if (
                        resultado.armazenamentoEnergiaDemonstrado
                    ) "SIM" else "NÃO DEMONSTRADO"
                }\n"
            )

            append(
                "Autonomia energética: ${
                    if (
                        resultado.autonomiaEnergeticaDemonstrada
                    ) "SIM" else "NÃO DEMONSTRADA"
                }\n\n"
            )

            append("════════════════════\n")
            append("RECURSOS DISPONÍVEIS\n")
            append("════════════════════\n\n")

            resultado.recursosDisponiveis
                .forEach {
                    append("✓ $it\n")
                }

            append("\n════════════════════\n")
            append("RECURSOS NÃO DISPONÍVEIS\n")
            append("════════════════════\n\n")

            if (
                resultado.recursosNaoDisponiveis.isEmpty()
            ) {

                append("Nenhum.\n")

            } else {

                resultado.recursosNaoDisponiveis
                    .forEach {
                        append("• $it\n")
                    }
            }

            append("\n════════════════════\n")
            append("ETAPAS DA PESQUISA\n")
            append("════════════════════\n\n")

            resultado.etapasInvestigacao
                .forEach {
                    append("$it\n")
                }

            append("\n════════════════════\n")
            append("PRÓXIMOS EXPERIMENTOS\n")
            append("════════════════════\n\n")

            resultado.proximosExperimentos
                .forEachIndexed { index, experimento ->

                    append(
                        "${index + 1}. $experimento\n"
                    )
                }

            append("\n════════════════════\n")
            append("CONCLUSÃO ATUAL\n")
            append("════════════════════\n\n")

            append(resultado.conclusao)

            append("\n\n════════════════════\n")
            append("REGRA DA PESQUISA\n")
            append("════════════════════\n\n")

            append(
                "Sensor não é gerador.\n"
            )

            append(
                "Observação não é geração.\n"
            )

            append(
                "Estimativa não é medição.\n"
            )

            append(
                "Hipótese não é demonstração.\n"
            )

            append(
                "Toda conclusão energética deverá ser baseada em evidência."
            )

            append("\n\n")

            append(
                "Nenhum componente físico externo é utilizado."
            )

            append("\n")

            append(
                "Nenhum dado do DataReservoir é apagado ou substituído."
            )
        }
    }
}
