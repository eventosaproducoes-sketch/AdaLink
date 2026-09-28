package com.adalink

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.IBinder
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import java.net.DatagramSocket
import java.net.InetSocketAddress
class AdaVpnService : VpnService() {

    companion object {

        const val ACTION_START =
            "ADALINK_VPN_START"

        const val ACTION_STOP =
            "ADALINK_VPN_STOP"

        const val ACTION_STATUS =
            "ADALINK_VPN_STATUS"

        private const val CHANNEL_ID =
            "ADALINK_VPN_CHANNEL"

        private const val NOTIFICATION_ID =
            41001

        private const val VPN_ADDRESS =
            "10.77.0.1"

        private const val VPN_PREFIX =
            24

        @Volatile
        var tunInterface =
            null as android.os.ParcelFileDescriptor?

        @Volatile
        var tunInput =
            null as FileInputStream?

        @Volatile
        var tunOutput =
            null as FileOutputStream?

        @Volatile
        var interfaceCriada =
            false

        @Volatile
        var ipInterno =
            VPN_ADDRESS

        @Volatile
        var pacotesLidos =
            0

        @Volatile
        var pacotesEscritos =
            0

        @Volatile
        var bytesLidos =
            0L

        @Volatile
        var bytesEscritos =
            0L

        private val executando =
            AtomicBoolean(false)

        fun estaExecutando(): Boolean {
            return executando.get()
        }

        fun status(): String {

            return buildString {

                appendLine("ADALINK VPN/TUN")
                appendLine()
                appendLine(
                    "Executando: ${executando.get()}"
                )
                appendLine(
                    "Interface criada: $interfaceCriada"
                )
                appendLine(
                    "IP interno: $ipInterno"
                )
                appendLine(
                    "Pacotes lidos: $pacotesLidos"
                )
                appendLine(
                    "Pacotes escritos: $pacotesEscritos"
                )
                appendLine(
                    "Bytes lidos: $bytesLidos"
                )
                appendLine(
                    "Bytes escritos: $bytesEscritos"
                )
            }
        }
    }

    private var threadLeitura: Thread? = null

    override fun onCreate() {
        super.onCreate()

        criarCanalNotificacao()

        iniciarForeground()

        criarInterfaceTun()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_START -> {

                if (!interfaceCriada) {
                    criarInterfaceTun()
                }
            }

            ACTION_STOP -> {

                encerrarTun()

                stopForeground(true)

                stopSelf()
            }
        }

        return START_NOT_STICKY
    }
