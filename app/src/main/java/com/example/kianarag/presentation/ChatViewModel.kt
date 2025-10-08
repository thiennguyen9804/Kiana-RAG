package com.example.kianarag.presentation

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kianarag.rag.RagPipeline
import com.example.kianarag.rag.RecursiveCharacterTextSplitter
import com.example.kianarag.util.PdfLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChatViewModel(
    private val application: Application
) : AndroidViewModel(application = application) {
    private val pdfLoader = PdfLoader(application)
    private val splitter = RecursiveCharacterTextSplitter(
        chunkSize = 200,
        chunkOverlap = 100
    )
    private val ragPipeline = RagPipeline(application, pdfLoader, splitter)

    internal val messages = emptyList<MessageData>().toMutableStateList()
    internal val statistics = mutableStateOf("")

    @SuppressWarnings("FutureReturnValueIgnored")
    fun requestResponse(prompt: String) {
        appendMessage(MessageOwner.User, prompt)
        viewModelScope.launch(Dispatchers.IO) {
            requestResponseFromModel(prompt)
        }
    }

    private suspend fun requestResponseFromModel(prompt: String) {
        val response = ragPipeline.generateResponse(
            prompt,
            callback = { response, done -> updateLastMessage(MessageOwner.Model, response.text) }
        )
        // Có thể xử lý response nếu cần
    }

    fun memorizeChunks(filename: String) {
        viewModelScope.launch(Dispatchers.IO) {
            ragPipeline.memorizeChunks(application.applicationContext, filename)
        }
    }


    private fun appendMessage(role: MessageOwner, message: String) {
        messages.add(MessageData(role, message))
    }

    private fun updateLastMessage(role: MessageOwner, message: String) {
        if (messages.isNotEmpty() && messages[messages.lastIndex].owner == role) {
            messages[messages.lastIndex] = MessageData(role, message)
        } else {
            appendMessage(role, message)
        }
    }
}

enum class MessageOwner {
    User,
    Model,
}

data class MessageData(val owner: MessageOwner, val message: String)