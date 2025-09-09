package com.example.kianarag.rag

import org.apache.commons.math3.linear.ArrayRealVector
import org.pytorch.Module



class EmbeddingModel {
    lateinit var modelFolder: String
    val module = Module.load(modelFolder)
    init {

    }
    fun embed(input: String): ArrayRealVector {
        return TODO("Provide the return value")
    }
    fun embed(inputs: List<String>): List<ArrayRealVector> {
        return TODO("Provide the return value")
    }
}