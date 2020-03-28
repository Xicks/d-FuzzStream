package com.xicks.dfuzzstream.utils

import com.xicks.dfuzzstream.structures.DataPoint
import com.xicks.dfuzzstream.structures.FMiC

fun FMiC.toDataPoint() : DataPoint = DataPoint(
    values = this.center,
    weight = this.m,
    timestamp = this.timestamp,
    tag = this.tagTable.maxBy { entry -> entry.value }!!.key
)