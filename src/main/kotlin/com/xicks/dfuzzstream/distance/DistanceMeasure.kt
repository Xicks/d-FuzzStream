package com.xicks.dfuzzstream.distance

import kotlin.math.pow
import kotlin.math.sqrt

abstract class DistanceMeasure {
    abstract fun distance(pointA: List<Double>, pointB: List<Double>, default: Double) : Double
}

class EuclideanDistance : DistanceMeasure() {
    override fun distance(pointA: List<Double>, pointB: List<Double>, default: Double): Double {
        return sqrt(pointA.mapIndexed { index, value ->
            (value - pointB[index]).pow(2)
        }.sum())
    }
}

class SquaredDistance : DistanceMeasure() {
    override fun distance(pointA: List<Double>, pointB: List<Double>, default: Double): Double {
        return pointA.mapIndexed { index, value ->
            (value - pointB[index]).pow(2)
        }.sum()
    }
}