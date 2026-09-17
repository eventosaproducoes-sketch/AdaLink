package com.adalink

import android.Manifest
import android.app.Activity
import android.os.*
import android.content.*
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.location.*
import android.net.*
import android.net.wifi.WifiManager
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

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
        tela.setPadding(20,20,20,20)

        val titulo = TextView(this)
        titulo.text = "ADA LINK — RAY-X"
        titulo.textSize = 24f
        titulo.setTextColor(Color.WHITE)
        titulo.gravity = 17
        tela.addView(titulo)

        // BOTÃO PRINCIPAL: PRIMEIRO ELEMENTO INTERATIVO
        val pdf = Button(this)
        pdf.text = "📄 GERAR PDF DO DIAGNÓSTICO"
        pdf.textSize = 16f
        tela.addView(pdf)

        pdf.setOnClickListener {
            gerarPDF()
        }

        info = TextView(this)
        info.textSize = 15f
        info.setTextColor(Color.WHITE)
        info.text = "🔎 Iniciando Ray-X..."
        tela.addView(info)

        setContentView(tela)

        lm = getSystemService(LOCATION_SERVICE)
            as LocationManager

        analisar()
    }

    fun analisar() {

        gps = try {
            lm.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )
        } catch (e: Exception) {
            false
        }

        val rede = try {
            val cm = getSystemService(
                CONNECTIVITY_SERVICE
            ) as ConnectivityManager

            val n = cm.activeNetwork
            val c = cm.getNetworkCapabilities(n)

            when {
                c == null -> "SEM CONEXÃO"
                c.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI
                ) -> "WI-FI"
                c.hasTransport(
                    NetworkCapabilities.TRANSPORT_CELLULAR
                ) -> "REDE MÓVEL"
                else -> "OUTRA"
            }
        } catch (e: Exception) {
            "NÃO IDENTIFICADA"
        }

        val wifi = try {
            val wm = applicationContext.getSystemService(
                WIFI_SERVICE
            ) as WifiManager
            if (wm.isWifiEnabled) "ATIVO"
            else "DESATIVADO"
        } catch (e: Exception) {
            "INDISPONÍVEL"
        }

        val stat = StatFs(filesDir.path)
        val total = stat.totalBytes / 1073741824
        val livre = stat.availableBytes / 1073741824

        val bateria = registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        
                val nivel = bateria?.getIntExtra(
            "level",
            -1
        ) ?: -1

        info.text =
            "📡 RAY-X\n\n" +
            "🛰️ GNSS/GPS: " +
            if (gps) "ATIVO" else "DESATIVADO" +
            "\n📶 Conexão: $rede" +
            "\n📡 Wi-Fi: $wifi" +
            "\n💾 Armazenamento: $livre GB livres / $total GB" +
            "\n🔋 Bateria: $nivel%" +
            "\n\n📱 Fabricante: ${Build.MANUFACTURER}" +
            "\n📱 Modelo: ${Build.MODEL}" +
            "\n🤖 Android: ${Build.VERSION.RELEASE}"

        if (
            checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            iniciarGNSS()
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                10
            )
        }
    }

    fun iniciarGNSS() {

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

                                val detalhes = StringBuilder()

                detalhes.append(
                    "🛰️ ADA LINK — RAY-X\n\n"
                )

                detalhes.append(
                    "Satélites detectados: ${status.satelliteCount}\n"
                )

                detalhes.append(
                    "Usados no fix: $used\n\n"
                )

                for (i in 0 until status.satelliteCount) {

                    detalhes.append(
                        "SV ${status.getSvid(i)} | " +
                        "Const ${status.getConstellationType(i)}\n"
                    )

                    detalhes.append(
                        "C/N0: %.1f dB-Hz\n".format(
                            status.getCn0DbHz(i)
                        )
                    )

                    detalhes.append(
                        "Az: %.1f° | El: %.1f°\n".format(
                            status.getAzimuthDegrees(i),
                            status.getElevationDegrees(i)
                        )
                    )

                    if (Build.VERSION.SDK_INT >= 26) {
                        detalhes.append(
                            "Freq: %.3f MHz\n".format(
                                status.getCarrierFrequencyHz(i) /
                                    1_000_000.0
                            )
                        )
                    }

                    detalhes.append(
                        "Usado no fix: ${status.usedInFix(i)}\n\n"
                    )
                }

                info.text = detalhes.toString()
            }
        }

        try {
            lm.registerGnssStatusCallback(
                callback,
                Handler(Looper.getMainLooper())
            )

            
lm.requestLocationUpdates(
    LocationManager.GPS_PROVIDER,
    1000L,
    0f,
    object : LocationListener {
        override fun onLocationChanged(location: Location) {
        }
    }
)
        } catch (e: Exception) {
        }
    }

    fun gerarPDF() {

        val escolha = Intent(
            Intent.ACTION_CREATE_DOCUMENT
        )

        escolha.type = "application/pdf"
        escolha.putExtra(
            Intent.EXTRA_TITLE,
            "AdaLink_RayX.pdf"
        )

        startActivityForResult(
            escolha,
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

        if (
            requestCode != 100 ||
            resultCode != RESULT_OK ||
            data?.data == null
        ) return

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
            val p = Paint()

            p.color = Color.BLACK
            p.textSize = 22f

            canvas.drawText(
                "ADA LINK — RAY-X",
                40f,
                60f,
                p
            )

            p.textSize = 16f

            canvas.drawText(
                "RELATÓRIO DE DIAGNÓSTICO",
                40f,
                95f,
                p
            )

            p.textSize = 14f

            canvas.drawText(
                "GNSS/GPS: " +
                    if (gps) "ATIVO" else "DESATIVADO",
                40f,
                160f,
                p
            )

            canvas.drawText(
                "Satélites detectados: $sats",
                40f,
                195f,
                p
            )

            canvas.drawText(
                "Satélites usados no fix: $used",
                40f,
                230f,
                p
            )

            canvas.drawText(
                "Fabricante: ${Build.MANUFACTURER}",
                40f,
                275f,
                p
            )

            canvas.drawText(
                "Modelo: ${Build.MODEL}",
                40f,
                310f,
                p
            )

            canvas.drawText(
                "Android: ${Build.VERSION.RELEASE}",
                40f,
                345f,
                p
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
