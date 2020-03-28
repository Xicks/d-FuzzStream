package com.xicks.dfuzzstream.structures

import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class FMiC (
    val id: Long,
    var cf: MutableList<Double> = mutableListOf(),
    var m: Double,
    var n: Long,
    var ssd: Double,
    var timestamp: Long,
    var center: MutableList<Double> = mutableListOf(),
    var tagTable: MutableMap<String, Double>
) {

    val radius: Double
        get() = if(n > 1L) {
            sqrt(ssd / n)
        } else {
            0.0
        }

    fun assignDataPoint(dataPoint: DataPoint, membership: Double, distance: Double) {
        m += membership
        n++
        ssd += membership * distance.pow(2)

        dataPoint.values.forEachIndexed { index, value -> cf[index] += value * membership }

        val tagMembership = tagTable.getOrPut(dataPoint.tag) { 0.0 }

        tagTable[dataPoint.tag] = tagMembership + membership
        updateCenter()
    }

    private fun updateCenter() = cf.forEachIndexed { index, value -> center[index] = value / m }

    fun merge(fmicToMerge: FMiC) {
        val cfToMerge = fmicToMerge.cf
        cf.forEachIndexed{ index, value ->
            cf[index] = value + cfToMerge[index]
        }
        m += fmicToMerge.m
        ssd += fmicToMerge.ssd
        n += fmicToMerge.n

        updateCenter()

        timestamp = max(timestamp, fmicToMerge.timestamp)

        fmicToMerge.tagTable.map { entry ->
            val a = tagTable.getOrPut(entry.key) { 0.0 }
            tagTable[entry.key] = a + entry.value
        }
    }
}