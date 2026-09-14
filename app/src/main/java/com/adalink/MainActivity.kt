package com.adalink.rayx

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var tela: TextView
    private lateinit var lm: LocationManager

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        tela = TextView(this)
        tela.textSize = 16f
        tela.setPadding(20, 20, 20, 20)
        setContentView(tela)

        lm = getSystemService(LOCATION_SERVICE)
                as LocationManager

        if (checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                10
            )
        } else {
            iniciar()
        }
    }

    override fun onRequestPermissionsResult(
        code: Int,
        permissions: Array<out String>,
        results: IntArray
    ) {
        super.onRequestPermissionsResult(
            code, permissions, results
        )

        if (code == 10) iniciar()
    }

    private fun iniciar() {

        val local = try {
            lm.getLastKnownLocation(
                LocationManager.GPS_PROVIDER
            )
        } catch (e: Exception) {
            null
        }

        val texto = StringBuilder()

        texto.append("🚀 ADA-LINK RAY-X\n\n")

        texto.append(Sistema.texto())
        texto.append("\n\n")

        texto.append(Gnss.texto(lm))
        texto.append("\n\n")

        texto.append("🛰️ NTN ORBIT\n\n")

        if (local != null) {

            val sat = NTNOrbit.posicao(0.0)

            texto.append(
                "Latitude: %.6f°\n".format(
                    local.latitude
                )
            )

            texto.append(
                "Longitude: %.6f°\n".format(
                    local.longitude
                )
            )

            texto.append(
                "Altitude: %.1f m\n\n".format(
                    local.altitude
                )
            )

            texto.append("Satélite virtual: 600 km\n")
            texto.append(
                "Velocidade: %.3f km/s\n\n".format(
                    NTNOrbit.velocidadeOrbital()
                )
            )

            texto.append(
                "Posição orbital:\n" +
                "X: %.2f km\n".format(sat[0]) +
                "Y: %.2f km\n".format(sat[1]) +
                "Z: %.2f km\n\n".format(sat[2])
            )

            texto.append("🟡 SIMULAÇÃO NTN\n")
            texto.append("Nenhum sinal RF foi transmitido.")

        } else {

            texto.append(
                "Aguardando posição GNSS..."
            )
        }

        tela.text = texto.toString()
    }
}
