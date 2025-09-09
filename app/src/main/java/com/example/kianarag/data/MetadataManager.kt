package com.example.kianarag.data

object MetadataManager {
    private val metadatas = mutableListOf<FileMetadata>()
    fun saveToDatabase(fileMetadata: FileMetadata) {
        metadatas.add(fileMetadata)
    }

    fun getById(id: String): FileMetadata {
        return TODO("Provide the return value")
    }
}