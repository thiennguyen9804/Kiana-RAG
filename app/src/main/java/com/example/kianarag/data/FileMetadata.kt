package com.example.kianarag.data

import java.util.UUID

data class FileMetadata(
    var chunkId: String = UUID.randomUUID().toString(),
    var docId: String,
    var chunkContent: String,
    val chunkOffset: Int
)

