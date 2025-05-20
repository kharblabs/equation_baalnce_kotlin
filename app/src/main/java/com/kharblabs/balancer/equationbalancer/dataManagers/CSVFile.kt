package com.kharblabs.balancer.equationbalancer.dataManagers

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AssetFileReader(private val context: Context) {
    suspend fun readCsvFile(fileName: String): List<Array<String>> = withContext(Dispatchers.IO) {
        val resultList = mutableListOf<Array<String>>()
        val inputStream = context.assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(inputStream))

        try {
            reader.forEachLine { line ->
                val row = line.split(",").toTypedArray()
                resultList.add(row)
            }
        } catch (ex: IOException) {
            throw RuntimeException("Error reading CSV file: $ex")
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                throw RuntimeException("Error closing InputStream: $e")
            }
        }

        return@withContext resultList
    }
}
class CSVFile(private val inputStream: InputStream) {

    fun read(): List<Array<String>> {
        val resultList = mutableListOf<Array<String>>()
        val reader = BufferedReader(InputStreamReader(inputStream))

        try {
            var csvLine: String?
            while (reader.readLine().also { csvLine = it } != null) {
                val row = csvLine!!.split(",").toTypedArray()
                resultList.add(row)
            }
        } catch (ex: IOException) {
            throw RuntimeException("Error in reading CSV file: $ex")
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                throw RuntimeException("Error while closing input stream: $e")
            }
        }

        return resultList
    }
}