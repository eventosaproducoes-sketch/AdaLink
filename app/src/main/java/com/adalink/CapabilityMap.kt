package com.adalink

import android.app.ActivityManager
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.VpnService
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.wifi.WifiManager

data class AdaCapability(
    val id: String,
    val nome: String,
    val estado: String,
    val fonte: String,
    val evidencia: String,
    val detalhes: String
)

object CapabilityMap {

    private fun capacidade(
        id: String,
        nome: String,
        estado: String,
        fonte: String,
        evidencia: String,
        detalhes: String
    ): AdaCapability {
        return AdaCapability(
            id = id,
            nome = nome,
            estado = estado,
            fonte = fonte,
            evidencia = evidencia,
            detalhes = detalhes
        )
    }
        private fun cpu(context: Context): AdaCapability {

        val processadores = Runtime.getRuntime().availableProcessors()

        return capacidade(
            id = "C001",
            nome = "CPU / PROCESSAMENTO",
            estado = "DETECTADO",
            fonte = "Android Runtime",
            evidencia = "availableProcessors()",
            detalhes = "Processadores disponíveis para a aplicação: $processadores"
        )
    }

    private fun ram(context: Context): AdaCapability {

        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val info = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(info)

        val totalMb = info.totalMem / (1024 * 1024)
        val disponivelMb = info.availMem / (1024 * 1024)

        return capacidade(
            id = "C002",
            nome = "RAM / MEMÓRIA",
            estado = "DETECTADO",
            fonte = "ActivityManager",
            evidencia = "MemoryInfo",
            detalhes = "RAM total: ${totalMb} MB | Disponível: ${disponivelMb} MB"
        )
    }
        private fun armazenamento(context: Context): AdaCapability {

        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)

        val total = stat.totalBytes
        val livre = stat.availableBytes
        val usado = total - livre

        val totalGb = total / (1024.0 * 1024.0 * 1024.0)
        val usadoGb = usado / (1024.0 * 1024.0 * 1024.0)
        val livreGb = livre / (1024.0 * 1024.0 * 1024.0)

