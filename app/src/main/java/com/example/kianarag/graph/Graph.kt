package com.example.kianarag.graph

import com.example.kianarag.graph.GraphPoint
import org.apache.commons.math3.linear.ArrayRealVector

open class Graph {
    protected open val points = mutableListOf<GraphPoint>()

    open fun batchAdd(points: List<GraphPoint>) {
        this.points.addAll(points)
    }

    open fun add(point: GraphPoint) {
        points.add(point)
    }

    open fun search(query: ArrayRealVector, k: Int): List<Pair<GraphPoint, Double>> {
        if (points.isEmpty()) return emptyList()

        // Lấy k điểm gần nhất
        return points
            .map { it to query.getDistance(it.vector) }
            .sortedBy { it.second }
            .take(2 * k) // oversampling
    }
}