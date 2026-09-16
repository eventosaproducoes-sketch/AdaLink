package com.adalink

import android.app.Activity
import android.os.*
import android.content.*
import android.graphics.Color
import android.location.*
import android.net.*
import android.net.wifi.WifiManager
import android.bluetooth.BluetoothAdapter
import android.widget.*

class MainActivity : Activity() {

    lateinit var info: TextView
    lateinit var lm: LocationManager

    var sats = 0
    var used = 0

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        val tela = LinearLayout(this)
        tela.orientation = LinearLayout.VERTICAL
        tela.setBackgroundColor(Color.BLACK)
        tela.setPadding(20,20,20,20)

        val titulo = TextView(this)
        titulo.text = "ADA LINK — RAY-X"
        titulo.textSize = 22f
        titulo.setTextColor(Color.WHITE)
        titulo.gravity = android.view.Gravity.CENTER
        tela.addView(titulo)

        val pdf = Button(this)
        pdf.text = "📄 GERAR PDF DO DIAGNÓSTICO"
        tela.addView(pdf)

        info = TextView(this)
        info.textSize = 16f
        info.setTextColor(Color.WHITE)
        info.text = "\nPreparando diagnóstico..."
        tela.addView(info)
        pdf.setOnClickListener {
    gerarPDF()
        }

        setContentView(tela)

        lm = getSystemService(LOCATION_SERVICE)
            as LocationManager

        analisar()
    }
        fun analisar() {

        info.text = "\n🔎 ANALISANDO...\n"

        try {
            val gps = lm.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )

            info.text =
                "📡 RAY-X ATIVO\n\n" +
                "GNSS/GPS: " +
                if (gps) "ATIVO" else "DESATIVADO"
            
            val cb = object : GnssStatus.Callback() {

                override fun onSatelliteStatusChanged(
                    s: GnssStatus
                ) {
                    sats = s.satelliteCount
                    used = 0

                    for (i in 0 until sats) {
                        if (s.usedInFix(i)) used++
                    }

                    info.text =
                        "📡 RAY-X — RESULTADO\n\n" +
                        "🛰️ Satélites
                        fun gerarPDF() {

        val nome = "AdaLink_RayX.pdf"

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_TITLE, nome)

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (requestCode == 100 &&
            resultCode == RESULT_OK &&
            data?.data != null
        ) {

            val uri = data.data!!

            try {

                val documento =
                    android.graphics.pdf.PdfDocument()

                val pagina =
                    documento.startPage(
                        android.graphics.pdf.PdfDocument
                            .PageInfo
                            .Builder(595, 842, 1)
                            .create()
                    )

                val canvas = pagina.canvas

                val tinta =
                    android.graphics.Paint()

                tinta.color = Color.BLACK
                tinta.textSize = 22f

                canvas.drawText(
                    "ADA LINK — RAY-X",
                    40f,
                    60f,
                    tinta
                )

                tinta.textSize = 16f

                canvas.drawText(
                    "RELATÓRIO DE DIAGNÓSTICO",
                    40f,
                    95f,
                    tinta
                )

                tinta.textSize = 14f

                canvas.drawText(
                    "GNSS/GPS: ATIVO",
                    40f,
                    145f,
                    tinta
                )

                canvas.drawText(
                    "Satélites detectados: $sats",
                    40f,
                    180f,
                    tinta
                )

                canvas.drawText(
                    "Satélites usados no fix: $used",
                    40f,
                    215f,
                    tinta
                )

                canvas.drawText(
                    "Fabricante: ${Build.MANUFACTURER}",
                    40f,
                    265f,
                    tinta
                )

                canvas.drawText(
                    "Modelo: ${Build.MODEL}",
                    40f,
                    300f,
                    tinta
                )

                canvas.drawText(
                    "Android: ${Build.VERSION.RELEASE}",
                    40f,
                    335f,
                    tinta
                )

                documento.finishPage(pagina)

                val saida =
                    contentResolver.openOutputStream(uri)

                documento.writeTo(saida!!)

                saida.close()
                documento.close()

                Toast.makeText(
                    this,
                    "PDF gerado com sucesso!",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {

                Toast.makeText(
                    this,
                    "Erro ao gerar PDF",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
                }
                
