package com.xicks.dfuzzstream.utils

import com.xicks.dfuzzstream.structures.DataPoint
import com.xicks.dfuzzstream.structures.FMiC
import java.lang.IllegalStateException
import java.util.StringTokenizer

fun String.toDataPoint(delimiter: String = ",", hasWeight: Boolean = false, timestamp: Long) : DataPoint {
    return try {
        val tokenizer = StringTokenizer(this, delimiter)
        val tokenCount = tokenizer.countTokens()
        val dimensions = tokenCount - if (hasWeight) 2 else 1

        DataPoint(
            values = 0.until(dimensions).map { tokenizer.nextToken().toDouble() },
            weight = if (hasWeight) tokenizer.nextToken().toDouble() else 1.0,
            tag = tokenizer.nextToken(),
            timestamp = timestamp
        )
    } catch(e: Exception) {
        throw IllegalStateException("cannot parse line [$this] to DataPoint", e)
    }
}

fun DataPoint.toFMiC(id: Long) : FMiC = FMiC(
    id = id,
    cf = this.values.toMutableList(),
    m = 1.0,
    n = 1,
    ssd = 0.0,
    timestamp = this.timestamp,
    center = this.values.toMutableList(),
    tagTable = mutableMapOf(this.tag to 1.0)
)