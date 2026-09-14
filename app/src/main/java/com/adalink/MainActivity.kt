package com.adalink.rayx

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.Location
import android.location.LocationManager
import android.telephony.TelephonyManager
import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

        r.append("==============================\n")
        r.append("🧠 RAY-X\n\n")

        r.append("Diagnóstico integrado ativo.\n")
        r.append("Android + hardware + telefonia +\n")
        r.append("conectividade + Wi-Fi + Bluetooth + GNSS.\n")

        r.append("\nA análise NTN será feita separadamente.\n")

        r.append(Analise.texto())
        r.append("\n\n")

        r.append(Veredito.texto())
        r.append("\n")

        r.append(NTN.texto(this))
        r.append("\n")

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

            r.append("\n\n")
            r.append(
                textoOrbit(local)
            )

        } else {

            r.append(
                "\n🛰️ NTN ENGINE\n\n" +
                "Aguardando posição GNSS real..."
            )
        }

        tela.text = r.toString()

        iniciarGnss(r)
    }

    private fun textoOrbit(
        local: Location
    ): String {

        val tempoSeg =
            android.os.SystemClock.elapsedRealtime() / 1000.0

        val sat =
            NTNOrbit.posicao(tempoSeg)

        val earthKm = 6371.0

        val raio =
            earthKm + local.altitude / 1000.0

        val lat =
            Math.toRadians(local.latitude)

        val lon =
            Math.toRadians(local.longitude)

        val x =
            raio * cos(lat) * cos(lon)

        val y =
            raio * cos(lat) * sin(lon)

        val z =
            raio * sin(lat)

        val dx =
            sat[0] - x

        val dy =
            sat[1] - y

        val dz =
            sat[2] - z

        val distanciaKm =
            sqrt(
                dx * dx +
                dy * dy +
                dz * dz
            )

        val velocidade =
            NTNOrbit.velocidadeOrbital()

        val atrasoMs =
            (distanciaKm * 1000.0 /
                    299792458.0) * 1000.0

        return """
🛰️ NTN ORBIT — RAY-X

MODELO ORBITAL

Tempo do modelo:
${"%.1f".format(tempoSeg)} s

Órbita:
LEO virtual

Altitude orbital:
600 km

Velocidade orbital:
${"%.3f".format(velocidade)} km/s

POSIÇÃO DO SATÉLITE

X: ${"%.2f".format(sat[0])} km
Y: ${"%.2f".format(sat[1])} km
Z: ${"%.2f".format(sat[2])} km

DISTÂNCIA ATÉ O APARELHO:
${"%.2f".format(distanciaKm)} km

ATRASO DE PROPAGAÇÃO:
${"%.3f".format(atrasoMs)} ms

STATUS:
🟡 ÓRBITA NTN VIRTUAL

O satélite é matematicamente
simulado pelo AdaLink.

Nenhum sinal RF foi transmitido.
""".trimIndent()
    }

    private fun iniciarGnss(
        base: StringBuilder
    ) {

        try {

            if (
                !lm.isProviderEnabled(
                    LocationManager.GPS_PROVIDER
                )
            ) {
                return
            }

            val callback =
                object : GnssStatus.Callback() {

                    override fun onSatelliteStatusChanged(
                        status: GnssStatus
                    ) {

                        var usados = 0

                        for (
                            i in 0 until status.satelliteCount
                        ) {

                            if (
                                status.usedInFix(i)
                            ) {
                                usados++
                            }
                        }

                        runOnUiThread {

                            tela.text =
                                base.toString() +
                                "\n\n🛰️ GNSS AO VIVO\n\n" +
                                "Satélites: " +
                                status.satelliteCount +
                                "\n" +
                                "Usados no fix: " +
                                usados
                        }
                    }
                }

            lm.registerGnssStatusCallback(
                callback
            )

        } catch (e: Exception) {

            tela.append(
                "\n\nGNSS ao vivo: acesso limitado"
            )
        }
    }
}
