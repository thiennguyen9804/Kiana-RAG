package com.example.kianarag.rag

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import android.content.Context
import java.util.Locale

class Tokenizer(context: Context) {
    private val vocab: Map<String, Int>
    private val mTokenIdMap: Map<String, Long>
    private val MODEL_INPUT_LENGTH = 512
    private val EXTRA_ID_NUM = 3
    private val CLS = "[CLS]"
    private val SEP = "[SEP]"

    init {
        // Đọc vocab từ assets
        val vocabText = context.assets.open("tokenizer/vocab.txt").bufferedReader().use { it.readText() }
        vocab = vocabText.lines().mapIndexed { index, token -> token to index }.toMap()

        // Đọc special tokens (hoặc hard-code cho đơn giản)
        mTokenIdMap = mapOf(
            CLS to vocab[CLS]!!.toLong(),
            SEP to vocab[SEP]!!.toLong()
        )
    }

    private fun wordPieceTokenizer(text: String): IntArray {
        // WordPiece tokenization đơn giản
        val tokens = text.toLowerCase(Locale.ROOT).split(" ").flatMap { word ->
            // Tìm subwords (giả sử đơn giản, thực tế cần WordPiece algorithm)
            val subwords = mutableListOf<String>()
            if (word in vocab) {
                subwords.add(word)
            } else {
                // Xử lý unknown hoặc subword (cần thuật toán WordPiece đầy đủ)
                subwords.add("[UNK]")
            }
            subwords
        }

        return tokens.map { vocab[it] ?: vocab["[UNK]"]!! }.toIntArray()
    }

    fun tokenizer(question: String, text: String): LongArray {
        val tokenIdsQuestion = wordPieceTokenizer(question)
        if (tokenIdsQuestion.size >= MODEL_INPUT_LENGTH) throw IllegalArgumentException("Question too long")
        val tokenIdsText = wordPieceTokenizer(text)
        val inputLength = tokenIdsQuestion.size + tokenIdsText.size + EXTRA_ID_NUM
        val ids = LongArray(MODEL_INPUT_LENGTH.coerceAtMost(inputLength))

        ids[0] = mTokenIdMap[CLS]!!
        for (i in tokenIdsQuestion.indices) ids[i + 1] = tokenIdsQuestion[i].toLong()
        ids[tokenIdsQuestion.size + 1] = mTokenIdMap[SEP]!!
        val maxTextLength =
            tokenIdsText.size.coerceAtMost(MODEL_INPUT_LENGTH - tokenIdsQuestion.size - EXTRA_ID_NUM)
        for (i in 0 until maxTextLength) {
            ids[tokenIdsQuestion.size + i + 2] = tokenIdsText[i].toLong()
        }
        ids[tokenIdsQuestion.size + maxTextLength + 2] = mTokenIdMap[SEP]!!

        // Padding nếu cần
        val paddedIds = LongArray(MODEL_INPUT_LENGTH) { if (it < ids.size) ids[it] else 0L }
        return paddedIds
    }

    fun createAttentionMask(inputIds: LongArray): LongArray {
        return LongArray(inputIds.size) { if (inputIds[it] != 0L) 1L else 0L }
    }
}