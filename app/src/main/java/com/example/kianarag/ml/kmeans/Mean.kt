package com.example.kianarag.ml.kmeans
data class Mean(var centroid: FloatArray) {
    val closestItems: MutableList<FloatArray> = mutableListOf()

    override fun toString(): String {
        return "Mean(centroid: ${centroid.contentToString()}, size: ${closestItems.size})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Mean

        if (!centroid.contentEquals(other.centroid)) return false
        if (closestItems != other.closestItems) return false

        return true
    }

    override fun hashCode(): Int {
        var result = centroid.contentHashCode()
        result = 31 * result + closestItems.hashCode()
        return result
    }
}