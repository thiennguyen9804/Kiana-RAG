package com.example.kianarag.graph


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.apache.commons.math3.linear.ArrayRealVector

open class Graph {
    protected open val points = mutableListOf<NormalGraphNode>()

    open fun add(points: List<NormalGraphNode>) {
        this.points.addAll(points)
    }

    open fun add(point: NormalGraphNode) {
        points.add(point)
    }

    open fun search(query: ArrayRealVector, k: Int): List<Pair<NormalGraphNode, Double>> = runBlocking {
        if (points.isEmpty()) return@runBlocking emptyList()
        points.asFlow()
            .buffer(4)
            .map { async(Dispatchers.Default) { it to query.getDistance(it.vector) } }
            .toList()
            .map { it.await() }
            .sortedBy { it.second }
            .take(k)
    }
}