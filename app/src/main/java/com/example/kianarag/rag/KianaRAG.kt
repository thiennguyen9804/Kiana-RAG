package com.example.kianarag.rag

import com.example.kianarag.data.Document
import com.example.kianarag.data.DocumentManager
import com.example.kianarag.data.FileMetadata
import com.example.kianarag.data.MetadataManager
import com.example.kianarag.data.PdfLoader
import com.example.kianarag.graph.hub_nsw.HubNSWGraph
import com.example.kianarag.graph.hub_nsw.point.PqHubNSWPoint
import org.apache.commons.math3.linear.ArrayRealVector

val graph = HubNSWGraph()

class KianaRAG {
    lateinit var embeddings: List<ArrayRealVector>
    val points: List<PqHubNSWPoint> = mutableListOf()
    val docIds = mutableListOf<String>()
    val metadataIds = mutableListOf<String>()

    // chunk content + offset
    private fun split(input: String): Pair<List<String>, List<Long>> {

        return TODO("Provide the return value")
    }


    // return doc id + offset
    private fun saveToDatabase(paths: List<String>) {
        paths.forEach {
            val (content, filename) = PdfLoader.load(it)
            val doc = Document(
                id = filename,
                pointer = it,
            )
            DocumentManager.saveToDatabase(doc)
            docIds.add(filename)


            val (splitTexts, offsets) = split(content)
            splitTexts.zip(offsets) { text, offset ->
                val fileMetadata = FileMetadata(
                    docId = filename,
                    chunkContent = text,
                    chunkOffset = offset
                )

                MetadataManager.saveToDatabase(fileMetadata = fileMetadata)
            }
        }

    }

    fun index(paths: List<String>) {
        saveToDatabase(paths)
    }


}