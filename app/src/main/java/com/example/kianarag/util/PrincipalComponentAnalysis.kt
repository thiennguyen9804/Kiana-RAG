package com.example.kianarag.util


// Principal Component Analysis
class PrincipalComponentAnalysis(val nComponents: Int) {
    private lateinit var mean: FloatArray
    private lateinit var components: Array<FloatArray> // Ma trận W (D x nComponents)
    private lateinit var explainedVariance: FloatArray // Eigenvalues

    /**
     * Fit PCA model and transform input data to lower-dimensional space.
     * @param data Input data as Array<FloatArray> (N x D matrix)
     * @return Transformed data (N x nComponents matrix)
     */
    fun fitTransform(data: Array<FloatArray>): Array<FloatArray> {
        checkDataSetSanity(data)
        val dimension = data[0].size
        require(nComponents <= dimension) { "nComponents must be <= input dimension." }

        // 1. Chuẩn hóa dữ liệu (zero-mean)
        centerData(data)

        // 2. Tính ma trận hiệp phương sai
        val covMatrix = computeCovarianceMatrix(data)

        // 3. Phân rã eigenvalue (giả lập, cần thư viện như Apache Commons Math)
        val (eigenValues, eigenVectors) = computeEigenDecomposition(covMatrix)

        // 4. Chọn nComponents eigenvectors có eigenvalue lớn nhất
        val sortedIndices = eigenValues.indices.sortedByDescending { eigenValues[it] }
        explainedVariance = sortedIndices.take(nComponents).map { eigenValues[it] }.toFloatArray()
        components = sortedIndices.take(nComponents).map { eigenVectors[it] }.toTypedArray()

        // 5. Ánh xạ dữ liệu sang không gian mới
        return transform(data)
    }

    /**
     * Transform new data using fitted PCA model.
     * @param data Input data as Array<FloatArray>
     * @return Transformed data
     */
    fun transform(data: Array<FloatArray>): Array<FloatArray> {
        require(this::components.isInitialized) { "PCA model must be fitted first." }
        val centeredData = data.map { row ->
            row.mapIndexed { i, value -> value - mean[i] }.toFloatArray()
        }.toTypedArray()
        return matrixMultiply(centeredData, components)
    }

    /**
     * Get explained variance ratio.
     * @return Array of explained variance ratios for each component
     */
    fun getExplainedVarianceRatio(): FloatArray {
        require(this::explainedVariance.isInitialized) { "PCA model must be fitted first." }
        val totalVariance = explainedVariance.sum()
        return explainedVariance.map { it / totalVariance }.toFloatArray()
    }

    /**
     * Get principal components (W matrix).
     * @return Array of principal components
     */
    fun getComponents(): Array<FloatArray> {
        require(this::components.isInitialized) { "PCA model must be fitted first." }
        return components
    }

    /**
     * Center data by subtracting mean of each dimension.
     * @param data Input data
     */
    private fun centerData(data: Array<FloatArray>) {
        val dimension = data[0].size
        mean = FloatArray(dimension) { i ->
            data.map { it[i] }.average().toFloat()
        }
        // Center data in-place (optional, depending on use case)
        data.forEach { row ->
            row.forEachIndexed { i, _ -> row[i] -= mean[i] }
        }
    }

    /**
     * Compute covariance matrix of centered data.
     * @param data Centered data
     * @return Covariance matrix (D x D)
     */
    private fun computeCovarianceMatrix(data: Array<FloatArray>): Array<FloatArray> {
        val n = data.size
        val d = data[0].size
        val covMatrix = Array(d) { FloatArray(d) }
        for (i in 0 until d) {
            for (j in i until d) {
                var sum = 0f
                for (row in data) {
                    sum += row[i] * row[j]
                }
                covMatrix[i][j] = sum / (n - 1)
                covMatrix[j][i] = covMatrix[i][j] // Symmetric matrix
            }
        }
        return covMatrix
    }

    /**
     * Compute eigen decomposition (simplified, assumes external library for real implementation).
     * @param covMatrix Covariance matrix
     * @return Pair of eigenvalues and eigenvectors
     */
    private fun computeEigenDecomposition(covMatrix: Array<FloatArray>): Pair<FloatArray, Array<FloatArray>> {
        // Giả lập: Trong thực tế, sử dụng thư viện như Apache Commons Math hoặc ND4J để tính SVD/eigen decomposition
        val d = covMatrix.size
        val eigenValues = FloatArray(d) { 1f } // Placeholder
        val eigenVectors = Array(d) { FloatArray(d) { 1f } } // Identity matrix as placeholder
        // TODO: Thay bằng gọi thư viện SVD/eigen decomposition
        return eigenValues to eigenVectors
    }

    /**
     * Matrix multiplication: data (N x D) * components (D x k) = transformed data (N x k)
     */
    private fun matrixMultiply(data: Array<FloatArray>, components: Array<FloatArray>): Array<FloatArray> {
        val n = data.size
        val k = components[0].size
        val result = Array(n) { FloatArray(k) }
        for (i in 0 until n) {
            for (j in 0 until k) {
                var sum = 0f
                for (p in data[0].indices) {
                    sum += data[i][p] * components[p][j]
                }
                result[i][j] = sum
            }
        }
        return result
    }
}