package com.adalink.rayx

import android.os.Build

object Analise {
    fun texto() = """
🧠 ANÁLISE RAY-X

Android: ${Build.VERSION.RELEASE}
API: ${Build.VERSION.SDK_INT}
Hardware: ${Build.HARDWARE}

O aparelho será comparado em:
• Hardware
• Modem/telefonia
• Conectividade
• GNSS
• Recursos expostos pelo Android

⚠️ NTN não será presumido.
A compatibilidade será determinada pelas evidências coletadas.
""".trimIndent()
}
