package com.adalink

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.location.GnssStatus
import android.location.LocationManager
import android.widget.*

class MainActivity : Activity() {

    lateinit var info: TextView
    lateinit var lm: LocationManager

    var sats = 0
    var used = 0
    var gps = false

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        val tela = LinearLayout(this)
        tela.orientation = LinearLayout.VERTICAL
        tela.setBackgroundColor(Color.BLACK)
        tela.setPadding(20, 20, 20, 20)

        val titulo = TextView(this)
        titulo.text = "ADA LINK — RAY-X"
        titulo.textSize = 22f
        titulo.setTextColor(Color.WHITE)
        titulo.gravity = 17
        tela.addView(titulo)

        // BOTÃO DEVE FICAR NO INÍCIO DA PÁGINA
        val pdf = Button(this)
        pdf.text = "📄 GERAR PDF DO DIAGNÓSTICO"
        tela.addView(pdf)

        pdf.setOnClickListener {
            gerarPDF()
        }

        info = TextView(this)
        info.textSize = 16f
        info.setTextColor(Color.WHITE)
        info.text = "\nIniciando diagnóstico..."
        tela.addView(info)

        setContentView(tela)

        lm = getSystemService(LOCATION_SERVICE)
            as LocationManager

        iniciar()
    }
        fun iniciar() {

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
            return
        }

        gps = try {
            lm.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )
        } catch (e: Exception) {
            false
        }

        info.text =
            "🔎 RAY-X ANALISANDO...\n\n" +
            "GNSS/GPS: " +
            if (gps) "ATIVO" else "DESATIVADO" +
            "\n\n" +
            "📱 ${Build.MANUFACTURER} ${Build.MODEL}" +
            "\n🤖 Android ${Build.VERSION.RELEASE}"

        val callback = object : GnssStatus.Callback() {

            override fun onSatelliteStatusChanged(
                status: GnssStatus
            ) {
                sats = status.satelliteCount
                used = 0

                for (i in 0 until sats) {
                    if (status.usedInFix(i)) {
                        used++
                    }
                }

                info.text =
                    "ADA LINK — RAY-X\n\n" +
                    "🛰️ Satélites: $sats\n" +
                    "🎯 Usados no fix: $used\n" +
                    "📡 GNSS: " +
                    if (gps) "ATIVO" else "DESATIVADO" +
                    "\n\n" +
                    "📱 ${Build.MANUFACTURER} ${Build.MODEL}" +
                    "\n🤖 Android ${Build.VERSION.RELEASE}"
            }
        }

        try {
            lm.registerGnssStatusCallback(
                callback,
                Handler(Looper.getMainLooper())
            )

            Handler(Looper.getMainLooper()).postDelayed
                fun gerarPDF() {

        val escolher = Intent(
            Intent.ACTION_CREATE_DOCUMENT
        )

        escolher.type = "application/pdf"
        escolher.putExtra(
            Intent.EXTRA_TITLE,
            "AdaLink_RayX.pdf"
        )

        startActivityForResult(
            escolher,
            100
        )
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

        if (requestCode != 100 ||
            resultCode != RESULT_OK ||
            data?.data == null
        ) {
            return
        }

        try {

            val documento = PdfDocument()

            val pagina = documento.startPage(
                PdfDocument.PageInfo.Builder(
                    595,
                    842,
                    1
                ).create()
            )

            val canvas = pagina.canvas

            val tinta = Paint()
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
                "GNSS/GPS: " +
                    if (gps) "ATIVO" else "DESATIVADO",
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
                contentResolver.openOutputStream(
                    data.data!!
                )

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
