package com.adalink

import android.content.Context
import android.os.BatteryManager
import android.os.Build
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

data class AdaObservEnerResult(
    val luzDisponivel: Boolean,
    val luzLux: Float?,
    val movimentoDisponivel: Boolean,
    val aceleracaoX: Float?,
    val aceleracaoY: Float?,
    val aceleracaoZ: Float?,
    val temperaturaBateriaCelsius: Float?,
    val nivelBateriaPercentual: Int?,
    val voltagemBateriaMilivolts: Int?,
    val carregando: Boolean,
    val correnteBateriaMicroampere: Long?,
    val energiaBateriaNanonWh: Long?,
    val descricao: String
)

object AdaObservEner {

    fun lerBateria(context: Context): AdaObservEnerResult {

        val bateria =
            context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        val intent =
            context.registerReceiver(
                null,
                android.content.IntentFilter(
                    android.content.Intent.ACTION_BATTERY_CHANGED
                )
            )

        val temperatura =
            intent?.getIntExtra(
                BatteryManager.EXTRA_TEMPERATURE,
                Int.MIN_VALUE
            )

        val temperaturaCelsius =
            if (temperatura != null && temperatura != Int.MIN_VALUE) {
                temperatura / 10f
            } else {
                null
            }

        val nivel =
            intent?.getIntExtra(
                BatteryManager.EXTRA_LEVEL,
                -1
            )

        val escala =
            intent?.getIntExtra(
                BatteryManager.EXTRA_SCALE,
                -1
            )

        val nivelPercentual =
            if (nivel != null && escala != null && nivel >= 0 && escala > 0) {
                ((nivel.toFloat() / escala.toFloat()) * 100f).toInt()
            } else {
                null
            }

        val voltagem =
            intent?.getIntExtra(
                BatteryManager.EXTRA_VOLTAGE,
                -1
            )

        val status =
            intent?.getIntExtra(
                BatteryManager.EXTRA_STATUS,
                -1
            )

        val carregando =
            status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL

        val corrente =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    bateria.getLongProperty(
                        BatteryManager.BATTERY_PROPERTY_CURRENT_NOW
                    )
                } catch (e: Exception) {
                    Long.MIN_VALUE
                }
            } else {
                Long.MIN_VALUE
            }

        val correnteValida =
            if (corrente != Long.MIN_VALUE) {
                corrente
            } else {
                null
            }

        val energia =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    bateria.getLongProperty(
                        BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER
                    )
                } catch (e: Exception) {
                    Long.MIN_VALUE
                }
            } else {
                Long.MIN_VALUE
            }

        val energiaValida =
            if (energia != Long.MIN_VALUE) {
                energia
            } else {
                null
            }

        return AdaObservEnerResult(
            luzDisponivel = false,
            luzLux = null,
            movimentoDisponivel = false,
            aceleracaoX = null,
            aceleracaoY = null,
            aceleracaoZ = null,
            temperaturaBateriaCelsius = temperaturaCelsius,
            nivelBateriaPercentual = nivelPercentual,
            voltagemBateriaMilivolts = voltagem,
            carregando = carregando,
            correnteBateriaMicroampere = correnteValida,
            energiaBateriaNanonWh = energiaValida,
            descricao =
                "ADAOBSENER — leitura energética inicial do dispositivo."
        )
    }

    fun sensoresDisponiveis(context: Context): String {

        val sensorManager =
            context.getSystemService(
                Context.SENSOR_SERVICE
            ) as SensorManager

        val luz =
            sensorManager.getDefaultSensor(
                Sensor.TYPE_LIGHT
            )

        val movimento =
            sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            )

        return buildString {

            append("⚡ ADAOBSENER\n\n")

            append("OBSERVAÇÃO ENERGÉTICA DO DISPOSITIVO\n\n")

            append(
                "SENSOR DE LUZ: " +
                    if (luz != null) "DISPONÍVEL" else "NÃO DISPONÍVEL"
            )

            append("\n")

            if (luz != null) {
                append("Nome: ${luz.name}\n")
                append("Fabricante: ${luz.vendor}\n")
                append("Faixa máxima: ${luz.maximumRange} lux\n")
            }

            append("\n")

            append(
                "ACELERÔMETRO: " +
                    if (movimento != null) "DISPONÍVEL" else "NÃO DISPONÍVEL"
            )

            append("\n")

            if (movimento != null) {
                append("Nome: ${movimento.name}\n")
                append("Fabricante: ${movimento.vendor}\n")
                append("Faixa máxima: ${movimento.maximumRange}\n")
            }

            append("\n")

            append("OBJETIVO:\n")
            append(
                "Mapear recursos internos do Redmi que possam " +
                    "ser relacionados à observação e futura recuperação de energia."
            )
        }
    }
}
