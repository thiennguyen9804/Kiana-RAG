package com.example.kianarag.graph

import java.util.UUID

data class NodeDocumentMetaData(
    val id: String = UUID.randomUUID().toString(),
    val docId: String
) : NodeMetaData