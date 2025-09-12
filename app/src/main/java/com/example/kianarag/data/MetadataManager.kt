package com.example.kianarag.data

object MetadataManager {
    private val metadatas = mutableMapOf<String, FileMetadata>()
    fun saveToDatabase(fileMetadata: FileMetadata) {
        metadatas.put(fileMetadata.chunkId, fileMetadata)
    }

    fun getById(id: String): FileMetadata {
        return metadatas[id]!!
    }
}