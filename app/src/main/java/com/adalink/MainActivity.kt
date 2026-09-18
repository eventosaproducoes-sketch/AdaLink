package com.adalink

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.widget.*

class MainActivity : Activity() {

    private lateinit var info: TextView
    private lateinit var lm: LocationManager

    private var sats = 0
    private var used = 0
    private var ultimoRaw = ""
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
    
        val tela = LinearLayout(this)
        tela.orientation = LinearLayout.VERTICAL
        tela.setPadding(20, 20, 20, 20)
        tela.setBackgroundColor(Color.BLACK)

        val titulo = TextView(this)
        titulo.text = "ADA LINK — RAY-X"
        titulo.textSize = 24f
        titulo.setTextColor(Color.WHITE)
        tela.addView(titulo)

        val reservatorio = Button(this)
        reservatorio.text = "📦 CONSULTAR ADA RESERVOIR"
        tela.addView(reservatorio)

        reservatorio.setOnClickListener {
            Toast.makeText(
                this,
                "📦 ADA RESERVOIR\nPacotes: ${
                    DataReservoir.quantidade(this)
                }\nDados: ${
                    DataReservoir.tamanhoTotal(this)
                } bytes",
                Toast.LENGTH_LONG
            ).show()
        }

        val energia = Button(this)
        energia.text = "⚡ DIAGNÓSTICO DE ENERGIA"
        tela.addView(energia)

        energia.setOnClickListener {
            Toast.makeText(
                this,
                AdaEnergyDiagnostic.diagnostico(this),
                Toast.LENGTH_LONG
            ).show()
        }

        info = TextView(this)
        info.textSize = 14f
        info.setTextColor(Color.WHITE)
        info.text = "🔎 Iniciando Ray-X..."
        tela.addView(info)

        setContentView(tela)

        lm = getSystemService(LOCATION_SERVICE) as LocationManager

        if (checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        } else {
            iniciarGNSS()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        results: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions, results
        )

        if (requestCode == 1001 &&
            results.isNotEmpty() &&
            results[0] == PackageManager.PERMISSION_GRANTED
        ) {
            iniciarGNSS()
        }
    }
val rawCallback = object : GnssMeasurementsEvent.Callback() {

    override fun onGnssMeasurementsReceived(
        event: GnssMeasurementsEvent
    ) {
        val resultado = GnssRawProbe.analisar(event)

        ultimoRaw = resultado

        runOnUiThread {
            info.text = resultado
        }
    }
}
    private fun iniciarGNSS() {

        val callback = object : GnssStatus.Callback() {

            override fun onSatelliteStatusChanged(
                status: GnssStatus
            ) {
                sats = status.satelliteCount
                used = 0

                val texto = StringBuilder()
                texto.append("🛰️ RAY-X\n\n")
                texto.append("Satélites: $sats\n")

                for (i in 0 until sats) {
                    if (status.usedInFix(i)) used++

                    texto.append(
                        "SV ${status.getSvid(i)} | " +
                        "C/N0 %.1f dB-Hz\n".format(
                            status.getCn0DbHz(i)
                        )
                    )
                }

                texto.append("\nUsados no fix: $used")

                DataReservoir.armazenar(
                    this@MainActivity,
                    "RAY-X",
                    "GNSS",
                    texto.toString()
                )

                info.text = texto.toString()
            }
        }

        lm.registerGnssStatusCallback(callback, Handler(mainLooper))

lm.registerGnssMeasurementsCallback(
    rawCallback,
    Handler(mainLooper)
)
        try {
            lm.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L,
                0f,
                object : LocationListener {
                    override fun onLocationChanged(
                        location: Location
                    ) {}
                }
            )
        } catch (e: SecurityException) {
            info.text = "⚠️ Permissão GNSS não autorizada"
        }
    }
}
