package com.example.kianarag.di

import com.example.kianarag.graph.Graph
import com.example.kianarag.rag.Splitter

val graph = Graph()
val splitter = Splitter(chunkSize = 100, chunkOverlap = 20)