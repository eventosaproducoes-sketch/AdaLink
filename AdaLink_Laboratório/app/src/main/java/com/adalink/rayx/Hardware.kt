package com.adalink.rayx

import android.os.Build

object Hardware {

    fun texto() = """
Fabricante: ${Build.MANUFACTURER}
Modelo: ${Build.MODEL}
Android: ${Build.VERSION.RELEASE}
API: ${Build.VERSION.SDK_INT}
Device: ${Build.DEVICE}
Produto: ${Build.PRODUCT}
Hardware: ${Build.HARDWARE}
ABI: ${Build.SUPPORTED_ABIS.joinToString()}
Baseband: ${Build.getRadioVersion()}
""".trimIndent()
}
