package com.adalink

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class AdaPacket(
    val id: Long,
    val timestamp: Long,
    val source: String,
    val type: String,
    val data: String,
    val size: Int
)

object DataReservoir {

    private const val FILE_NAME = "adalink_reservoir.json"

    private fun file(context: Context): File {
        return File(context.filesDir, FILE_NAME)
    }

    private fun carregar(context: Context): JSONArray {

        val f = file(context)

        if (!f.exists()) {
            return JSONArray()
        }

        return try {
            JSONArray(f.readText())
        } catch (e: Exception) {
            throw IllegalStateException(
    "ADA RESERVOIR CORROMPIDO — NÃO SOBRESCREVER DADOS",
    e
)
        }
    }

    private fun salvar(
        context: Context,
        array: JSONArray
    ) {
        file(context).writeText(
            array.toString()
        )
    }

    fun armazenar(
        context: Context,
        source: String,
        type: String,
        data: String
    ): Long {

        val array = carregar(context)

        val id = System.currentTimeMillis()

        val pacote = JSONObject()

        pacote.put("id", id)
        pacote.put(
            "timestamp",
            System.currentTimeMillis()
        )
        pacote.put("source", source)
        pacote.put("type", type)
        pacote.put("data", data)
        pacote.put("size", data.toByteArray().size)

        array.put(pacote)

        salvar(context, array)

        return id
    }

    fun listar(
        context: Context
    ): List<AdaPacket> {

        val array = carregar(context)

        val lista = mutableListOf<AdaPacket>()

        for (i in 0 until array.length()) {

            val p = array.getJSONObject(i)

            lista.add(
                AdaPacket(
                    id = p.getLong("id"),
                    timestamp = p.getLong("timestamp"),
                    source = p.getString("source"),
                    type = p.getString("type"),
                    data = p.getString("data"),
                    size = p.getInt("size")
                )
            )
        }

        return lista
    }

    fun quantidade(
        context: Context
    ): Int {
        return carregar(context).length()
    }

    fun tamanhoTotal(
        context: Context
    ): Long {

        val lista = listar(context)

        return lista.sumOf {
            it.size.toLong()
        }
    }

        fun limpar(
        context: Context
    ) {
        file(context).delete()
    }

    fun dadosDoReservatorio(
        context: Context
    ): String {
        val f = file(context)

        if (!f.exists()) {
            return "[]"
        }

        return f.readText()
    }
}
