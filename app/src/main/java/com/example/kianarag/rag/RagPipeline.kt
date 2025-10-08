package com.example.kianarag.rag

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.kianarag.KianaRAGApplication
import com.example.kianarag.util.PdfLoader
import com.google.ai.edge.localagents.rag.chains.ChainConfig
import com.google.ai.edge.localagents.rag.chains.RetrievalAndInferenceChain
import com.google.ai.edge.localagents.rag.memory.DefaultSemanticTextMemory
import com.google.ai.edge.localagents.rag.memory.SqliteVectorStore
import com.google.ai.edge.localagents.rag.models.AsyncProgressListener
import com.google.ai.edge.localagents.rag.models.Embedder
import com.google.ai.edge.localagents.rag.models.GeminiEmbedder
import com.google.ai.edge.localagents.rag.models.GemmaEmbeddingModel
import com.google.ai.edge.localagents.rag.models.LanguageModelResponse
import com.google.ai.edge.localagents.rag.models.MediaPipeLlmBackend
import com.google.ai.edge.localagents.rag.prompt.PromptBuilder
import com.google.ai.edge.localagents.rag.retrieval.RetrievalConfig
import com.google.ai.edge.localagents.rag.retrieval.RetrievalConfig.TaskType
import com.google.ai.edge.localagents.rag.retrieval.RetrievalRequest
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.ListenableFuture
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInference.LlmInferenceOptions
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.jvm.optionals.getOrNull

class RagPipeline(
    private val application: Application,
    private val pdfLoader: PdfLoader,
    private val splitter: RecursiveCharacterTextSplitter
) {

    private val mediaPipeLanguageModelOptions: LlmInferenceOptions =
        LlmInferenceOptions.builder().setModelPath(
            GEMMA_MODEL_PATH
        ).setPreferredBackend(LlmInference.Backend.GPU).setMaxTokens(1024).build()
    private val mediaPipeLanguageModelSessionOptions: LlmInferenceSession.LlmInferenceSessionOptions =
        LlmInferenceSession.LlmInferenceSessionOptions.builder().setTemperature(1.0f)
            .setTopP(0.95f).setTopK(64).build()
    private val mediaPipeLanguageModel: MediaPipeLlmBackend =
        MediaPipeLlmBackend(
            application.applicationContext, mediaPipeLanguageModelOptions,
            mediaPipeLanguageModelSessionOptions
        )

    private val embedder: Embedder<String> = if (COMPUTE_EMBEDDINGS_LOCALLY) {
        GemmaEmbeddingModel(
            EMBEDDING_GEMMA_MODEL_PATH,
            TOKENIZER_MODEL_PATH,
            USE_GPU_FOR_EMBEDDINGS,
        )
    } else {
        GeminiEmbedder(
            GEMINI_EMBEDDING_MODEL,
            GEMINI_API_KEY
        )
    }

    private val config = ChainConfig.create(
        mediaPipeLanguageModel,
        PromptBuilder(PROMPT_TEMPLATE),
        DefaultSemanticTextMemory(
            // Gecko embedding model dimension is 768
            SqliteVectorStore(768), embedder
        )
    )
    private val retrievalAndInferenceChain = RetrievalAndInferenceChain(config)

    init {
        mediaPipeLanguageModel.initialize()
    }

    fun memorizeChunks(context: Context, filename: String) {
        // BufferedReader is needed to read the *.txt file
        // Create and Initialize BufferedReader
//        val reader = BufferedReader(
//            InputStreamReader(
//                context.assets.open(filename)
//            )
//        )
        val sb = StringBuilder()
//        val texts = mutableListOf<String>()
//        generateSequence { reader.readLine() }
//            .forEach { line ->
//                if (line.startsWith(CHUNK_SEPARATOR)) {
//                    if (sb.isNotEmpty()) {
//                        val chunk = sb.toString()
//                        texts.add(chunk)
//                    }
//                    sb.clear()
//                    sb.append(line.removePrefix(CHUNK_SEPARATOR).trim())
//                } else {
//                    sb.append(" ")
//                    sb.append(line)
//                }
//            }
//        if (sb.isNotEmpty()) {
//            texts.add(sb.toString())
//        }
//        reader.close()
//        if (texts.isNotEmpty()) {
//            return memorize(texts)
//        }

        val (content, _) = pdfLoader.load(filename)
        val texts = splitter.splitText(content)
        memorize(texts)

        Log.d(TAG, "Memorize done!!!")
    }

    /** Stores input texts in the semantic text memory. */
    private fun memorize(facts: List<String>) {
        val future = config.semanticMemory
            .getOrNull()
            ?.recordBatchedMemoryItems(ImmutableList.copyOf(facts))
        future?.get()
    }

    /** Generates the response from the LLM. */
    suspend fun generateResponse(
        prompt: String,
        callback: AsyncProgressListener<LanguageModelResponse>?,
    ): String =
        coroutineScope {
            val retrievalRequest =
                RetrievalRequest.create(
                    prompt,
                    RetrievalConfig.create(3, 0.0f, TaskType.QUESTION_ANSWERING)
                )
            retrievalAndInferenceChain.invoke(retrievalRequest, callback).await().text
        }

    companion object {
        private const val COMPUTE_EMBEDDINGS_LOCALLY = true
        private const val USE_GPU_FOR_EMBEDDINGS = true
        private const val CHUNK_SEPARATOR = "<chunk_splitter>"
        private const val GEMMA_MODEL_PATH = "/data/local/tmp/llm/gemma3.task"
        private const val TOKENIZER_MODEL_PATH = "/data/local/tmp/sentencepiece.model"
        private const val EMBEDDING_GEMMA_MODEL_PATH = "/data/local/tmp/gecko.tflite"
        private const val GEMINI_EMBEDDING_MODEL = "models/text-embedding-004"
        private const val GEMINI_API_KEY = "..."

        // The prompt template for the RetrievalAndInferenceChain. It takes two inputs: {0}, which is the retrieved context, and {1}, which is the user's query.
        private const val PROMPT_TEMPLATE: String =
            "You are an assistant for question-answering tasks. Here are the things I want to remember: {0} Use the things I want to remember, answer the following question the user has: {1}"
        private const val TAG: String = "RAG PIPELINE"
    }
}
