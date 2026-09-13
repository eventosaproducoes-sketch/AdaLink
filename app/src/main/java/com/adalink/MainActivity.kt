package com.adalink

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var status: TextView
    private lateinit var locationManager: LocationManager

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

        status = TextView(this)
        status.text = "Afro-Connect Inteligente\n\nVerificando GNSS..."
        status.textSize = 18f
        status.gravity = Gravity.CENTER
        status.setPadding(0, 40, 0, 0)

        layout.addView(titulo)
        layout.addView(status)

        setContentView(layout)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        verificarPermissao()
    }

    private fun verificarPermissao() {

        if (checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )

            status.text = "AdaLink\n\nAguardando permissão de localização..."
            return
        }

        iniciarGNSS()
    }

    private fun iniciarGNSS() {

        status.text = "AdaLink\n\nGNSS ativo.\nAguardando localização..."

        try {

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000L,
                1f,
                object : LocationListener {

                    override fun onLocationChanged(location: Location) {

                        val latitude =
                            String.format("%.6f", location.latitude)

                        val longitude =
                            String.format("%.6f", location.longitude)

                        status.text =
                            "AdaLink\n\n" +
                            "GNSS conectado\n\n" +
                            "Latitude: $latitude\n" +
                            "Longitude: $longitude\n\n" +
                            "Sistema pronto para pesquisa de conectividade."
                    }
                }
            )

        } catch (e: SecurityException) {

            status.text =
                "AdaLink\n\nNão foi possível acessar o GNSS."
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == 100) {

            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {

                iniciarGNSS()

            } else {

                status.text =
                    "AdaLink\n\n" +
                    "Permissão de localização não concedida."
            }
        }
    }
}
