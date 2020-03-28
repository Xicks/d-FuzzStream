package com.xicks.dfuzzstream

import com.xicks.dfuzzstream.metrics.Purity
import com.xicks.dfuzzstream.output.FMiCsToCSV
import com.xicks.dfuzzstream.summarizers.DFuzzStreamSummarizer
import com.xicks.dfuzzstream.utils.toDataPoint
import java.io.File

fun main(vararg args: String) {

    if (args.size < 4) {
        println("Usage: java Task [datasetFile] [numberOfChunks] [outputFilePath] [outputFileName]")
        return
    }

    val dataSetPath = args[0]
    val numberOfChunks = args[1].toInt()
    val outputFilePath = args[2]
    val outputFileName = args[3]

    val file = File(dataSetPath)

    val summarizer = DFuzzStreamSummarizer(mergeThreshold = 0.8)

    println("Summarizing file: " + file.absolutePath)
    val metrics = mutableListOf<String>()
    metrics.add("Merges,Creations,Removals,Absorptions,FMiCs,Purity,Time")
    var count = 0L

    val dataSet = file.readLines(Charsets.UTF_8).drop(1)
    val chunkSize = dataSet.size / numberOfChunks
    dataSet.chunked(chunkSize).forEachIndexed { chunkIndex, chunk ->
        println("Processing chunk ${chunkIndex + 1} of size ${chunk.size}")
        val time = System.currentTimeMillis()
        chunk.forEach { line ->
            summarizer.summarize(line.toDataPoint(timestamp = count)).also { count++ }
        }

        FMiCsToCSV.writeFMiCs(outputFilePath, "${outputFileName}_$chunkIndex", summarizer.getFMiCs())

        metrics.add(summarizer.informativeMetrics.run {
            "$merges,$creations,$removals,$absorptions,${summarizer.getFMiCs().size},${Purity.calculate(summarizer.getFMiCs())},${System.currentTimeMillis() - time}"
        })
    }

    println("Processed $count examples")
}