        return capacidade(
            id = "C003",
            nome = "ARMAZENAMENTO",
            estado = "DETECTADO",
            fonte = "StatFs",
            evidencia = "Armazenamento interno acessível",
            detalhes = String.format(
                "Total: %.2f GB | Usado: %.2f GB | Livre: %.2f GB",
                totalGb,
                usadoGb,
                livreGb
            )
        )
    }
            private fun gnss(context: Context): AdaCapability {

        val location =
            context.packageManager.hasSystemFeature(
                PackageManager.FEATURE_LOCATION_GPS
            )

        return if (location) {
            capacidade(
                id = "C004",
                nome = "GNSS",
                estado = "DETECTADO",
                fonte = "PackageManager / GNSS",
                evidencia = "FEATURE_LOCATION_GPS + testes reais",
                detalhes = "GNSS presente no aparelho e já observado pelo AdaLink."
            )
        } else {
            capacidade(
                id = "C004",
                nome = "GNSS",
                estado = "NÃO DETECTADO",
                fonte = "PackageManager",
                evidencia = "FEATURE_LOCATION_GPS",
                detalhes = "Recurso GPS não identificado."
            )
        }
    }
                private fun gnssRaw(context: Context): AdaCapability {

        val suportaRaw = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

        return if (suportaRaw) {
            capacidade(
                id = "C005",
                nome = "GNSS RAW",
                estado = "DISPONÍVEL PARA INVESTIGAÇÃO",
                fonte = "GnssMeasurementsEvent",
                evidencia = "Android API ${Build.VERSION.SDK_INT}",
                detalhes = "API compatível com investigação de medições GNSS RAW."
            )
        } else {
            capacidade(
                id = "C005",
                nome = "GNSS RAW",
                estado = "NÃO DISPONÍVEL",
                fonte = "Android",
                evidencia = "API",
                detalhes = "API insuficiente para GnssMeasurementsEvent."
            )
        }
    }
                    private fun celular(context: Context): AdaCapability {

        val pm = context.packageManager

        val possuiTelefonia =
            pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)

        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager

        val network = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(network)

        val celularAtivo =
            capabilities?.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            ) == true

        val detalhes =
            if (celularAtivo) {
                "Telefonia disponível e transporte celular atualmente ativo."
            } else {
                "Recurso de telefonia identificado: $possuiTelefonia | Transporte celular ativo: não observado."
            }

        return capacidade(
            id = "C006",
            nome = "REDE CELULAR",
            estado = if (possuiTelefonia) "DETECTADO" else "NÃO DETECTADO",
            fonte = "PackageManager / ConnectivityManager",
            evidencia = "FEATURE_TELEPHONY + TRANSPORT_CELLULAR",
            detalhes = detalhes
        )
    }
                        private fun wifi(context: Context): AdaCapability {

        val pm = context.packageManager

        val possuiWifi =
            pm.hasSystemFeature(PackageManager.FEATURE_WIFI)

        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager

        val network = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(network)

        val wifiAtivo =
            capabilities?.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            ) == true

        return capacidade(
            id = "C007",
            nome = "WI-FI",
            estado = if (possuiWifi) "DETECTADO" else "NÃO DETECTADO",
            fonte = "PackageManager / ConnectivityManager",
            evidencia = "FEATURE_WIFI + TRANSPORT_WIFI",
            detalhes = "Wi-Fi presente: $possuiWifi | Wi-Fi ativo: $wifiAtivo"
        )
                        }
                            private fun bluetooth(context: Context): AdaCapability {

        val pm = context.packageManager

        val possuiBluetooth =
            pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)

        val adapter =
            BluetoothAdapter.getDefaultAdapter()

        val ativo =
            adapter?.isEnabled == true

        return capacidade(
            id = "C008",
            nome = "BLUETOOTH",
            estado = if (possuiBluetooth) "DETECTADO" else "NÃO DETECTADO",
            fonte = "PackageManager / BluetoothAdapter",
            evidencia = "FEATURE_BLUETOOTH",
            detalhes = "Bluetooth presente: $possuiBluetooth | Estado ativo observado: $ativo"
        )
                            }
                                private fun sensores(context: Context): AdaCapability {

        val sensorManager =
            context.getSystemService(Context.SENSOR_SERVICE)
                    as SensorManager

        val sensores =
            sensorManager.getSensorList(Sensor.TYPE_ALL)

        val nomes =
            sensores.joinToString(", ") {
                it.name
            }

        return capacidade(
            id = "C009",
            nome = "SENSORES",
            estado = if (sensores.isNotEmpty()) "DETECTADO" else "NÃO DETECTADO",
            fonte = "SensorManager",
            evidencia = "TYPE_ALL",
            detalhes = "Sensores encontrados: ${sensores.size}\n$nomes"
        )
                                }
                                    private fun bateria(context: Context): AdaCapability {

        val batteryManager =
            context.getSystemService(Context.BATTERY_SERVICE)
                    as android.os.BatteryManager

        val nivel =
            batteryManager.getIntProperty(
                android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY
            )

        return capacidade(
            id = "C010",
            nome = "BATERIA / ENERGIA",
            estado = "DETECTADO",
            fonte = "BatteryManager",
            evidencia = "BATTERY_PROPERTY_CAPACITY",
            detalhes = "Nível de bateria observado: $nivel%"
        )
                                    }
                private fun interfacesRede(context: Context): AdaCapability {

        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager

        val network = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(network)

        if (capabilities == null) {
            return capacidade(
                id = "C011",
                nome = "INTERFACES DE REDE",
                estado = "SEM REDE ATIVA",
                fonte = "ConnectivityManager",
                evidencia = "activeNetwork",
                detalhes = "Nenhuma rede ativa foi observada neste momento."
            )
        }

        val lista = mutableListOf<String>()

        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            lista.add("CELLULAR")
        }

        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            lista.add("WIFI")
        }

        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
            lista.add("BLUETOOTH")
        }

        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
            lista.add("VPN")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                lista.add("ETHERNET")
            }
        }

        return capacidade(
            id = "C011",
            nome = "INTERFACES DE REDE",
            estado = "DETECTADO",
            fonte = "ConnectivityManager",
            evidencia = "NetworkCapabilities",
            detalhes = "Transportes observados: ${lista.joinToString(", ")}"
        )
                }
                    private fun dataReservoir(context: Context): AdaCapability {

        return try {

            val quantidade = DataReservoir.quantidade(context)
            val tamanho = DataReservoir.tamanhoTotal(context)

            capacidade(
                id = "C012",
                nome = "ARMAZENAMENTO PERSISTENTE / DATA RESERVOIR",
                estado = "IMPLEMENTADO",
                fonte = "DataReservoir",
                evidencia = "DataReservoir.quantidade() / tamanhoTotal()",
                detalhes = "Registros preservados: $quantidade | Dados: $tamanho bytes"
            )

        } catch (e: Exception) {

            capacidade(
                id = "C012",
                nome = "ARMAZENAMENTO PERSISTENTE / DATA RESERVOIR",
                estado = "ERRO DE LEITURA",
                fonte = "DataReservoir",
                evidencia = "Exceção durante consulta",
                detalhes = e.message ?: "Erro desconhecido"
            )
        }
                    }
                        private fun packetEngine(context: Context): AdaCapability {

        return try {

            val resultado =
                PacketEngine.analisar(context)

            capacidade(
                id = "C013",
                nome = "PROCESSAMENTO DE PACOTES",
                estado = "IMPLEMENTADO",
                fonte = "PacketEngine",
                evidencia = "PacketEngine.analisar()",
                detalhes =
                    "Pacotes: ${resultado.pacotes} | " +
                    "Bytes: ${resultado.bytes} | " +
                    "Fontes: ${resultado.fontes} | " +
                    "Tipos: ${resultado.tipos}"
            )

        } catch (e: Exception) {

            capacidade(
                id = "C013",
                nome = "PROCESSAMENTO DE PACOTES",
                estado = "ERRO",
                fonte = "PacketEngine",
                evidencia = "Exceção durante análise",
                detalhes = e.message ?: "Erro desconhecido"
            )
        }
                        }
                            private fun localizacao(context: Context): AdaCapability {

        val pm = context.packageManager

        val gps =
            pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)

        val network =
            pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK)

        return capacidade(
            id = "C014",
            nome = "LOCALIZAÇÃO",
            estado = if (gps || network) "DETECTADO" else "NÃO DETECTADO",
            fonte = "PackageManager",
            evidencia = "FEATURE_LOCATION_GPS / NETWORK",
            detalhes = "GPS: $gps | Localização por rede: $network"
        )
                            }
                                private fun hotspot(context: Context): AdaCapability {

        val possuiWifi =
            context.packageManager.hasSystemFeature(
                PackageManager.FEATURE_WIFI
            )

        return capacidade(
            id = "C015",
            nome = "ROTEAMENTO / HOTSPOT",
            estado = if (possuiWifi) {
                "CAPACIDADE WIFI DETECTADA — HOTSPOT A VALIDAR"
            } else {
                "NÃO DETECTADO"
            },
            fonte = "PackageManager / Android",
            evidencia = "FEATURE_WIFI",
            detalhes = "O aparelho possui Wi-Fi. O estado e as capacidades específicas de hotspot/roteamento serão validados em teste próprio."
        )
                                }
                                    private fun vpnTun(context: Context): AdaCapability {

        val intent =
            VpnService.prepare(context)

        val estado =
            if (intent == null) {
                "VPN JÁ AUTORIZADA"
            } else {
                "VPN DISPONÍVEL — AUTORIZAÇÃO NECESSÁRIA"
            }

        return capacidade(
            id = "C016",
            nome = "VPN / TUN VIRTUAL",
            estado = estado,
            fonte = "VpnService",
            evidencia = "VpnService.prepare()",
            detalhes = "O Android fornece a infraestrutura VpnService para criação de uma interface virtual. Nenhuma conexão externa é afirmada."
        )
                                    }
                                        fun mapear(context: Context): List<AdaCapability> {

        return listOf(
            cpu(context),               // C001
            ram(context),               // C002
            armazenamento(context),     // C003
            gnss(context),              // C004
            gnssRaw(context),           // C005
            celular(context),           // C006
            wifi(context),              // C007
            bluetooth(context),         // C008
            sensores(context),          // C009
            bateria(context),           // C010
            interfacesRede(context),    // C011
            dataReservoir(context),     // C012
            packetEngine(context),      // C013
            localizacao(context),       // C014
            hotspot(context),           // C015
            vpnTun(context)             // C016
        )
                                        }
                fun relatorio(context: Context): String {

        val capacidades = mapear(context)

        val r = StringBuilder()

        r.append("🧠 ADA CAPABILITY MAP\n\n")
        r.append("APARELHO\n")
        r.append("${Build.MANUFACTURER} ${Build.MODEL}\n")
        r.append("Android: ${Build.VERSION.RELEASE}\n")
        r.append("API: ${Build.VERSION.SDK_INT}\n\n")

        r.append("================================\n\n")

        for (capacidade in capacidades) {

            r.append("${capacidade.id} — ${capacidade.nome}\n")
            r.append("Estado: ${capacidade.estado}\n")
            r.append("Fonte: ${capacidade.fonte}\n")
            r.append("Evidência: ${capacidade.evidencia}\n")
            r.append("Detalhes: ${capacidade.detalhes}\n")
            r.append("--------------------------------\n")
        }

        r.append("\nADA CAPABILITY MAP FINALIZADO\n")
        r.append("C001 → C016 mapeados\n")

        return r.toString()
    }
}
