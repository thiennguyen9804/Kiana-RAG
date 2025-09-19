package com.example.kianarag.graph.node

import com.example.kianarag.graph.node_metadata.NodeMetaData
import org.apache.commons.math3.linear.ArrayRealVector

class NormalGraphNode(
    override val metaData: NodeMetaData,
) : GraphNode(metaData), NodeMetaData by metaData {
    override fun getBackingVector(): ArrayRealVector {
        return _vector ?: throw IllegalStateException("Vector has not been set")
    }

    override fun distanceTo(other: GraphNode): Double {
        return vector.getDistance(other.vector)
    }
}