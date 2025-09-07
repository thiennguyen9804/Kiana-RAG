package com.example.kianarag.ml.product_quantization

import com.example.kianarag.data.CodebooksManager
import com.example.kianarag.ml.kmeans.KMeans
import com.example.kianarag.util.checkDataSetSanity
import java.util.Random

object ProductQuantization {
    val codebookManager = CodebooksManager
    var dimension : Int = -1
    var m = -1
    var k = -1

    fun train(
        vectors: Array<FloatArray>,
        m: Int, // subspace's dimensionality
        k: Int, // number of clusters per subspace
    ) : List<IntArray> {
        this.m = m
        this.k = k
        val codebooks = mutableListOf<List<FloatArray>>()
        val pqCodes = mutableListOf<IntArray>()

        checkDataSetSanity(vectors)
        dimension = vectors[0].size
        require(dimension % m == 0) { "Dimension must be divisible by m" }
        val subDim = dimension / m

        for(i in 0 until m) {
            val subVectors = vectors.map { vector ->
                vector.sliceArray(i * subDim until (i + 1) * subDim)
            }.toTypedArray()
            val kmeans = KMeans(Random())
            val means = kmeans.fitPredict(k, subVectors)

            codebooks.add(means.map { it.centroid })

            val codes = subVectors.map { subVector ->
                var bestIndex = -1
                var bestDistance = Float.MAX_VALUE
                means.forEachIndexed { idx, mean ->
                    val dist = subVector.l2DistanceTo(mean.centroid)
                    if (dist < bestDistance) {
                        bestDistance = dist
                        bestIndex = idx
                    }
                }
                bestIndex
            }.toIntArray()
            pqCodes.add(codes)
        }

        val finalPqCodes = vectors.indices.map { i ->
            IntArray(m) { j -> pqCodes[j][i] }
        }.toList()

        codebookManager.saveCodebooks(codebooks)


        return finalPqCodes
    }

    fun encode(vector: FloatArray): IntArray {
        require(dimension != -1) { "Dimension not set. Call train() first." }
        require(vector.size == dimension) { "Vector dimension must match trained dimension: $dimension" }
        val subDim = dimension / m
        val pqCode = PqCodeKey(IntArray(m))

        for (i in 0 until m) {
            val centroids = codebookManager.getCentroids(i) // List<FloatArray>

            // Tìm centroid gần nhất
            var bestIndex = -1
            var bestDistance = Float.MAX_VALUE
            centroids.forEachIndexed { idx, centroid ->
                val dist = vector.l2DistanceToSub(centroid, i * subDim, subDim)
                if (dist < bestDistance) {
                    bestDistance = dist
                    bestIndex = idx
                }
            }

            pqCode[i] = bestIndex
        }

        // Cache vector tái tạo
        val reconstructed = codebookManager.getVector(pqCode)
        codebookManager.pqCodeCache.put(pqCode, reconstructed)
        return pqCode.backing
    }
}