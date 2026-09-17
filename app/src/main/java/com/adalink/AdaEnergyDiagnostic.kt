package com.adalink

import android.content.Context
import android.os.BatteryManager
import android.os.Build

object AdaEnergyDiagnostic {

    fun diagnostico(context: Context): String {

        val bateria = context.getSystemService(Context.BATTERY_SERVICE)
                as BatteryManager

        val nivel = bateria.getIntProperty(
            BatteryManager.BATTERY_PROPERTY_CAPACITY
        )

        val corrente = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bateria.getIntProperty(
                BatteryManager.BATTERY_PROPERTY_CURRENT_NOW
            )
        } else {
            0
        }

        val energia = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bateria.getIntProperty(
                BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER
            )
        } else {
            0
        }

        return """
            ⚡ ADA ENERGY DIAGNOSTIC

            Bateria: $nivel%

            Corrente informada pelo sistema:
            $corrente µA

            Contador de energia:
            $energia nWh

            Android:
            ${Build.VERSION.RELEASE}

            API:
            ${Build.VERSION.SDK_INT}

            Status:
            DIAGNÓSTICO ATIVO
        """.trimIndent()
    }
}
