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

    lateinit var info: TextView
    lateinit var lm: LocationManager

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        info = TextView(this)
        info.textSize = 20f
        info.setTextColor(Color.WHITE)
        info.gravity = Gravity.CENTER

        val tela = LinearLayout(this)
        tela.setBackgroundColor(Color.BLACK)
        tela.gravity = Gravity.CENTER
        tela.addView(info)
        setContentView(tela)

        lm = getSystemService(LOCATION_SERVICE) as LocationManager

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 10
            )
        } else iniciar()
    }

    fun iniciar() {
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            info.text = "AdaLink\n\n🔴 GPS desligado"
            return
        }

        info.text = "AdaLink\n\n🛰️ GNSS ligado\nProcurando satélites..."

        val cb = object : GnssStatus.Callback() {
            override fun onSatelliteStatusChanged(s: GnssStatus) {
                var usados = 0
                for (i in 0 until s.satelliteCount)
                    if (s.usedInFix(i)) usados++

                info.text = "AdaLink\n\n" +
                    "🛰️ Satélites: ${s.satelliteCount}\n" +
                    "🎯 Usados: $usados\n\n" +
                    "📡 GNSS ATIVO"
            }
        }

        lm.registerGnssStatusCallback(
            cb, Handler(Looper.getMainLooper())
        )
    }
}
