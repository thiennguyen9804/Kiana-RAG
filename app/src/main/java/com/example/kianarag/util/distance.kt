package com.example.kianarag.util

import kotlin.math.sqrt

fun FloatArray.sqL2DistanceTo(b: FloatArray): Float {
    checkDataSetSanity(arrayOf(this, b))
    return this.indices.sumOf { i ->
        (this[i] - b[i]) * (this[i] - b[i]).toDouble()
    }.toFloat()
}

fun FloatArray.l2DistanceTo(b: FloatArray): Float {
    return sqrt(this.sqL2DistanceTo(b))
}

fun FloatArray.l2DistanceToSub(other: FloatArray, start: Int, length: Int): Float {
    var sum = 0f
    for (i in 0 until length) {
        val d = this[start + i] - other[i]
        sum += d * d
    }
    return sum
}