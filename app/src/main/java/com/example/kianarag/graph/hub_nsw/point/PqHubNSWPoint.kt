package com.example.kianarag.graph.hub_nsw.point

import com.example.kianarag.data.CodebooksManager
import com.example.kianarag.ml.product_quantization.PqCodeKey
import org.apache.commons.math3.linear.ArrayRealVector

data class PqHubNSWPoint(
    val id: Int,
    val docId: Int,
    val pqCode: PqCodeKey,
    val neighbors: MutableList<PqHubNSWPoint> = mutableListOf()
) {
    val vector: ArrayRealVector
        get() = reconstruct(pqCode)
        private set

    fun reconstruct(pqCode: PqCodeKey): ArrayRealVector {
        return CodebooksManager.getVector(pqCode = pqCode)
    }


}