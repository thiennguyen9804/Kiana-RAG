package com.example.kianarag.di

import com.example.kianarag.data.CodebooksManager
import com.example.kianarag.graph.Graph
import com.example.kianarag.ml.product_quantization.ProductQuantization
import com.example.kianarag.rag.RecursiveCharacterTextSplitter
import com.example.kianarag.util.PdfLoader
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val graph = Graph()

val embeddingCodebooks = CodebooksManager()
val centroidsCodebooks = CodebooksManager()

val embeddingPq = ProductQuantization(embeddingCodebooks)
val centroidsPq = ProductQuantization(centroidsCodebooks)

val embeddingGraph = Graph()
val centroidGraph = Graph()

val ragModule = module {
    single {
        PdfLoader(androidApplication())
    }
    single {
        RecursiveCharacterTextSplitter()
    }
}