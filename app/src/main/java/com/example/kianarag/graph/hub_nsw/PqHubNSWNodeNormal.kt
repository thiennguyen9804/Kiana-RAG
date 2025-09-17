package com.example.kianarag.graph.hub_nsw

import com.example.kianarag.data.CodebooksManager
import com.example.kianarag.graph.NormalGraphNode
import com.example.kianarag.ml.product_quantization.PqCodeKey
import org.apache.commons.math3.linear.ArrayRealVector
import java.util.UUID

data class PqHubNSWNodeNormal(
    override val id: String = UUID.randomUUID().toString(),
    override val docId: String,
    private val pqCode: PqCodeKey,
    override val neighbors: MutableList<NormalGraphNode> = mutableListOf(),
    private val codebooks: CodebooksManager,
): NormalGraphNode(id, docId, neighbors) {
    override var vector: ArrayRealVector
        get() {
            return this.reconstruct()
        }

        set(_) {
            throw IllegalStateException("PQ Vector cannot be set")
        }

    private fun reconstruct(pqCode: PqCodeKey = this.pqCode): ArrayRealVector {
        return codebooks.getVector(pqCode)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PqHubNSWNodeNormal

        if (id != other.id) return false
        if (docId != other.docId) return false
        if (neighbors != other.neighbors) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + docId.hashCode()
        result = 31 * result + neighbors.hashCode()
        return result
    }

}