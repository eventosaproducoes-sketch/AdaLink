package com.adalink.rayx

import android.app.Activity
import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val texto = TextView(this)
        texto.textSize = 18f
        texto.setPadding(20, 20, 20, 40)

        texto.text = """
            🚀 ADALINK RAY-X

            ==============================

            📱 SISTEMA
            ${Sistema.texto()}

            ==============================

            🧪 LABORATÓRIO
            Ray-X iniciado.

            Próxima etapa:
            diagnóstico completo.

            ==============================

            Role a tela para baixo
            para visualizar o relatório.

        """.trimIndent()

        val scroll = ScrollView(this)
        scroll.addView(texto)

        setContentView(scroll)
    }
}
