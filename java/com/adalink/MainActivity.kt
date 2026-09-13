package com.adalink

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(32, 32, 32, 32)

        val titulo = TextView(this)
        titulo.text = "AdaLink"
        titulo.textSize = 32f
        titulo.setTextColor(Color.BLACK)
        titulo.gravity = Gravity.CENTER

        val status = TextView(this)
        status.text = "Afro-Conect Inteligente\n\nPreparando leitura GNSS..."
        status.textSize = 18f
        status.gravity = Gravity.CENTER
        status.setPadding(0, 40, 0, 0)

        layout.addView(titulo)
        layout.addView(status)

        setContentView(layout)
    }
}
