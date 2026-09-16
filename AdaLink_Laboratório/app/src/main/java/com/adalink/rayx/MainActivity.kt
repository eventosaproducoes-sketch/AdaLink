package com.adalink.rayx

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.LocationManager
import android.telephony.TelephonyManager
import android.os.Bundle
import android.widget.TextView
import android.widget.ScrollView

class MainActivity : Activity() {

    private lateinit var tela: TextView
    private lateinit var lm: LocationManager
    private lateinit var tm: TelephonyManager

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        tela = TextView(this)
        tela.textSize = 15f
        tela.setPadding(20, 20, 20, 20)

        val rolagem = ScrollView(this)
        rolagem.addView(tela)
        setContentView(rolagem)

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

        r.append(SatelliteHunter.texto(tm))
        r.append("\n")

        r.append(Gnss.texto(lm))
        r.append("\n")

        r.append(Analise.texto())
        r.append("\n\n")

        r.append(Veredito.texto())
        r.append("\n")

        r.append(NTN.texto(this))
        r.append("\n\n")

        val local = try {
            lm.getLastKnownLocation(
                LocationManager.GPS_PROVIDER
            )
        } catch (e: SecurityException) {
            null
        }

        if (local != null) {
            r.append(
                NTNEngine.simular(
                    local.latitude,
                    local.longitude,
                    local.altitude
                )
            )
        } else {
            r.append(
                "\n🛰️ NTN ENGINE\n\n" +
                "Aguardando posição GNSS real..."
            )
        }

        val tempo =
            System.currentTimeMillis() / 1000.0

        val orbita =
            NTNOrbit.posicao(tempo)

        r.append(
            "\n🛰️ NTN ORBIT — SATÉLITE VIRTUAL\n\n" +
            "Tempo orbital: %.0f s\n".format(tempo) +
            "X: %.2f km\n".format(orbita[0]) +
            "Y: %.2f km\n".format(orbita[1]) +
            "Z: %.2f km\n".format(orbita[2]) +
            "Velocidade orbital: %.3f km/s\n".format(
                NTNOrbit.velocidadeOrbital()
            ) +
            "Raio orbital: %.1f km\n".format(
                NTNOrbit.distanciaCentroTerra(tempo)
            ) +
            "\nSTATUS: 🟡 ÓRBITA VIRTUAL"
        )

        r.append("\n\n")
        r.append("==============================\n")
        r.append("🧠 RAY-X\n\n")
        r.append("Diagnóstico integrado ativo.\n")
        r.append(
            "Android + hardware + telefonia +\n" +
            "conectividade + Wi-Fi + Bluetooth + GNSS.\n"
        )

        tela.text = r.toString()

        iniciarGnss()
    }

    private fun iniciarGnss() {

        try {

            if (
                !lm.isProviderEnabled(
                    LocationManager.GPS_PROVIDER
                )
            ) {
                tela.append(
                    "\n\n🛰️ GNSS CALLBACK\n\n" +
                    "GPS desligado."
                )
                return
            }

            val callback =
                object : GnssStatus.Callback() {

                    override fun onStarted() {
                        runOnUiThread {
                            tela.append(
                                "\n\n🟢 GNSS CALLBACK\n" +
                                "onStarted()"
                            )
                        }
                    }

                    override fun onFirstFix(
                        ttffMillis: Int
                    ) {
                        runOnUiThread {
                            tela.append(
                                "\n🟢 onFirstFix(): " +
                                ttffMillis + " ms"
                            )
                        }
                    }

                    override fun onSatelliteStatusChanged(
                        status: GnssStatus
                    ) {

                        val deep =
                            GnssDeepScan.satelites(status)

                        runOnUiThread {

                            tela.text =
                                tela.text.toString() +
                                "\n\n" +
                                "🟢 GNSS CALLBACK\n" +
                                "onSatelliteStatusChanged()\n\n" +
                                deep
                        }
                    }

                    override fun onStopped() {
                        runOnUiThread {
                            tela.append(
                                "\n\n🔴 GNSS CALLBACK\n" +
                                "onStopped()"
                            )
                        }
                    }
                }

            lm.registerGnssStatusCallback(
                callback
            )
lm.requestLocationUpdates(
    LocationManager.GPS_PROVIDER,
    1000L,
    0f,
    object : android.location.LocationListener {
        override fun onLocationChanged(location: android.location.Location) {
            tela.append(
                "\n📍 GNSS LOCATION ATIVA\n" +
                "Lat: %.6f\n".format(location.latitude) +
                "Lon: %.6f\n".format(location.longitude) +
                "Alt: %.1f m".format(location.altitude)
            )
        }
    }
)
            tela.append(
                "\n\n🧪 TESTE GNSS CALLBACK\n" +
                "Callback registrado.\n" +
                "Aguardando eventos..."
            )

        } catch (e: Exception) {

            tela.append(
                "\n\n🔴 GNSS CALLBACK\n" +
                "Erro: " +
                e.javaClass.simpleName
            )
        }
    }
}
