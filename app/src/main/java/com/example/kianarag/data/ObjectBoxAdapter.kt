package com.example.kianarag.data

import android.app.Application
import com.google.ai.edge.localagents.rag.memory.SqliteVectorStore
import com.google.ai.edge.localagents.rag.memory.VectorStore
import com.google.ai.edge.localagents.rag.memory.VectorStoreRecord
import com.google.ai.edge.localagents.rag.memory.proto.MemoryRecord
import com.google.common.collect.ImmutableList
import io.objectbox.kotlin.boxFor

class ObjectBoxAdapter(
    private val application: Application
) : VectorStore<String> {
    private val store = MyObjectBox.builder()
        .androidContext(application.applicationContext)
        .build()
    private val chunkBox = store.boxFor(Chunk::class)

    override fun insert(record: VectorStoreRecord<String>) {
        val chunkData = Chunk(
            chunkText = record.data,
            embedding = record.embeddings.toFloatArray()
        )
        chunkBox.put(chunkData)
    }

    override fun getNearestRecords(
        queryEmbeddings: List<Float>,
        topK: Int,
        minSimilarityScore: Float
    ): List<VectorStoreRecord<String>> {
//        require(queryEmbeddings != null) { "queryEmbeddings cannot be null" }
//        require(queryEmbeddings.all { it != null }) { "queryEmbeddings contains null elements" }
        val floatArray = queryEmbeddings.toFloatArray()
        val query = chunkBox
            .query(Chunk_.embedding.nearestNeighbors(floatArray, topK))
            .build()

        val results = query.find()
            .map {
                val memoryRecord = MemoryRecord.newBuilder()
                    .addAllEmbeddings(it.embedding?.asIterable())
                    .build()
                return@map VectorStoreRecord.create(
                    it.chunkText,
                    ImmutableList.copyOf(memoryRecord.embeddingsList),
                )
            }

        query.close()
        return results
    }


}