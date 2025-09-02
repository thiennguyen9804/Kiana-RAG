package com.example.kianarag.util

import kotlin.math.sqrt

fun sqDistance(a: FloatArray, b: FloatArray): Float {
    return a.indices.sumOf { i ->
        (a[i] - b[i]) * (a[i] - b[i]).toDouble()
    }.toFloat()
}

fun distance(a: FloatArray, b: FloatArray): Float = sqrt(sqDistance(a, b))
