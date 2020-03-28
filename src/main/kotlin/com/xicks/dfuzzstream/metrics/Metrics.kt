package com.xicks.dfuzzstream.metrics

import com.xicks.dfuzzstream.structures.FMiC

data class InformativeMetrics(
    var merges: Long = 0,
    var creations: Long = 0,
    var removals: Long = 0,
    var absorptions: Long = 0
)

object Purity {

    fun calculate(fmics: List<FMiC>) : Double {
        return fmics.map { fmic ->
            val max = fmic.tagTable.maxBy { it.value }!!.value
            val sum = fmic.tagTable.map { it.value }.sum()
            max / sum
        }.sum() / fmics.size.toDouble()
    }
}