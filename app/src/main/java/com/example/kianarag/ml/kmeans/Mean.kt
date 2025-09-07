package com.example.kianarag.ml.kmeans

import org.apache.commons.math3.linear.ArrayRealVector

data class Mean(var centroid: ArrayRealVector) {
    val closestItems: MutableList<ArrayRealVector> = mutableListOf()

}