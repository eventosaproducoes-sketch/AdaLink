package com.adalink

import android.app.Activity
import android.os.*
import android.content.*
import android.graphics.Color
import android.widget.*
import android.location.*

class MainActivity : Activity() {

    lateinit var info: TextView
    lateinit var lm: LocationManager
    var sats = 0
    var used = 0

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        val tela = LinearLayout(this)
        tela.orientation = LinearLayout.VERTICAL
        tela.setBackgroundColor(Color.BLACK)

        info = TextView(this)
        info.textSize = 18f
        info.setTextColor(Color.WHITE)
        info.gravity = android.view.Gravity.CENTER
        info.text = "AdaLink — Ray-X\n\nIniciando..."

        tela.addView(info,
            LinearLayout.LayoutParams(-1, 0, 1f))

        val botao = Button(this)
        botao.text = "EXPORTAR TXT"
        botao.setOnClickListener { compartilhar() }

        tela.addView(botao)
        setContentView(tela)

        lm = getSystemService(LOCATION_SERVICE)
            as LocationManager
                val cb = object : GnssStatus.Callback() {

            override fun onSatelliteStatusChanged(
                s: GnssStatus
            ) {

                sats = s.satelliteCount
                used = 0

                for (i in 0 until sats) {
                    if (s.usedInFix(i)) used++
                }

                info.text =
                    "AdaLink — Ray-X\n\n" +
                    "🛰️ Satélites: $sats\n" +
                    "🎯 Usados: $used\n\n" +
                    "📡 GNSS ATIVO"
            }
        }

        lm.registerGnssStatusCallback(
            cb,
            Handler(Looper.getMainLooper())
        )
    }    fun compartilhar() {

        val texto =
            "ADALINK — RAY-X 002\n\n" +
            "Satélites: $sats\n" +
            "Usados no fix: $used\n" +
            "GNSS: ATIVO\n"

        val i = Intent(Intent.ACTION_SEND)

        i.type = "text/plain"

        i.putExtra(
            Intent.EXTRA_SUBJECT,
            "AdaLink Ray-X"
        )

        i.putExtra(
            Intent.EXTRA_TEXT,
            texto
        )

        startActivity(
            Intent.createChooser(
                i,
                "Compartilhar relatório"
            )
        )
    }
}
