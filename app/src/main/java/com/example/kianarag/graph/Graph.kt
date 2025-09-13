package com.example.kianarag.graph


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.commons.math3.linear.ArrayRealVector

open class Graph {
    protected open val points = mutableListOf<GraphPoint>()

    open fun add(points: List<GraphPoint>) {
        this.points.addAll(points)
    }

    open fun add(point: GraphPoint) {
        points.add(point)
    }

    open fun search(query: ArrayRealVector, k: Int): List<Pair<GraphPoint, Double>> = runBlocking {
        if (points.isEmpty()) return@runBlocking emptyList()
        points.asFlow()
            .buffer(4)
            .map { async(Dispatchers.Default) { it to query.getDistance(it.vector) } }
            .toList()
            .map { it.await() }
            .sortedBy { it.second }
            .take(2 * k)
    }
}