package com.example.kianarag.rag

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.kianarag.data.Document
import com.example.kianarag.data.DocumentManager
import com.example.kianarag.data.FileMetadata
import com.example.kianarag.data.MetadataManager
import com.example.kianarag.data.PdfLoader
import com.example.kianarag.di.graph
import com.example.kianarag.di.splitter
import com.example.kianarag.graph.GraphPoint
import com.example.kianarag.rag.embedding.EmbeddingModel
import com.example.kianarag.util.toArrayRealVector
import org.apache.commons.math3.linear.ArrayRealVector

class KianaRAG(
    private val context: Context
) : EmbeddingModel.EmbedderListener {
    lateinit var embeddings: List<ArrayRealVector>
    val points: List<GraphPoint> = mutableListOf()
    val embeddingModel = EmbeddingModel(context = context)
    val pdfLoader = PdfLoader(context = context)

    val docIds = mutableListOf<String>()
    val metadataIds = mutableListOf<String>()

    // chunk content + offset
    private fun split(input: String): Pair<List<String>, List<Int>> {
        return splitter.split(input)
    }

    private fun embedding(input: String): FloatArray? {
        return embeddingModel.embed(input)
    }

    // return doc id + offset
    private fun saveToDatabase(localFileNames: List<String>) {
        localFileNames.forEach {
            val (content, filePath) = pdfLoader.load(it)
            val doc = Document(
                id = it,
                pointer = filePath,
            )
            DocumentManager.saveToDatabase(doc)
            docIds.add(it)

            val (splitTexts, offsets) = split(content)
            splitTexts.zip(offsets) { text, offset ->
                val fileMetadata = FileMetadata(
                    docId = it,
                    chunkContent = text,
                    chunkOffset = offset
                )

                MetadataManager.saveToDatabase(fileMetadata = fileMetadata)
                metadataIds.add(fileMetadata.chunkId)
            }
        }

    }

    fun index(localFileNames: List<String>) {
        saveToDatabase(localFileNames)
        metadataIds.forEach {
            val chunk = MetadataManager.getById(it)
            val value = embedding(chunk.chunkContent)
            println("String value: ${chunk.chunkContent} Embedding value: ${value.contentToString()}")
        }
//        val pendingPoints = mutableListOf<GraphPoint>()
//        metadataIds.forEach {
//            val chunk = MetadataManager.getById(it)
//            val embedValue = (embedding(chunk.chunkContent))
//            val graphPoint = GraphPoint(
//                docId = chunk.docId,
//            ).apply { vector = embedValue }
//            pendingPoints.add(graphPoint)
//
//        }
//        graph.batchAdd(pendingPoints)
    }

    override fun onError(error: String, errorCode: Int) {
        Log.e("EmbeddingModel", error)
    }

//    fun retrieve(query: String, k: Int): List<Pair<String, Double>> {
//        val queryVector = embedding(query)
//        val results = graph.search(queryVector, k)
//        return results.map { it.first.docId to it.second }
//    }
}