package com.example.kianarag.graph

import org.apache.commons.math3.linear.ArrayRealVector
import java.util.UUID

open class GraphPoint(
    open val id: String = UUID.randomUUID().toString(),
    open val docId: String,
    open val neighbors: MutableList<GraphPoint> = mutableListOf()
) {
    private var _vector: ArrayRealVector? = null  // Backing field private, nullable

    open var vector: ArrayRealVector  // Public val, immutable khi truy cập
        get() {
            return _vector ?: throw IllegalStateException("Vector has not been initialized")
        }
        set(value) {
            if (_vector != null) {
                throw IllegalStateException("Vector can only be set once")
            }
            _vector = value
        }
}