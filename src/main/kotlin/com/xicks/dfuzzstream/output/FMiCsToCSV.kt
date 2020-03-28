package com.xicks.dfuzzstream.output

import com.xicks.dfuzzstream.structures.FMiC
import com.xicks.dfuzzstream.utils.toDataPoint
import java.io.File
import java.io.IOException
import java.io.PrintWriter

object FMiCsToCSV {
    @Throws(IOException::class)
    fun writeFMiCs(path: String, filename: String, fmics: List<FMiC>) {
        PrintWriter(File("$path/$filename.csv")
            .also {
                //println("Creating file: ${it.absolutePath}")
                it.delete()
                it.createNewFile()
            }).use {

            val sb = StringBuilder()

            val attributesSize = fmics.first().cf.size

            sb.append((1..attributesSize).joinToString(separator = ",") { "X_$it" })
            sb.append(",weight")
            sb.append(",tag\n")

            fmics.map { fmic ->
                val dp = fmic.toDataPoint()
                sb.append(dp.values.joinToString(separator = ",") { "$it" })
                sb.append(",${dp.weight}")
                sb.append(",${dp.tag}\n")
            }
            it.write(String(sb))
        }
    }

}
