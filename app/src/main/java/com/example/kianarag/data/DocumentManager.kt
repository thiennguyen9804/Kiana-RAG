package com.example.kianarag.data

object DocumentManager {
    private val documents = mutableListOf<Document>()
    fun saveToDatabase(docs: List<Document>) {
        documents.addAll(docs)
    }

    fun saveToDatabase(doc: Document) {
        documents.add(doc)
    }
}