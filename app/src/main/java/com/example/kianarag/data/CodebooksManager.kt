package com.example.kianarag.data

import android.util.LruCache
import com.example.kianarag.ml.product_quantization.PqCodeKey
import org.apache.commons.math3.linear.ArrayRealVector

class CodebooksManager {
    val pqCodeCache = LruCache<PqCodeKey, ArrayRealVector>(1000)
    val centroidsCache = LruCache<Int, List<ArrayRealVector>>(3)

    fun getVector(pqCode: PqCodeKey): ArrayRealVector {
        val vector = pqCodeCache.get(pqCode)
        if(vector != null) {
            return vector
        }

        val doubleArray = doubleArrayOf(0.0, 1.0, 2.0, 4.0)
        return ArrayRealVector(doubleArray)

    }

    fun getCentroids(subSpace: Int): List<ArrayRealVector> {
        val centroids = centroidsCache.get(subSpace)
        if(centroids != null) {
            return centroids
        }

        val doubleArray = doubleArrayOf(0.0, 1.0, 2.0, 4.0)
        val vector =  ArrayRealVector(doubleArray)
        return listOf(vector, vector)
    }

    fun saveCodebooks(codebooks: List<List<ArrayRealVector>>) {

    }
}