private fun criarInterfaceTun() {

    if (interfaceCriada) {
        return
    }

    try {

        val builder =
            Builder()
                .setSession("AdaLink Connectivity Lab")
                .addAddress(
                    VPN_ADDRESS,
                    VPN_PREFIX
                )
        .addRoute(
            "10.77.0.2",
            32
        )
        /*
         * IMPORTANTE:
         *
         * Não adicionamos:
         *
         * addRoute("0.0.0.0", 0)
         *
         * Portanto o laboratório não assume
         * automaticamente todo o tráfego
         * de Internet do aparelho.
         */

        val tun =
            builder.establish()

        if (tun == null) {

            interfaceCriada = false
            executando.set(false)

            android.util.Log.e(
                "AdaLinkVPN",
                "ERRO: Builder.establish() retornou null"
            )

            return
        }

        tunInterface = tun

        val descriptor =
            tun.fileDescriptor

        tunInput =
            FileInputStream(descriptor)

        tunOutput =
            FileOutputStream(descriptor)

        interfaceCriada = true

        android.util.Log.i(
            "AdaLinkVPN",
            "TUN CRIADO COM SUCESSO"
        )

        android.util.Log.i(
            "AdaLinkVPN",
            "IP INTERNO: $VPN_ADDRESS/$VPN_PREFIX"
        )

        executando.set(true)

        iniciarLeitura()

    } catch (e: Exception) {

        android.util.Log.e(
            "AdaLinkVPN",
            "EXCEÇÃO AO CRIAR TUN",
            e
        )

        interfaceCriada = false

        executando.set(false)

        try {
            tunInterface?.close()
        } catch (_: Exception) {
        }

        tunInterface = null
        tunInput = null
        tunOutput = null
    }
}
    private fun iniciarLeitura() {

        if (threadLeitura != null) {
            return
        }

        threadLeitura =
            Thread {

                val buffer =
                    ByteBuffer.allocate(32767)

                while (executando.get()) {

                    try {

                        val input =
                            tunInput ?: break

                        buffer.clear()

                        val quantidade =
                            input.read(buffer.array())

                        if (quantidade > 0) {

                            pacotesLidos++

                            bytesLidos +=
                                quantidade.toLong()
                                                        val pacote =
                            buffer.array()
                                .copyOfRange(
                                    0,
                                    quantidade
                                )

                        if (pacote.size >= 20) {

                            val versao =
                                (pacote[0].toInt() ushr 4) and 0x0F

                            if (versao == 4) {

                                val protocolo =
                                    pacote[9].toInt() and 0xFF

                                val destino =
                                    "${pacote[16].toInt() and 0xFF}." +
                                    "${pacote[17].toInt() and 0xFF}." +
                                    "${pacote[18].toInt() and 0xFF}." +
                                    "${pacote[19].toInt() and 0xFF}"

                                android.util.Log.i(
                                    "AdaLinkVPN",
                                    "PACOTE IPv4 RECEBIDO | " +
                                    "bytes=${pacote.size} | " +
                                    "protocolo=$protocolo | " +
                                    "destino=$destino"
                                )
                            }
                        }
                        }

                    } catch (e: Exception) {

                        if (executando.get()) {
                            break
                        }
                    }
                }

            }.apply {

                name =
                    "AdaLink-TUN-Reader"

                isDaemon = true

                start()
            }
    }

    fun escreverPacote(
        pacote: ByteArray
    ): Boolean {

        if (!interfaceCriada) {
            return false
        }

        return try {

            val output =
                tunOutput ?: return false

            output.write(pacote)

            output.flush()

            pacotesEscritos++

            bytesEscritos +=
                pacote.size.toLong()

            true

        } catch (e: Exception) {

            false
        }
    }

    private fun encerrarTun() {

        executando.set(false)

        threadLeitura = null

        try {
            tunInput?.close()
        } catch (_: Exception) {
        }

        try {
            tunOutput?.close()
        } catch (_: Exception) {
        }

        try {
            tunInterface?.close()
        } catch (_: Exception) {
        }

        tunInput = null
        tunOutput = null
        tunInterface = null

        interfaceCriada = false
    }

    override fun onDestroy() {

        encerrarTun()

        super.onDestroy()
    }

    override fun onRevoke() {

        encerrarTun()

        super.onRevoke()
    }

    private fun criarCanalNotificacao() {

        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val manager =
                getSystemService(
                    NotificationManager::class.java
                )

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "AdaLink Connectivity Lab",
                    NotificationManager.IMPORTANCE_LOW
                )

            manager.createNotificationChannel(
                channel
            )
        }
    }

    private fun iniciarForeground() {

        val notification =
            criarNotificacao()

        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.Q
        ) {

            startForeground(
                NOTIFICATION_ID,
                notification
            )

        } else {

            startForeground(
                NOTIFICATION_ID,
                notification
            )
        }
    }

    private fun criarNotificacao(): Notification {

        return if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            Notification.Builder(
                this,
                CHANNEL_ID
            )
                .setContentTitle(
                    "AdaLink Connectivity Lab"
                )
                .setContentText(
                    "Laboratório VPN/TUN ativo"
                )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setOngoing(true)
                .build()

        } else {

            Notification.Builder(this)
                .setContentTitle(
                    "AdaLink Connectivity Lab"
                )
                .setContentText(
                    "Laboratório VPN/TUN ativo"
                )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setOngoing(true)
                .build()
        }
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? {

        return super.onBind(intent)
    }
}
