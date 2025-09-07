package com.example.kianarag.ml.kmeans

import android.util.Log
import com.example.kianarag.util.checkDataSetSanity
import org.apache.commons.math3.linear.ArrayRealVector
import java.util.Random

class KMeans(
    private val randomState: Random,
    private val maxIterations: Int = 30,
    convergenceEpsilon: Float = 0.005f,
) {
    val sqConvergenceEpsilon = convergenceEpsilon * convergenceEpsilon;
    companion object {
        var DEBUG: Boolean = false
        var TAG: String = "KMeans"
    }



    fun fitPredict(k: Int, inputData: Array<ArrayRealVector>): List<Mean> {
//        checkDataSetSanity(inputData)
        val dimension = inputData[0].dimension
        val means = mutableListOf<Mean>()

        repeat(k) {
            val centroid = DoubleArray(dimension) { randomState.nextDouble() }
            val centroidVector = ArrayRealVector(centroid, false)
            means.add(Mean(centroidVector))
        }

        var converged = false
        for (i in 0 until maxIterations) {
            converged = step(means, inputData)
            if (converged) {
                if (DEBUG) Log.d(TAG, "Converged at iteration: $i")
                break
            }
        }
        if (!converged && DEBUG) Log.d(TAG, "Did not converge")
        return means
    }

    private fun step(means: MutableList<Mean>, inputData: Array<ArrayRealVector>): Boolean {
        // Clear previous state
        means.forEach { it.closestItems.clear() }

        // Assign points to nearest mean
        inputData.forEach { point ->
            val nearest = nearestMean(point, means)
            nearest.closestItems.add(point)
        }
        var converged = true
        // Move each mean towards the nearest data set points
        means.forEach { mean ->
            if (mean.closestItems.isEmpty()) return@forEach
            // Compute new centroid: sum all points and average
            val oldCentroid = mean.centroid
            mean.centroid = ArrayRealVector(oldCentroid, true)
            mean.closestItems.forEach { point ->
                val pointVector = ArrayRealVector(point, false)
                mean.centroid.add(pointVector)
            }
            if(mean.closestItems.isNotEmpty()) {
                mean.centroid.mapMultiply(1.0 / mean.closestItems.size)
            }

            mean.closestItems
            // Check if centroid moved significantly
            if (oldCentroid.getDistance(mean.centroid) > sqConvergenceEpsilon) {
                converged = false
            }
        }
        return converged
    }

    private fun nearestMean(point: ArrayRealVector, means: List<Mean>): Mean {
        var nearest: Mean? = null
        var nearestDistance = Double.MAX_VALUE
        means.forEach { mean ->
            val distance = point.getDistance(mean.centroid)
            if (distance < nearestDistance) {
                nearest = mean
                nearestDistance = distance
            }
        }
        return nearest ?: throw IllegalStateException("No means available")
    }


}