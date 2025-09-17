package com.example.kianarag.ml.product_quantization

import android.util.Log
import com.example.kianarag.data.CodebooksManager
import com.example.kianarag.ml.kmeans.KMeans
import com.example.kianarag.ml.kmeans.VectorPoint
import com.example.kianarag.util.checkDataSetSanity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.ml.clustering.Clusterable
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer
import org.apache.commons.math3.ml.distance.EuclideanDistance
class ProductQuantization(
    private val codebookManager: CodebooksManager
) {
    private var dimension: Int = -1
    private var m: Int = -1 // Số subspace
    private var k: Int = -1 // Số cluster mỗi subspace

    suspend fun train(
        vectors: List<ArrayRealVector>,
        m: Int, // Số subspace
        k: Int // Số cluster mỗi subspace
    ): List<IntArray> = withContext(Dispatchers.Default) {
        Log.d("ProductQuantization", "Training PQ with ${vectors.size} vectors, m=$m, k=$k")
        this@ProductQuantization.m = m
        this@ProductQuantization.k = k
        val codebooks = mutableListOf<List<ArrayRealVector>>()
        val pqCodes = mutableListOf<IntArray>()

        if (vectors.isEmpty()) {
            Log.w("ProductQuantization", "No vectors provided for training")
            return@withContext emptyList()
        }

        dimension = vectors[0].dimension
        require(dimension % m == 0) { "Dimension ($dimension) must be divisible by m ($m)" }
        val subDim = dimension / m

        coroutineScope {
            val deferredResults = (0 until m).map { i ->
                async(Dispatchers.Default) {
                    val subVectors = vectors.map { vector ->
                        VectorPoint(ArrayRealVector(vector.getSubVector(i * subDim, subDim).toArray()))
                    }

                    // K-Means clustering
                    val clusterer = KMeansPlusPlusClusterer<Clusterable>(
                        k, // Số cluster
                        100, // Số lần lặp tối đa
                        EuclideanDistance() // Khoảng cách Euclidean
                    )
                    val clusters = clusterer.cluster(subVectors)
                    val centroids = clusters.map { cluster ->
                        ArrayRealVector(cluster.center.point)
                    }

                    // Gán mã PQ cho mỗi vector
                    val codes = subVectors.map { subVector ->
                        var bestIndex = -1
                        var bestDistance = Double.MAX_VALUE
                        centroids.forEachIndexed { idx, centroid ->
                            val dist = subVector.vector.getDistance(centroid)
                            if (dist < bestDistance) {
                                bestDistance = dist
                                bestIndex = idx
                            }
                        }
                        bestIndex
                    }.toIntArray()

                    i to (centroids to codes)
                }
            }

            val results = deferredResults.awaitAll()
            results.forEach { (i, result) ->
                val (centroids, codes) = result
                codebooks.add(centroids)
                pqCodes.add(codes)
                Log.d("ProductQuantization", "Processed subspace $i with ${codes.size} codes")
            }
        }

        val finalPqCodes = vectors.indices.map { i ->
            IntArray(m) { j -> pqCodes[j][i] }
        }

        // Lưu codebooks
        codebookManager.saveCodebooks(codebooks)
        Log.d("ProductQuantization", "Trained PQ with $m codebooks, each with $k centroids")
        finalPqCodes
    }

    suspend fun encode(vectors: List<ArrayRealVector>): List<IntArray> = coroutineScope {
        vectors.map { vector ->
            async(Dispatchers.Default) {
                encode(vector) // Gọi hàm encode(vector) hiện có
            }
        }.awaitAll()
    }

    suspend fun encode(vector: ArrayRealVector): IntArray = withContext(Dispatchers.Default) {
        require(dimension != -1) { "Dimension not set. Call train() first." }
        require(vector.dimension == dimension) { "Vector dimension (${vector.dimension}) must match trained dimension ($dimension)" }
        val subDim = dimension / m
        val pqCode = PqCodeKey(IntArray(m))

        for (i in 0 until m) {
            val subVector = vector.getSubVector(i * subDim, subDim)
            val centroids = codebookManager.getCentroids(i) // List<ArrayRealVector>

            // Tìm centroid gần nhất
            var bestIndex = -1
            var bestDistance = Double.MAX_VALUE
            centroids.forEachIndexed { idx, centroid ->
                val dist = subVector.getDistance(centroid)
                if (dist < bestDistance) {
                    bestDistance = dist
                    bestIndex = idx
                }
            }
            pqCode.backing[i] = bestIndex
        }

        pqCode.backing
    }
}