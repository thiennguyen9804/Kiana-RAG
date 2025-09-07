package com.example.kianarag.ml

import org.apache.commons.math3.linear.*
import org.apache.commons.math3.stat.correlation.Covariance
import com.example.kianarag.util.checkDataSetSanity

// Principal Component Analysis using Apache Commons Math
class PrincipalComponentAnalysis(val nComponents: Int) {
    private lateinit var mean: ArrayRealVector
    private lateinit var components: RealMatrix // Ma trận W (D x nComponents)
    private lateinit var explainedVariance: DoubleArray // Eigenvalues

    /**
     * Fit PCA model and transform input data to lower-dimensional space.
     * @param data Input data as RealMatrix (N x D matrix)
     * @return Transformed data as RealMatrix (N x nComponents matrix)
     */
    fun fitTransform(data: RealMatrix): RealMatrix {
//        checkDataSetSanity(data.data) // Kiểm tra tính hợp lệ
        val dimension = data.columnDimension
        require(nComponents <= dimension) { "nComponents must be <= input dimension." }

        // 1. Chuẩn hóa dữ liệu (zero-mean)
        centerData(data)

        // 2. Tính ma trận hiệp phương sai
        val covMatrix = computeCovarianceMatrix(data)

        // 3. Phân rã eigenvalue
        val (eigenValues, eigenVectors) = computeEigenDecomposition(covMatrix)

        // 4. Chọn nComponents eigenvectors có eigenvalue lớn nhất
        val sortedIndices = eigenValues.indices.sortedByDescending { eigenValues[it] }
        explainedVariance = sortedIndices.take(nComponents).map { eigenValues[it] }.toDoubleArray()
        components = Array2DRowRealMatrix(dimension, nComponents)
        sortedIndices.take(nComponents).forEachIndexed { j, idx ->
            components.setColumn(j, eigenVectors.getColumn(idx))
        }

        // 5. Ánh xạ dữ liệu sang không gian mới
        return transform(data)
    }

    /**
     * Transform new data using fitted PCA model.
     * @param data Input data as RealMatrix (N x D matrix)
     * @return Transformed data as RealMatrix (N x nComponents matrix)
     */
    fun transform(data: RealMatrix): RealMatrix {
        require(this::components.isInitialized) { "PCA model must be fitted first." }
        // Chuẩn hóa dữ liệu
        val centeredData = Array2DRowRealMatrix(data.rowDimension, data.columnDimension)
        for (i in 0 until data.rowDimension) {
            val rowVector = data.getRowVector(i)
            centeredData.setRowVector(i, rowVector.subtract(mean))
        }
        // Nhân ma trận: centeredData (N x D) * components (D x k)
        return centeredData.multiply(components)
    }

    /**
     * Get explained variance ratio.
     * @return Array of explained variance ratios for each component
     */
    fun getExplainedVarianceRatio(): DoubleArray {
        require(this::explainedVariance.isInitialized) { "PCA model must be fitted first." }
        val totalVariance = explainedVariance.sum()
        return explainedVariance.map { it / totalVariance }.toDoubleArray()
    }

    /**
     * Get principal components (W matrix).
     * @return Principal components as RealMatrix (D x nComponents)
     */
    fun getComponents(): RealMatrix {
        require(this::components.isInitialized) { "PCA model must be fitted first." }
        return components
    }

    /**
     * Center data by subtracting mean of each dimension.
     * @param data Input data as RealMatrix (modified in-place)
     */
    private fun centerData(data: RealMatrix) {
        val dimension = data.columnDimension
        mean = ArrayRealVector(dimension)
        // Tính mean cho mỗi chiều
        for (i in 0 until dimension) {
            val column = data.getColumn(i)
            mean.setEntry(i, column.average())
        }
        // Chuẩn hóa dữ liệu
        for (i in 0 until data.rowDimension) {
            val rowVector = data.getRowVector(i)
            data.setRowVector(i, rowVector.subtract(mean))
        }
    }

    /**
     * Compute covariance matrix of centered data.
     * @param data Centered data as RealMatrix
     * @return Covariance matrix (D x D)
     */
    private fun computeCovarianceMatrix(data: RealMatrix): RealMatrix {
        return Covariance(data).covarianceMatrix
    }

    /**
     * Compute eigen decomposition of covariance matrix.
     * @param covMatrix Covariance matrix
     * @return Pair of eigenvalues and eigenvectors
     */
    private fun computeEigenDecomposition(covMatrix: RealMatrix): Pair<DoubleArray, RealMatrix> {
        val eigen = EigenDecomposition(covMatrix)
        return eigen.realEigenvalues to eigen.v
    }
}