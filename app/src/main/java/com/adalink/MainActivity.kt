package com.adalink

import android.Manifest
import android.app.AlertDialog
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import android.os.Handler
import android.content.Intent
import android.widget.*
import android.os.Bundle



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
        val backup = Button(this)
        backup.text = "🔐 BACKUP ADA RESERVOIR"
        tela.addView(backup)

        backup.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.type = "application/json"
            intent.putExtra(
                Intent.EXTRA_TITLE,
                "AdaReservoir_Backup.json"
            )
            startActivityForResult(intent, 2001)
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
                val packetEngine = Button(this)
        packetEngine.text = "⚙️ ADA PACKET ENGINE"
        tela.addView(packetEngine)

        packetEngine.setOnClickListener {
            Toast.makeText(
                this,
                PacketEngine.resumo(this),
                Toast.LENGTH_LONG
            ).show()
        }
        val radio = Button(this)
        radio.text = "📡 RADIO ENVIRONMENT"
        tela.addView(radio)

        radio.setOnClickListener {
        Toast.makeText(
        this,
        RadioEnvironment.analisar(this),
        Toast.LENGTH_LONG
        ).show()
       }
                val capabilityMap = Button(this)
        capabilityMap.text = "🧠 ADA CAPABILITY MAP"
        tela.addView(capabilityMap)

        capabilityMap.setOnClickListener {
            val relatorio = CapabilityMap.relatorio(this@MainActivity)

            AlertDialog.Builder(this@MainActivity)
                .setTitle("🧠 ADA CAPABILITY MAP")
                .setMessage(relatorio)
                .setPositiveButton("FECHAR", null)
                .show()
        }
                val requirementModel = Button(this)
        requirementModel.text = "🧠 ADA REQUIREMENT MODEL"
        tela.addView(requirementModel)

        requirementModel.setOnClickListener {
            AlertDialog.Builder(this@MainActivity)
                .setTitle("🧠 ADA REQUIREMENT MODEL")
                .setMessage(RequirementModel.resumo())
                .setPositiveButton("FECHAR", null)
                .show()
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
        override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2001 &&
            resultCode == RESULT_OK
        ) {
            val uri = data?.data

            if (uri != null) {
                try {
                    val conteudo =
                        DataReservoir.dadosDoReservatorio(this)

                    contentResolver
                        .openOutputStream(uri)
                        ?.use { saida ->
                            saida.write(
                                conteudo.toByteArray(
                                    Charsets.UTF_8
                                )
                            )
                        }

                    Toast.makeText(
                        this,
                        "🔐 BACKUP CRIADO\n" +
                        "Pacotes: ${DataReservoir.quantidade(this)}\n" +
                        "Dados: ${DataReservoir.tamanhoTotal(this)} bytes",
                        Toast.LENGTH_LONG
                    ).show()

                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "⚠️ Erro ao criar backup",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        }
}
