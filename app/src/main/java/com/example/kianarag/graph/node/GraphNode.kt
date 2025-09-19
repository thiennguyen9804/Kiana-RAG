package com.example.kianarag.graph.node

import com.example.kianarag.graph.node_metadata.NodeMetaData
import org.apache.commons.math3.linear.ArrayRealVector

abstract class GraphNode(
    protected open val metaData: NodeMetaData,
    val neighbors: MutableList<GraphNode> = mutableListOf()
) : NodeMetaData by metaData {
    protected var _vector: ArrayRealVector? = null // Backing field private để bảo vệ logic set

    var vector: ArrayRealVector
        get() = getBackingVector()
        set(value) = setVectorOnce(value)

    private fun setVectorOnce(value: ArrayRealVector) {
        if (_vector != null) {
            throw IllegalStateException("Vector can only be set once")
        }
        _vector = value
    }

    constructor(vector: ArrayRealVector, metaData: NodeMetaData,) : this(metaData = metaData) {
        setVectorOnce(vector)
    }

    abstract fun getBackingVector(): ArrayRealVector

    abstract fun distanceTo(other: GraphNode): Double
}