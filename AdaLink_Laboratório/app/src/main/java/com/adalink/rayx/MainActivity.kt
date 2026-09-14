package com.adalink.rayx

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.LocationManager
import android.telephony.TelephonyManager
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var tela: TextView
    private lateinit var lm: LocationManager
    private lateinit var tm: TelephonyManager

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        tela = TextView(this)
tela.textSize = 15f
tela.setPadding(20, 20, 20, 20)

val scroll = android.widget.ScrollView(this)
scroll.addView(tela)

setContentView(scroll)

        lm = getSystemService(LOCATION_SERVICE)
                as LocationManager

        tm = getSystemService(TELEPHONY_SERVICE)
                as TelephonyManager

        if (
            checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE
                ),
                10
            )

        } else {
            diagnostico()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        results: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            results
        )

        if (requestCode == 10) {
            diagnostico()
        }
    }

    private fun diagnostico() {

        val r = StringBuilder()

        r.append("🚀 ADALINK RAY-X\n")
        r.append("==============================\n\n")

        r.append(Sistema.texto())
        r.append("\n\n")

        r.append(Telefonia.texto(tm))
        r.append("\n")

        r.append(Conectividade.texto(this))
        r.append("\n")

        r.append(WifiBluetooth.texto(this))
        r.append("\n")

        r.append(Gnss.texto(lm))
        r.append("\n")

        r.append("==============================\n")
        r.append("🧠 RAY-X\n\n")
        r.append("Diagnóstico integrado ativo.\n")
        r.append("Android + hardware + telefonia +\n")
        r.append("conectividade + Wi-Fi + Bluetooth + GNSS.\n")
        r.append("\nA análise NTN será feita separadamente.\n")

        tela.text = r.toString()

        iniciarGnss(r)
    }

    private fun iniciarGnss(base: StringBuilder) {

        try {

            if (
                !lm.isProviderEnabled(
                    LocationManager.GPS_PROVIDER
                )
            ) {
                return
            }

            val callback = object : GnssStatus.Callback() {

                override fun onSatelliteStatusChanged(
                    status: GnssStatus
                ) {

                    var usados = 0

                    for (
                        i in 0 until status.satelliteCount
                    ) {
                        if (status.usedInFix(i)) {
                            usados++
                        }
                    }

                    runOnUiThread {

                        tela.text =
                            base.toString() +
                            "\n🛰️ GNSS AO VIVO\n\n" +
                            "Satélites: " +
                            status.satelliteCount +
                            "\n" +
                            "Usados no fix: " +
                            usados
                    }
                }
            }

            lm.registerGnssStatusCallback(callback)

        } catch (e: Exception) {

            tela.append(
                "\n\nGNSS ao vivo: acesso limitado"
            )
        }
    }
}
