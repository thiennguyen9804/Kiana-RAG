package com.example.kianarag

import com.example.kianarag.util.EmbeddingModel

class KianaRAG(
    private val chunks: List<String>,
) {
    lateinit var embeddings: Array<FloatArray>



    private fun chunkEmbedding() {
        embeddings = EmbeddingModel.embed(chunks)

    }




}