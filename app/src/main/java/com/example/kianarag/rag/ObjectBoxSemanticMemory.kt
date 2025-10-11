package com.example.kianarag.rag

import com.example.kianarag.data.Chunk
import com.google.ai.edge.localagents.rag.memory.SemanticMemory
import com.google.ai.edge.localagents.rag.memory.VectorStore
import com.google.ai.edge.localagents.rag.memory.VectorStoreRecord
import com.google.ai.edge.localagents.rag.models.EmbedData
import com.google.ai.edge.localagents.rag.models.Embedder
import com.google.ai.edge.localagents.rag.models.EmbeddingRequest
import com.google.ai.edge.localagents.rag.retrieval.RetrievalConfig
import com.google.ai.edge.localagents.rag.retrieval.RetrievalEntity
import com.google.ai.edge.localagents.rag.retrieval.RetrievalRequest
import com.google.ai.edge.localagents.rag.retrieval.RetrievalResponse
import com.google.ai.edge.localagents.rag.retrieval.SemanticDataEntry
import com.google.common.base.Function
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.ThreadFactoryBuilder
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import com.google.ai.edge.localagents.rag.models.EmbedData.TaskType

//private class ObjectBoxSemanticMemory(
//    private val vectorStore: VectorStore<Chunk>,
//    private val embeddingModel: Embedder<String>,
//    private val workerExecutor: Executor = Executors.newSingleThreadExecutor(
//        (ThreadFactoryBuilder()).setNameFormat("object-box-semantic-text-memory-pool-%d")
//            .setPriority(5).build()
//    )
//) : SemanticMemory<Chunk> {
//    override fun retrieveResults(request: RetrievalRequest<Chunk>): ListenableFuture<RetrievalResponse<Chunk>> {
//        val embedDataBuilder: EmbedData.Builder<Chunk?> =
//            EmbedData.builder<Chunk?>().setData(request.getQuery() as Chunk).setIsQuery(true)
//        when (request.config.task) {
//            RetrievalConfig.TaskType.TASK_UNSPECIFIED, RetrievalConfig.TaskType.RETRIEVAL_QUERY -> embedDataBuilder.setTask(
//                TaskType.RETRIEVAL_QUERY
//            )
//
//            RetrievalConfig.TaskType.QUESTION_ANSWERING -> embedDataBuilder.setTask(TaskType.QUESTION_ANSWERING)
//            RetrievalConfig.TaskType.FACT_VERIFICATION -> embedDataBuilder.setTask(TaskType.FACT_VERIFICATION)
//            RetrievalConfig.TaskType.CODE_RETRIEVAL -> embedDataBuilder.setTask(TaskType.CODE_RETRIEVAL)
//        }
//        val embeddingRequest =
//            EmbeddingRequest.create<Chunk?>(ImmutableList.of(embedDataBuilder.build()))
//        return Futures.transform(embeddingModel.getEmbeddings(embeddingRequest.), { embeddings ->
//            val records: MutableList<VectorStoreRecord<Chunk?>?> =
//                this.vectorStore.getNearestRecords(
//                    embeddings,
//                    request.config.topK,
//                    request.config.minSimilarityScore
//                )
//            val entities = records.stream()
//                .map { record: VectorStoreRecord<Chunk?>? ->
//                    RetrievalEntity.builder<Chunk?>().setData(record!!.getData())
//                        .setEmbeddings(record.embeddings).setMetadata(record.metadata)
//                        .build()
//                }
//                .collect(ImmutableList.toImmutableList())
//            RetrievalResponse.create(entities)
//        }, workerExecutor)
//    }
//
//    override fun recordMemoryItem(item: Chunk): ListenableFuture<Boolean> {
//        val dataEntry = SemanticDataEntry.create(item)
//        return recordMemoryEntry(dataEntry)
//    }
//
//    override fun recordMemoryEntry(dataEntry: SemanticDataEntry<Chunk>): ListenableFuture<Boolean> {
//        val embedText = dataEntry.getCustomEmbeddingData().orElse(dataEntry.getData())
//        val embedData = EmbedData.builder<Chunk>()
//            .setData(embedText)
//            .setTask(TaskType.RETRIEVAL_DOCUMENT)
//            .build()
//        val embeddingRequest = EmbeddingRequest.create(ImmutableList.of(embedData))
//        return Futures.transform(
//            embeddingModel.getEmbeddings(embeddingRequest),
//            { embeddings ->
//                val record = VectorStoreRecord.builder<Chunk>()
//                    .setData(dataEntry.getData())
//                    .setEmbeddings(embeddings)
//                    .setMetadata(dataEntry.metadata)
//                    .build()
//                vectorStore.insert(record)
//                true
//            },
//            workerExecutor
//        )
//    }
//
//    override fun recordBatchedMemoryItems(items: ImmutableList<Chunk>): ListenableFuture<Boolean> {
//        val dataEntries = items.stream()
//            .map { SemanticDataEntry.create(it) }
//            .collect(ImmutableList.toImmutableList())
//        return recordBatchedMemoryEntries(dataEntries)
//    }
//
//    override fun recordBatchedMemoryEntries(dataEntries: ImmutableList<SemanticDataEntry<Chunk>>): ListenableFuture<Boolean> {
//        val entries = dataEntries.stream()
//            .map { dataEntry ->
//                EmbedData.builder<Chunk>()
//                    .setData(dataEntry.getCustomEmbeddingData().orElse(dataEntry.getData()))
//                    .setTask(TaskType.RETRIEVAL_DOCUMENT)
//                    .build()
//            }
//            .collect(ImmutableList.toImmutableList())
//        val request = EmbeddingRequest.create(entries)
//        return Futures.transform(
//            embeddingModel.getBatchEmbeddings(request),
//            { embeddingsList ->
//                if (embeddingsList.size != dataEntries.size) {
//                    throw AssertionError(
//                        "Embeddings list size is not equal to memory entries size, ${embeddingsList.size} != ${dataEntries.size}"
//                    )
//                }
//                for (i in embeddingsList.indices) {
//                    val record = VectorStoreRecord.builder<Chunk>()
//                        .setData(dataEntries[i].getData())
//                        .setEmbeddings(embeddingsList[i])
//                        .setMetadata(dataEntries[i].metadata)
//                        .build()
//                    vectorStore.insert(record)
//                }
//                true
//            },
//            workerExecutor
//        )
//    }
//
//
//}