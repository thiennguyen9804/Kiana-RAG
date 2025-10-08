package com.example.kianarag.rag

import android.util.Log
import kotlin.math.min


// TODO: Code của LLM, độ chính xác chưa cao,
// TODO: chỉ đảm bảo có cái chạy được, sẽ thay đổi trong tương lai
class BasicCharacterSplitter(
    private val chunkSize: Int = 400,
    private val chunkOverlap: Int = 200,
    private val separators: List<String> = listOf("\n\n", "\n", " ", ""),
    private val keepSeparator: Boolean = true
) {
    companion object {
        private const val TAG = "RecursiveCharacterTextSplitter"
    }

    fun splitText(text: String): List<String> {
        if (text.isEmpty()) return emptyList()

        val chunks = mutableListOf<String>()
        val offsets = mutableListOf<Int>()
        var startIndex = 0

        while (startIndex < text.length) {
            // Tính endIndex cho chunk hiện tại
            val endIndex = minOf(startIndex + chunkSize, text.length)

            // Lấy chunk
            val chunk = text.substring(startIndex, endIndex)
                .trim()
                .replace('\n', ' ')
            chunks.add(chunk)
            offsets.add(startIndex)

            // Cập nhật startIndex cho chunk tiếp theo, tính overlap
            startIndex += (chunkSize - chunkOverlap)
        }

        return chunks
    }


}