package com.kharblabs.equationbalancer2.dataManagers

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class CSVFile  (inputStream: InputStream? ){
    private var inputStream: InputStream? = null



    fun read(): List<*> {
        val resultList: ArrayList<String?> = ArrayList<String?>()
        val reader = BufferedReader(InputStreamReader(inputStream))
        try {
            var csvLine: String
            while ((reader.readLine().also { csvLine = it }) != null) {
                val row = csvLine.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                resultList.addAll(row)
            }
        } catch (ex: IOException) {
            throw RuntimeException("Error in reading CSV file: $ex")
        } finally {
            try {
                inputStream!!.close()
            } catch (e: IOException) {
                throw RuntimeException("Error while closing input stream: $e")
            }
        }
        return resultList
    }
}