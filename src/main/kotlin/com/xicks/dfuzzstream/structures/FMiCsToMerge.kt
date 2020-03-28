package com.xicks.dfuzzstream.structures

data class FMiCsToMerge(
    val FMiCA: FMiC,
    val FMiCB: FMiC,
    val similarity: Double
) : Comparable<FMiCsToMerge> {
    override fun compareTo(other: FMiCsToMerge): Int = similarity.compareTo(other.similarity)
}