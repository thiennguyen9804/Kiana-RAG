package com.example.kianarag.graph

import com.example.kianarag.graph.node.GraphNode
import com.example.kianarag.graph.node.NormalGraphNode
import org.apache.commons.math3.linear.ArrayRealVector
import java.util.PriorityQueue

class HubNSWGraph(
    private val M: Int = 16, // Số hàng xóm tối đa
    private val efConstruction: Int = 200, // Số ứng viên khi thêm điểm
    private val efSearch: Int = 100 // Số ứng viên khi tìm kiếm
) {
    private val points = mutableListOf<NormalGraphNode>() // Lưu tất cả điểm

    fun batchAdd(points: List<NormalGraphNode>) {
        points.forEach { add(it) }
    }

    fun add(point: NormalGraphNode) {
        points.add(point)
        val vector = point.vector

        if (points.size == 1) return // Điểm đầu tiên không cần hàng xóm

        // Tìm ứng viên hàng xóm bằng greedy search
        val candidates = searchCandidates(vector, efConstruction)
        // Chọn M hàng xóm gần nhất
        val neighbors = candidates.sortedBy { vector.getDistance(it.vector) }.take(M)
        // Kết nối bidirectional
        point.neighbors.addAll(neighbors)
        neighbors.forEach { it.neighbors.add(point) }
    }

    private fun searchCandidates(query: ArrayRealVector, ef: Int): List<GraphNode> {
        val visited = mutableSetOf<GraphNode>()
        val candidates = PriorityQueue(compareBy<GraphNode> { query.getDistance(it.vector) })
        val start = points.random()
        candidates.add(start)
        visited.add(start)

        while (candidates.isNotEmpty() && visited.size < ef) {
            val current = candidates.poll()
            current!!.neighbors.forEach { neighbor ->
                if (neighbor !in visited) {
                    visited.add(neighbor)
                    candidates.add(neighbor)
                }
            }
        }
        return visited.toList()
    }

    // Tìm k điểm gần nhất với query
    fun search(query: ArrayRealVector, k: Int): List<Pair<GraphNode, Double>> {
        if (points.isEmpty()) return emptyList()

        // Greedy search để tìm efSearch ứng viên
        val visited = mutableSetOf<GraphNode>()
        val candidates = PriorityQueue(compareBy<GraphNode> { query.getDistance(it.vector) })
        val start = points.random() // Điểm bắt đầu ngẫu nhiên
        candidates.add(start)
        visited.add(start)

        while (candidates.isNotEmpty() && visited.size < efSearch) {
            val current = candidates.poll()
            current!!.neighbors.forEach { neighbor ->
                if (neighbor !in visited) {
                    visited.add(neighbor)
                    candidates.add(neighbor)
                }
            }
        }

        // Lấy k điểm gần nhất
        return visited
            .map { it to query.getDistance(it.vector) }
            .sortedBy { it.second }
            .take(2 * k) // oversampling
    }

}