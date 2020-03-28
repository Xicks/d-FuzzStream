package com.xicks.dfuzzstream.output

import java.io.File
import java.io.IOException
import java.io.PrintWriter

object MetricsToCSV {
    @Throws(IOException::class)
    fun writeMetrics(path: String, filename: String, metrics: List<String>) {
        PrintWriter(File("$path/$filename.csv")
            .also {
                //println("Creating file: ${it.absolutePath}")
                it.delete()
                it.createNewFile()
            }).use {

            metrics.forEach {line ->
                it.write("$line\n")
            }
        }
    }

}
