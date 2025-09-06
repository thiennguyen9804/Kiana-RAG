package com.example.kianarag.util.hub_nsw

import com.example.kianarag.util.hub_nsw.point.PqHubNSWPoint
import com.example.kianarag.util.l2DistanceTo
import java.util.PriorityQueue

class HubNSWGraph(
    private val M: Int = 16, // Số hàng xóm tối đa
    private val efConstruction: Int = 200, // Số ứng viên khi thêm điểm
    private val efSearch: Int = 100 // Số ứng viên khi tìm kiếm
) {
    private val points = mutableListOf<PqHubNSWPoint>() // Lưu tất cả điểm

    fun batchAdd(points: List<PqHubNSWPoint>) {
        points.forEach { add(it) }
    }

    fun add(point: PqHubNSWPoint) {
        points.add(point)
        val vector = point.vector

        if (points.size == 1) return // Điểm đầu tiên không cần hàng xóm

        // Tìm ứng viên hàng xóm bằng greedy search
        val candidates = searchCandidates(vector, efConstruction)
        // Chọn M hàng xóm gần nhất
        val neighbors = candidates.sortedBy { vector.l2DistanceTo(it.vector) }.take(M)
        // Kết nối bidirectional
        point.neighbors.addAll(neighbors)
        neighbors.forEach { it.neighbors.add(point) }
    }

    private fun searchCandidates(query: FloatArray, ef: Int): List<PqHubNSWPoint> {
        val visited = mutableSetOf<PqHubNSWPoint>()
        val candidates = PriorityQueue(compareBy<PqHubNSWPoint> { query.l2DistanceTo(it.vector) })
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
    fun search(query: FloatArray, k: Int): List<Pair<PqHubNSWPoint, Float>> {
        if (points.isEmpty()) return emptyList()

        // Greedy search để tìm efSearch ứng viên
        val visited = mutableSetOf<PqHubNSWPoint>()
        val candidates = PriorityQueue(compareBy<PqHubNSWPoint> { query.l2DistanceTo(it.vector) })
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
            .map { it to query.l2DistanceTo(it.vector) }
            .sortedBy { it.second }
            .take(2 * k) // oversampling
    }

}