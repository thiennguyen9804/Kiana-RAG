package com.example.kianarag.rag

class Splitter(
    private val chunkSize: Int,
    private val chunkOverlap: Int = 0,
    private val lengthFunction: (String) -> Int = { it.length }
) {
    init {
        require(chunkSize > 0) { "chunkSize must be positive" }
        require(chunkOverlap >= 0) { "chunkOverlap must be non-negative" }
        require(chunkOverlap < chunkSize) { "chunkOverlap must be less than chunkSize" }
    }
    fun split(text: String): Pair<List<String>, List<Int>> {
        if (text.isEmpty()) return emptyList<String>() to emptyList<Int>()

        val chunks = mutableListOf<String>()
        val offsets = mutableListOf<Long>()
        var startIndex = 0

        while (startIndex < lengthFunction(text)) {
            // Tính endIndex cho chunk hiện tại
            val endIndex = minOf(startIndex + chunkSize, text.length)

            // Lấy chunk
            val chunk = text.substring(startIndex, endIndex)
            chunks.add(chunk)
            offsets.add(startIndex)

            // Cập nhật startIndex cho chunk tiếp theo, tính overlap
            startIndex += (chunkSize - chunkOverlap)
        }

        return chunks to offsets

    }
}