package com.example.kianarag.util.hub_nsw.point

import com.example.kianarag.data.CodebooksManager

data class PqHubNSWPoint(
    val id: Int,
    val pqCode: IntArray,
    val neighbors: MutableList<PqHubNSWPoint> = mutableListOf()
) {
    val vector: FloatArray
        get() = reconstruct(pqCode)
        private set

    fun reconstruct(pqCode: IntArray): FloatArray {
        return CodebooksManager.getVector(pqCode = pqCode)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PqHubNSWPoint

        if (id != other.id) return false
        if (!pqCode.contentEquals(other.pqCode)) return false
        if (neighbors != other.neighbors) return false
        if (!vector.contentEquals(other.vector)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + pqCode.contentHashCode()
        result = 31 * result + neighbors.hashCode()
        result = 31 * result + vector.contentHashCode()
        return result
    }
}