package com.xicks.dfuzzstream.summarizers

import com.xicks.dfuzzstream.distance.DistanceMeasure
import com.xicks.dfuzzstream.distance.EuclideanDistance
import com.xicks.dfuzzstream.metrics.InformativeMetrics
import com.xicks.dfuzzstream.structures.DataPoint
import com.xicks.dfuzzstream.structures.FMiC
import com.xicks.dfuzzstream.structures.FMiCsToMerge
import com.xicks.dfuzzstream.utils.toFMiC
import kotlin.math.pow

class DFuzzStreamSummarizer(
    private var fmics: MutableList<FMiC> = mutableListOf(),
    override val minFMiC: Int = 5,
    override val maxFMiC: Int = 100,
    override val mergeThreshold: Double = 1.0,
    override val radiusFactor: Double = 1.0,
    override val m: Double = 2.0,
    override val measure: DistanceMeasure = EuclideanDistance(),
    override val informativeMetrics: InformativeMetrics = InformativeMetrics()
) : StreamSummarizer(fmics, minFMiC, maxFMiC, mergeThreshold, radiusFactor, m, measure) {
    private val centers: List<List<Double>>
        get() = fmics.map { it.center }

    override fun summarize(dataPoint: DataPoint) {
        if (fmics.size < minFMiC) {
            createNewFMiC(dataPoint)
        } else {
            var isOutlier = true

            val distances = fmics.map {
                measure.distance(it.center, dataPoint.values, Double.MIN_VALUE)
            }

            fmics.forEachIndexed { index, fmic ->
                val radius = if (fmic.radius == 0.0) {
                    fmics.filterNot { it == fmic }.map {
                        measure.distance(fmic.center, it.center, Double.MIN_VALUE)
                    }.min()!!
                } else {
                    fmic.radius
                }

                if (distances[index] <= radius * radiusFactor) {
                    isOutlier = false
                    fmic.timestamp = dataPoint.timestamp
                }
            }

            if (!isOutlier) {
                updateFMiCsFeatures(
                    dataPoint,
                    calculateMembershipMatrix(distances),
                    distances
                ).also { informativeMetrics.absorptions++ }
            } else {
                createNewFMiC(dataPoint)
            }
            merge()
        }
    }


    private fun createNewFMiC(dataPoint: DataPoint) {
        if (fmics.size == maxFMiC) { removeOldestFMiC() }
        fmics.add(dataPoint.toFMiC(informativeMetrics.creations)).also { informativeMetrics.creations++ }
    }

    private fun removeOldestFMiC() = fmics.remove(fmics.minBy { it.timestamp }).also { informativeMetrics.removals++ }

    private fun merge() {
        val centers = centers

        val mergeItems = ArrayList<FMiCsToMerge>()

        for (i in 0 until fmics.size - 1) {
            for (j in i + 1 until fmics.size) {

                val dissimilarity = measure.distance(centers[i], centers[j], 0.0)

                val fmicI = fmics[i]
                val fmicJ = fmics[j]

                if (dissimilarity > Double.MIN_VALUE) {
                    val sumRadius = fmicI.radius + fmicJ.radius
                    val similarity = sumRadius / dissimilarity

                    if (similarity > mergeThreshold) {
                        mergeItems.add(FMiCsToMerge(fmicI, fmicJ, similarity))
                    }
                } else {
                    mergeItems.add(FMiCsToMerge(fmicI, fmicJ, Double.MAX_VALUE))
                }
            }
        }

        val mergedFMiCs = mutableListOf<FMiC>()
        val removeFMiCs = mutableListOf<FMiC>()

        mergeItems.sortDescending()

        mergeItems.forEach {

            // Check if FMiCs were already merged
            val fmicA = it.FMiCA
            val fmicB = it.FMiCB

            if (!(mergedFMiCs.contains(fmicA) || mergedFMiCs.contains(fmicB))) {
                // MergeFMiC A with FMiC B
                fmicA.merge(fmicB).also { informativeMetrics.merges++ }

                // Adds both to the merged FMiC list
                mergedFMiCs.add(fmicA)
                mergedFMiCs.add(fmicB)

                // Adds only the fmicB to the list to be deleted later
                removeFMiCs.add(fmicB)
            }
        }

        // Remove fmics
        fmics.removeAll(removeFMiCs)
    }

    private fun updateFMiCsFeatures(dataPoint: DataPoint, memberships: List<Double>, distances: List<Double>) {
            fmics.forEachIndexed { index, fmic ->
                fmic.assignDataPoint(dataPoint, memberships[index], distances[index])
            }
    }

    private fun calculateMembershipMatrix(distances: List<Double>): List<Double> {
        return distances.map { distanceJ -> 1 / distances.map { distanceK -> (distanceJ / distanceK).pow(2 / (m - 1)) }.sum() }
    }
}