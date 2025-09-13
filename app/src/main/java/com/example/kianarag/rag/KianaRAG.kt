package com.example.kianarag.rag

import android.content.Context
import android.util.Log
import com.example.kianarag.data.Document
import com.example.kianarag.data.DocumentManager
import com.example.kianarag.data.FileMetadata
import com.example.kianarag.data.MetadataManager
import com.example.kianarag.util.PdfLoader
import com.example.kianarag.di.splitter
import com.example.kianarag.graph.GraphPoint
import com.example.kianarag.rag.embedding.EmbeddingModel
import com.example.kianarag.rag.embedding.EmbeddingModel.Companion.DELEGATE_CPU
import com.example.kianarag.rag.embedding.EmbeddingModel.Companion.DELEGATE_GPU
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.math3.linear.ArrayRealVector

class KianaRAG(
    private val context: Context
) : EmbeddingModel.EmbedderListener {
    lateinit var embeddings: List<ArrayRealVector>
    val points: List<GraphPoint> = mutableListOf()
    val embeddingModel = EmbeddingModel(
        context = context,
        currentDelegate = DELEGATE_GPU
    )
    val pdfLoader = PdfLoader(context = context)

    val docIds = mutableListOf<String>()
    val metadataIds = mutableListOf<String>()

    // chunk content + offset
    private fun split(input: String): Pair<List<String>, List<Int>> {
        return splitter.split(input)
    }

    private fun embed(input: String): FloatArray? {
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
        CoroutineScope(Dispatchers.IO).launch {
            saveToDatabase(localFileNames)
            val maxConcurrentTasks = 4
            metadataIds.asFlow()
                .buffer(maxConcurrentTasks)
                .map { it ->
                    async(Dispatchers.Default) {
                        val chunk = MetadataManager.getById(it)
                        val embedding = embed(chunk.chunkContent)
                        embedding to chunk.chunkId
                    }
                }
                .toList()
                .forEachIndexed { index, deferred ->
                    val (value, chunkId) = deferred.await()
                    if (value != null) {
                        Log.d("KianaRAG", "Chunk No. #$index: ${MetadataManager.getById(chunkId).chunkContent} -> ${value.contentToString()}")
                    }
                }
        }
//        metadataIds.forEachIndexed { index, it ->
//            val chunk = MetadataManager.getById(it)
//            val value = embed(chunk.chunkContent)
//            println("Chunk No. #${index}: ${chunk.chunkContent} -> ${value.contentToString()}")
//        }
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