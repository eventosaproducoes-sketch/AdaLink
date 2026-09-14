package com.adalink

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        val tela = TextView(this)

        tela.textSize = 20f
        tela.text = """
            AdaLink

            🛰️ GNSS
            Sistema funcionando.

            Aguardando sinais...
        """.trimIndent()

        setContentView(tela)
    }
}
