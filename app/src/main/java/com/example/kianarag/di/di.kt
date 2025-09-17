package com.example.kianarag.di

import com.example.kianarag.data.CodebooksManager
import com.example.kianarag.graph.Graph
import com.example.kianarag.ml.product_quantization.ProductQuantization
import com.example.kianarag.rag.Splitter

val graph = Graph()
val splitter = Splitter(chunkSize = 100, chunkOverlap = 20)

val embeddingCodebooks = CodebooksManager()
val centroidsCodebooks = CodebooksManager()

val embeddingPq = ProductQuantization(embeddingCodebooks)
val centroidsPq = ProductQuantization(centroidsCodebooks)

val embeddingGraph = Graph()
val centroidGraph = Graph()
