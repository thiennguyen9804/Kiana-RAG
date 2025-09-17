package com.example.kianarag.graph

class NormalGraphNode(
    private val metaData: NodeMetaData,
    override val neighbors: MutableList<GraphNode> = mutableListOf()
) : GraphNode(), NodeMetaData by metaData