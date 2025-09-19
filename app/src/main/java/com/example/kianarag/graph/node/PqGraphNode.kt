package com.example.kianarag.graph.node

import com.example.kianarag.data.CodebooksManager
import com.example.kianarag.graph.node_metadata.NodeMetaData
import com.example.kianarag.ml.product_quantization.PqCodeKey
import org.apache.commons.math3.linear.ArrayRealVector

class PqGraphNode(
    override val metaData: NodeMetaData,
    private val codebooksManager: CodebooksManager,
    private val pqCodeKey: PqCodeKey,
) : GraphNode(metaData), NodeMetaData by metaData {
    override fun getBackingVector(): ArrayRealVector {
        return codebooksManager.getVector(pqCodeKey)
    }

    override fun distanceTo(other: GraphNode): Double {

        return TODO("Provide the return value")
    }


}