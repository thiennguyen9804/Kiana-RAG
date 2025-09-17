package com.example.kianarag.rag

import android.content.Context
import android.util.Log
import com.example.kianarag.data.Document
import com.example.kianarag.data.DocumentManager
import com.example.kianarag.data.FileMetadata
import com.example.kianarag.data.MetadataManager
import com.example.kianarag.di.embeddingCodebooks
import com.example.kianarag.di.embeddingGraph
import com.example.kianarag.di.embeddingPq
import com.example.kianarag.di.graph
import com.example.kianarag.di.splitter
import com.example.kianarag.graph.Graph
import com.example.kianarag.graph.hub_nsw.PqHubNSWNodeNormal
import com.example.kianarag.ml.kmeans.VectorPoint
import com.example.kianarag.ml.product_quantization.PqCodeKey
import com.example.kianarag.rag.EmbeddingModel.Companion.DELEGATE_GPU
import com.example.kianarag.util.PdfLoader
import com.example.kianarag.util.toArrayRealVector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.ml.clustering.Clusterable
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer
import org.apache.commons.math3.ml.distance.EuclideanDistance

class KianaRAG(
    private val context: Context
) : EmbeddingModel.EmbedderListener {
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
            Log.d("Indexing", "Indexing start...")
            saveToDatabase(localFileNames)
            val maxConcurrentTasks = 5
            val vectorResults =  metadataIds.asFlow()
                .buffer(maxConcurrentTasks)
                .map { it ->
                    async(Dispatchers.Default) {
                        val chunk = MetadataManager.getById(it)
                        val embedding = embed(chunk.chunkContent)
                        embedding to chunk.chunkId
                    }
                }
                .toList()
                .map { deferred ->
                    deferred.await()
                }

            val points = vectorResults.map { it.first!!.toArrayRealVector() }
            val trainSize = (points.size * 0.8).toInt()
            val trainPoints = points.take(trainSize)
            val encodePoints = points.drop(trainSize)
            val trainPqCode = embeddingPq.train(trainPoints, m = 8, k = 5)
            val encodePqCode = embeddingPq.encode(encodePoints)
            val pqCodes = trainPqCode + encodePqCode

            val graphPoints = vectorResults.mapIndexed { index, (_, chunkId) ->
                PqHubNSWNodeNormal(
                    docId = chunkId,
                    pqCode = PqCodeKey(pqCodes[index]),
                    codebooks = embeddingCodebooks
                )
            }

            embeddingGraph.add(graphPoints)

            val clusterer = KMeansPlusPlusClusterer<Clusterable>(
                5, // Số cluster
                100, // Số lần lặp tối đa
                EuclideanDistance() // Khoảng cách Euclidean
            )


            Log.d("Indexing", "Indexing done!!! with ${points.size} vector")
        }
    }

    private fun buildEmbeddingGraph(points: List<PqHubNSWNodeNormal>, graph: Graph) {

    }

    private fun buildCentroidsGraph(points: List<ArrayRealVector>, graph: Graph) {
        val clusteringPoints = points.map { VectorPoint(it) }
        val clusterer = KMeansPlusPlusClusterer<Clusterable>(
            5, // Số cluster
            100, // Số lần lặp tối đa
            EuclideanDistance() // Khoảng cách Euclidean
        )
        val clusters = clusterer.cluster(clusteringPoints)
        val centroids = clusters.map { cluster ->
            ArrayRealVector(cluster.center.point)
        }

    }

    override fun onError(error: String, errorCode: Int) {
        Log.e("EmbeddingModel", error)
    }

    fun retrieve(query: String, k: Int): List<Pair<String, Double>> {
        val queryVector = embed(query)!!.toArrayRealVector()
        val results = graph.search(queryVector, k)
        return results.map { it.first.docId to it.second }
    }
}