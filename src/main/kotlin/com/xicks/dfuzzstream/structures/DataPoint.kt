package com.xicks.dfuzzstream.structures

import java.util.Collections.emptyList

data class DataPoint(
    val values: List<Double> = emptyList(),
    val tag: String = "",
    val weight: Double = 1.0,
    val timestamp: Long = 0
) : Comparable<DataPoint> {
    override fun compareTo(other: DataPoint) : Int = this.weight.compareTo(other.weight)
}


