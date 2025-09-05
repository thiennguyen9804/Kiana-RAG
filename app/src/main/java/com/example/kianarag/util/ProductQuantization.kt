package com.example.kianarag.util

import com.example.kianarag.util.kmeans.KMeans
import java.util.Random



class ProductQuantization {
    operator fun invoke(
        vectors: Array<FloatArray>,
        m: Int,
        k: Int
    ) : Pair<List<List<FloatArray>>, List<IntArray>> {
        val codebooks = mutableListOf<List<FloatArray>>()
        val pqCodes = mutableListOf<IntArray>()

        checkDataSetSanity(vectors)
        val dimension = vectors[0].size
        require(dimension % m == 0) { "Dimension must be divisible by m" }
        val subDim = dimension / m

        for(i in 0 until m) {
            val subVectors = vectors.map { vector ->
                vector.sliceArray(i * subDim until (i + 1) * subDim)
            }.toTypedArray()
            val kmeans = KMeans(Random())
            val means = kmeans.predict(k, subVectors)


            codebooks.add(means.map { it.centroid })

            val codes = subVectors.map { subVector ->
                means.indexOfFirst { mean ->
                    subVector.l2DistanceTo(mean.centroid) == means.minOf { subVector.l2DistanceTo(it.centroid) }
                }
            }.toIntArray()
            pqCodes.add(codes)
        }

        val finalPqCodes = vectors.indices.map { i ->
            IntArray(m) { j -> pqCodes[j][i] }
        }.toList()

        return codebooks to finalPqCodes
    }
}