package com.example.kianarag.ml.kmeans

import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.ml.clustering.Clusterable

data class VectorPoint(val vector: ArrayRealVector) : Clusterable {
    override fun getPoint(): DoubleArray = vector.toArray()
}
