package com.adalink

import android.Manifest
import android.app.Activity
import android.os.*
import android.content.pm.PackageManager
import android.location.*
import android.graphics.Color
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {

    private lateinit var info: TextView
    private lateinit var lm: LocationManager

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        info = TextView(this)
        info.text = "AdaLink\n\nVerificando GNSS..."
        info.textSize = 20f
        info.setTextColor(Color.WHITE)
        info.gravity = Gravity.CENTER

        val tela = LinearLayout(this)
        tela.gravity = Gravity.CENTER
        tela.setBackgroundColor(Color.BLACK)
        tela.addView(info)
        setContentView(tela)

        lm = getSystemService(LOCATION_SERVICE) as LocationManager

        if (Build.VERSION.SDK_INT >= 23 &&
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 10
            )
        } else iniciar()
    }

    private fun iniciar() {

        val cb = object : GnssStatus.Callback() {
            override fun onSatelliteStatusChanged(s: GnssStatus) {
                var usados = 0
                for (i in 0 until s.satelliteCount)
                    if (s.usedInFix(i)) usados++

                info.text = """
                    AdaLink
                    
                    🛰️ Satélites: ${s.satelliteCount}
                    🎯 Usados na posição: $usados
                    
                    📡 GNSS ATIVO
                """.trimIndent()
            }
        }

        lm.registerGnssStatusCallback(
            cb, Handler(Looper.getMainLooper())
        )
    }

    override fun onRequestPermissionsResult(
        r: Int, p: Array<out String>, g: IntArray
    ) {
        super.onRequestPermissionsResult(r, p, g)
        if (r == 10 && g.isNotEmpty() &&
            g[0] == PackageManager.PERMISSION_GRANTED) iniciar()
    }
}
