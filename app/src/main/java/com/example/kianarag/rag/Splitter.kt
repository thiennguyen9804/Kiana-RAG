package com.example.kianarag.rag

import android.util.Log
import kotlin.math.min


// TODO: Code của LLM, độ chính xác chưa cao,
// TODO: chỉ đảm bảo có cái chạy được, sẽ thay đổi trong tương lai
class RecursiveCharacterTextSplitter(
    private val chunkSize: Int = 400,
    private val chunkOverlap: Int = 200,
    private val separators: List<String> = listOf("\n\n", "\n", " ", ""),
    private val keepSeparator: Boolean = true
) {
    companion object {
        private const val TAG = "RecursiveCharacterTextSplitter"
    }

    fun splitText(text: String): List<String> {
        val chunks = mutableListOf<String>()
        splitRecursive(text, separators, chunks)
        val chunkOverlapped = applyOverlap(chunks)
        Log.d(TAG, chunkOverlapped.toString())
        return chunkOverlapped
    }

    private fun splitRecursive(
        text: String,
        seps: List<String>,
        output: MutableList<String>
    ) {
        // Nếu đoạn quá ngắn, thêm vào output
        if (text.length <= chunkSize) {
            output.add(text.trim())
            return
        }

        // Chọn separator đầu tiên xuất hiện trong văn bản
        val sep = seps.find { if (it.isEmpty()) false else text.contains(it) } ?: seps.last()
        val restSeps = if (sep == seps.last()) emptyList() else seps.drop(seps.indexOf(sep) + 1)

        // Tách văn bản sử dụng regex để giữ separator nếu cần
        val splits = if (sep.isEmpty()) listOf(text) else text.split(Regex(Regex.escape(sep)))
        val parts = if (keepSeparator && sep.isNotEmpty()) {
            splits.dropLast(1).map { it + sep } + splits.last()
        } else {
            splits
        }

        // Buffer để tích lũy các phần nhỏ
        val goodSplits = mutableListOf<String>()
        for (part in parts) {
            if (part.length <= chunkSize) {
                goodSplits.add(part)
            } else {
                // Hợp nhất goodSplits trước khi đệ quy
                mergeSplits(goodSplits, sep, output)
                // Đệ quy trên phần lớn
                splitRecursive(part, restSeps, output)
            }
        }

        // Hợp nhất phần còn lại trong goodSplits
        if (goodSplits.isNotEmpty()) {
            mergeSplits(goodSplits, sep, output)
        }
    }

    // Hợp nhất các phần nhỏ thành chunks gần chunkSize
    private fun mergeSplits(splits: MutableList<String>, sep: String, output: MutableList<String>) {
        val currentDoc = mutableListOf<String>()
        var currentLength = 0

        for (split in splits) {
            val projectedLength = currentLength + split.length + if (currentDoc.isNotEmpty()) sep.length else 0
            if (projectedLength > chunkSize && currentDoc.isNotEmpty()) {
                // Tạo chunk từ currentDoc
                output.add(currentDoc.joinToString(sep).trim())
                currentDoc.clear()
                currentLength = 0
            }
            currentDoc.add(split)
            currentLength += split.length + if (currentDoc.size > 1) sep.length else 0
        }

        // Xử lý phần còn lại
        if (currentDoc.isNotEmpty()) {
            val finalText = currentDoc.joinToString(sep).trim()
            if (finalText.length <= chunkSize) {
                output.add(finalText)
            } else {
                // Đệ quy tiếp nếu finalText quá dài
                splitRecursive(finalText, listOf(""), output)
            }
        }
    }

    // Áp dụng overlap cho chunks
    private fun applyOverlap(chunks: List<String>): List<String> {
        if (chunkOverlap <= 0) return chunks
        if (chunks.isEmpty()) return chunks

        val overlapped = mutableListOf<String>()
        overlapped.add(chunks[0]) // Chunk đầu không cần overlap

        for (i in 1 until chunks.size) {
            val prevChunk = chunks[i - 1] // Dùng chunk gốc
            val overlapPart = prevChunk.takeLast(min(chunkOverlap, prevChunk.length))
            overlapped.add((overlapPart + chunks[i]).trim())
        }
        return overlapped
    }
}