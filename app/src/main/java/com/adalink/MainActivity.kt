package com.adalink

import android.Manifest
import android.app.Activity
import android.os.*
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.location.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.StatFs
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
        titulo.gravity = android.view.Gravity.CENTER

        tela.addView(titulo)

        // PRIMEIRO BOTÃO DA PÁGINA
        val pdf = Button(this)
        pdf.text = "📄 GERAR PDF DO DIAGNÓSTICO"

        tela.addView(pdf)

        pdf.setOnClickListener {
            gerarPDF()
        }

        info = TextView(this)
        info.textSize = 16f
        info.setTextColor(Color.WHITE)
        info.text = "\nPreparando diagnóstico..."

        tela.addView(info)

        setContentView(tela)

        lm = getSystemService(LOCATION_SERVICE)
            as LocationManager

        if (checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                10
            )

        } else {
            analisar()
        }
    }
        fun analisar() {

        info.text = "🔎 ANALISANDO RAY-X...\n\nAguarde."

        gps = try {
            lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            false
        }

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
                    "ADA LINK — RAY-X\n\n" +
                    "🛰️ GNSS/GPS: " +
                    if (gps) "ATIVO" else "DESATIVADO" +
                    "\n\n" +
                    "Satélites: $sats\n" +
                    "Usados no fix: $used\n\n" +
                    "📱 Aparelho: ${Build.MODEL}\n" +
                    "🤖 Android: ${Build.VERSION.RELEASE}"
            }
        }

        try {
            lm.registerGnssStatusCallback(
                cb,
                Handler(Looper.getMainLooper())
            )
        } catch (e: Exception) {
            info.text =
                "⚠️ Não foi possível iniciar o GNSS."
        }
        }
            fun gerarPDF() {

        val i = Intent(Intent.ACTION_CREATE_DOCUMENT)

        i.type = "application/pdf"
        i.putExtra(
            Intent.EXTRA_TITLE,
            "AdaLink_RayX.pdf"
        )

        startActivityForResult(i, 100)
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
            data?.data == null) {
            return
        }

        try {

            val pdf = PdfDocument()

            val pagina = pdf.startPage(
                PdfDocument.PageInfo.Builder(
                    595, 842, 1
                ).create()
            )

            val canvas = pagina.canvas

            val p = Paint()
            p.color = Color.BLACK
            p.textSize = 22f

            canvas.drawText(
                "ADA LINK — RAY-X",
                40f, 60f, p
            )

            p.textSize = 16f

            canvas.drawText(
                "RELATÓRIO DE DIAGNÓSTICO",
                40f, 95f, p
            )

            p.textSize = 14f

            canvas.drawText(
                "GNSS/GPS: " +
                    if (gps) "ATIVO" else "DESATIVADO",
                40f, 145f, p
            )

            canvas.drawText(
                "Satélites detectados: $sats",
                40f, 180f, p
            )

            canvas.drawText(
                "Satélites usados no fix: $used",
                40f, 215f, p
            )

            canvas.drawText(
                "Fabricante: ${Build.MANUFACTURER}",
                40f, 265f, p
            )

            canvas.drawText(
                "Modelo: ${Build.MODEL}",
                40f, 300f, p
                            canvas.drawText(
                "Android: ${Build.VERSION.RELEASE}",
                40f, 335f, p
            )

            pdf.finishPage(pagina)

            val saida =
                contentResolver.openOutputStream(
                    data.data!!
                )

            pdf.writeTo(saida!!)
            saida.close()
            pdf.close()

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
