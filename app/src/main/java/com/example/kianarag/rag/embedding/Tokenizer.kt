package com.example.kianarag.rag.embedding

import android.content.Context
import org.pytorch.Tensor
import java.util.Locale
class Tokenizer(context: Context) {
    private val vocab: Map<String, Int>
    private val mTokenIdMap: Map<String, Long>
    private val mIdTokenMap: Map<Long, String>
    private val MODEL_INPUT_LENGTH = 512
    private val CLS = "[CLS]"
    private val SEP = "[SEP]"
    private val PAD = "[PAD]"

    init {
        // Load vocab.txt from assets/tokenizer/
        val vocabText = context.assets.open("tokenizer/vocab.txt").bufferedReader().use { it.readText() }
        vocab = vocabText.lines().mapIndexed { index, token -> token to index }.toMap()
        mTokenIdMap = mapOf(
            CLS to vocab[CLS]!!.toLong(),
            SEP to vocab[SEP]!!.toLong(),
            PAD to vocab[PAD]!!.toLong()
        )
        mIdTokenMap = vocab.entries.associate { (token, id) -> id.toLong() to token }
    }

    // Simplified WordPiece tokenization (replace with full WordPiece if needed)
    private fun wordPieceTokenizer(text: String): IntArray {
        val tokens = text.lowercase().split(" ").flatMap { word ->
            val subwords = mutableListOf<String>()
            if (word in vocab) {
                subwords.add(word)
            } else {
                subwords.add("[UNK]")
            }
            subwords
        }
        return tokens.map { vocab[it] ?: vocab["[UNK]"]!! }.toIntArray()
    }

    // Tokenize text into input_ids
    fun tokenize(text: String): LongArray {
        val tokenIdsText = wordPieceTokenizer(text)
        val inputLength = tokenIdsText.size + 2 // [CLS] + text + [SEP]
        if (inputLength > MODEL_INPUT_LENGTH) throw IllegalArgumentException("Text too long")

        val ids = LongArray(MODEL_INPUT_LENGTH)
        ids[0] = mTokenIdMap[CLS]!!
        for (i in tokenIdsText.indices) {
            ids[i + 1] = tokenIdsText[i].toLong()
        }
        ids[tokenIdsText.size + 1] = mTokenIdMap[SEP]!!
        // Padding with [PAD]
        for (i in tokenIdsText.size + 2 until MODEL_INPUT_LENGTH) {
            ids[i] = mTokenIdMap[PAD]!!
        }
        return ids
    }

    // Create attention_mask from input_ids
    fun createAttentionMask(inputIds: LongArray): LongArray {
        return LongArray(MODEL_INPUT_LENGTH) { if (it < inputIds.size && inputIds[it] != mTokenIdMap[PAD]!!) 1L else 0L }
    }

    // Utility to get input tensors for model
    fun getInputTensors(text: String): Pair<Tensor, Tensor> {
        val inputIds = tokenize(text)
        val attentionMask = createAttentionMask(inputIds)
        val inputTensor = Tensor.fromBlob(inputIds, longArrayOf(1, MODEL_INPUT_LENGTH.toLong()))
        val maskTensor = Tensor.fromBlob(attentionMask, longArrayOf(1, MODEL_INPUT_LENGTH.toLong()))
        return Pair(inputTensor, maskTensor)
    }
}