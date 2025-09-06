package com.example.kianarag.ml.kmeans

import android.util.Log
import com.example.kianarag.util.checkDataSetSanity
import com.example.kianarag.util.sqL2DistanceTo
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

    fun fitPredict(k: Int, inputData: Array<FloatArray>): List<Mean> {
        checkDataSetSanity(inputData)
        val dimension = inputData[0].size
        val means = mutableListOf<Mean>()

        repeat(k) {
            val centroid = FloatArray(dimension) { randomState.nextFloat() }
            means.add(Mean(centroid))
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

    private fun step(means: MutableList<Mean>, inputData: Array<FloatArray>): Boolean {
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
            mean.centroid = FloatArray(oldCentroid.size)
            mean.closestItems.forEach { point ->
                mean.centroid.forEachIndexed { index, _ ->
                    mean.centroid[index] += point[index]
                }
            }
            mean.centroid.forEachIndexed { index, value ->
                mean.centroid[index] = value / mean.closestItems.size
            }

            // Check if centroid moved significantly
            if (oldCentroid.sqL2DistanceTo(mean.centroid) > sqConvergenceEpsilon) {
                converged = false
            }


        }
        return converged
    }

    private fun nearestMean(point: FloatArray, means: List<Mean>): Mean {
        var nearest: Mean? = null
        var nearestDistance = Float.MAX_VALUE
        means.forEach { mean ->
            val distance = point.sqL2DistanceTo(mean.centroid)
            if (distance < nearestDistance) {
                nearest = mean
                nearestDistance = distance
            }
        }
        return nearest ?: throw IllegalStateException("No means available")
    }


}