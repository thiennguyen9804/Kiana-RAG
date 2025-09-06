package com.example.kianarag.data

data class Point<T>(
    val id: Long,
    val vector: FloatArray,
    val metadata: T? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point<*>

        if (id != other.id) return false
        if (!vector.contentEquals(other.vector)) return false
        if (metadata != other.metadata) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + vector.contentHashCode()
        result = 31 * result + (metadata?.hashCode() ?: 0)
        return result
    }


}