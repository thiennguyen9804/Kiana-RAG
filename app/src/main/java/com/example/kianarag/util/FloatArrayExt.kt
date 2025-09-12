package com.example.kianarag.util

import org.apache.commons.math3.linear.ArrayRealVector

fun FloatArray.toDoubleArray(): DoubleArray {
    return this.asSequence()
        .map { it.toDouble() }
        .toList()
        .toDoubleArray()
}

fun FloatArray.toArrayRealVector(): ArrayRealVector {
    return ArrayRealVector(toDoubleArray(), false)
}