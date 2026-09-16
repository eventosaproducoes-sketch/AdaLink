package com.adalink

import android.app.*
import android.os.*
import android.content.*
import android.graphics.Color
import android.view.*
import android.widget.*
import android.location.*

class MainActivity : Activity() {

    lateinit var info: TextView
    lateinit var lm: LocationManager

    var satelites = 0
    var usados = 0

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        val tela = LinearLayout(this)
        tela.orientation = LinearLayout.VERTICAL
        tela.setBackgroundColor(Color.BLACK)

        info = TextView(this)
        info.textSize = 18f
        info.setTextColor(Color.WHITE)
        info.gravity = Gravity.CENTER
        info.text = "AdaLink\n\nRay-X iniciando..."

        tela.addView(info, LinearLayout.LayoutParams(
            -1, 0, 1f
        ))

        val botao = Button(this)
        botao.text = "EXPORTAR TXT"
        botao.setOnClickListener {
            compartilhar()
        }

        tela.addView(botao)

        setContentView(tela)

        lm = getSystemService(LOCATION_SERVICE) as LocationManager

        iniciar()
    }

    fun iniciar() {

        info.text =
            "AdaLink — Ray-X\n\n" +
            "🛰️ Procurando satélites..."

        val cb = object : GnssStatus.Callback() {

            override fun onSatelliteStatusChanged(
                s: GnssStatus
            ) {

                satelites = s.satelliteCount
                usados = 0

                for (i in 0 until s.satelliteCount) {
                    if (s.usedInFix(i)) usados++
                }

                info.text =
                    "AdaLink — Ray-X\n\n" +
                    "🛰️ Satélites: $satelites\n" +
                    "🎯 Usados: $usados\n\n" +
                    "📡 GNSS ATIVO"
            }
        }

        lm.registerGnssStatusCallback(
            cb,
            Handler(Looper.getMainLooper())
        )
    }

    fun compartilhar() {

        val texto =
            "ADALINK — RAY-X\n\n" +
            "Satélites: $satelites\n" +
            "Usados no fix: $usados\n" +
            "GNSS: ATIVO\n"

        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT,
            "AdaLink Ray-X")
        i.putExtra(Intent.EXTRA_TEXT, texto)

        startActivity(
            Intent.createChooser(
                i,
                "Compartilhar relatório"
            )
        )
    }
}
