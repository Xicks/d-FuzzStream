package com.xicks.dfuzzstream.summarizers

import com.xicks.dfuzzstream.distance.DistanceMeasure
import com.xicks.dfuzzstream.distance.EuclideanDistance
import com.xicks.dfuzzstream.metrics.InformativeMetrics
import com.xicks.dfuzzstream.structures.DataPoint
import com.xicks.dfuzzstream.structures.FMiC

abstract class StreamSummarizer(
    private var fmics: MutableList<FMiC> = mutableListOf(),
    open val minFMiC: Int = 5,
    open val maxFMiC: Int,
    open val mergeThreshold: Double,
    open val radiusFactor: Double = 1.0,
    open val m: Double = 2.0,
    open val measure: DistanceMeasure = EuclideanDistance(),
    open val informativeMetrics: InformativeMetrics = InformativeMetrics()
) {
    abstract fun summarize(dataPoint: DataPoint)

    fun getFMiCs() : List<FMiC> = fmics.toList()
}