package com.example.kianarag.graph

import org.apache.commons.math3.linear.ArrayRealVector

abstract class GraphNode {
    private var _vector: ArrayRealVector? = null // Backing field private, nullable

    open var vector: ArrayRealVector
        get() = _vector ?: throw IllegalStateException("Vector has not been initialized")
        set(value) {
            if (_vector != null) {
                throw IllegalStateException("Vector can only be set once")
            }
            _vector = value
        }

    abstract val neighbors: MutableList<GraphNode>

    open fun distanceTo(other: GraphNode): Double {
        return this.vector.getDistance(other.vector)
    }
}