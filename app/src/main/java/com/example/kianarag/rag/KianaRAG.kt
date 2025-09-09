package com.example.kianarag.rag

import com.example.kianarag.data.Document
import com.example.kianarag.data.DocumentManager
import com.example.kianarag.data.FileMetadata
import com.example.kianarag.data.MetadataManager
import com.example.kianarag.data.PdfLoader
import com.example.kianarag.di.graph
import com.example.kianarag.di.splitter
import com.example.kianarag.graph.Graph
import com.example.kianarag.graph.hub_nsw.HubNSWGraph
import com.example.kianarag.graph.GraphPoint
import org.apache.commons.math3.linear.ArrayRealVector


class KianaRAG {
    lateinit var embeddings: List<ArrayRealVector>
    val points: List<GraphPoint> = mutableListOf()

    val docIds = mutableListOf<String>()
    val metadataIds = mutableListOf<String>()

    // chunk content + offset
    private fun split(input: String): Pair<List<String>, List<Int>> {
        return splitter.split(input)
    }

    private fun embedding(input: String): ArrayRealVector {
        return TODO("Provide the return value")

    }

    // return doc id + offset
    private fun saveToDatabase(paths: List<String>) {
        paths.forEach {
            val (content, filename) = PdfLoader.load(it)
            val doc = Document(
                id = filename,
                pointer = it,
            )
            DocumentManager.saveToDatabase(doc)
            docIds.add(filename)

            val (splitTexts, offsets) = split(content)
            splitTexts.zip(offsets) { text, offset ->
                val fileMetadata = FileMetadata(
                    docId = filename,
                    chunkContent = text,
                    chunkOffset = offset
                )

                MetadataManager.saveToDatabase(fileMetadata = fileMetadata)
                metadataIds.add(fileMetadata.chunkId)
            }
        }

    }

    fun index(paths: List<String>) {
        saveToDatabase(paths)
        val pendingPoints = mutableListOf<GraphPoint>()
        metadataIds.forEach {
            val chunk = MetadataManager.getById(it)
            val embedValue = (embedding(chunk.chunkContent))
            val graphPoint = GraphPoint(
                docId = chunk.docId,
            ).apply { vector = embedValue }
            pendingPoints.add(graphPoint)

        }
        graph.batchAdd(pendingPoints)
    }

    fun retrieve(query: String, k: Int): List<Pair<String, Double>> {
        val queryVector = embedding(query)
        val results = graph.search(queryVector, k)
        return results.map { it.first.docId to it.second }
    }
}