package com.example.kianarag.data

import android.util.LruCache
import com.example.kianarag.ml.product_quantization.PqCodeKey

object CodebooksManager {
    val pqCodeCache = LruCache<PqCodeKey, FloatArray>(1000)
    val centroidsCache = LruCache<Int, List<FloatArray>>(3)

    fun getVector(pqCode: PqCodeKey): FloatArray {
        val vector = pqCodeCache.get(pqCode)
        if(vector != null) {
            return vector
        }

    }

    fun getCentroids(subSpace: Int): List<FloatArray> {
        val centroids = centroidsCache.get(subSpace)
        if(centroids != null) {
            return centroids
        }

    }


    fun saveCodebooks(codebooks: List<List<FloatArray>>) {

    }
}