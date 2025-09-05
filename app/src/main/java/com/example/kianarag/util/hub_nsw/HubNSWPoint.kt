package com.example.kianarag.util.hub_nsw

data class HubNSWPoint<T>(
//    val id: Long,
    val vector: FloatArray,
    val metadata: T? = null,
    val neighbors: MutableList<HubNSWPoint<T>> = mutableListOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HubNSWPoint<*>

        if (!vector.contentEquals(other.vector)) return false
        if (metadata != other.metadata) return false
        if (neighbors != other.neighbors) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vector.contentHashCode()
        result = 31 * result + (metadata?.hashCode() ?: 0)
        result = 31 * result + neighbors.hashCode()
        return result
    }

}
