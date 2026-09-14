package com.adalink.rayx

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val info = TextView(this)
        info.textSize = 18f

        info.text = """
            ADALINK ANDROID RAY-X

            SISTEMA
            Fabricante: ${Build.MANUFACTURER}
            Modelo: ${Build.MODEL}
            Android: ${Build.VERSION.RELEASE}
            API: ${Build.VERSION.SDK_INT}
            Dispositivo: ${Build.DEVICE}
            Produto: ${Build.PRODUCT}
            Hardware: ${Build.HARDWARE}

            ABI
            ${Build.SUPPORTED_ABIS.joinToString("\n")}

            RAY-X v0.1
            Diagnóstico inicial concluído.
        """.trimIndent()

        setContentView(info)
    }
}
