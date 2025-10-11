package com.example.kianarag.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.HnswIndex
import io.objectbox.annotation.Id
import io.objectbox.annotation.VectorDistanceType

@Entity
data class Chunk(
    @Id
    var id: Long = 0,
    var chunkText: String = "",
    @HnswIndex(dimensions = 768, distanceType = VectorDistanceType.COSINE)
    var embedding: FloatArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Chunk

        if (id != other.id) return false
        if (chunkText != other.chunkText) return false
        if (!embedding.contentEquals(other.embedding)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + chunkText.hashCode()
        result = 31 * result + embedding.contentHashCode()
        return result
    }
}

@Entity
data class OBString(
    @Id
    var id: Long = 0
)