package com.example.kianarag.rag.embedding

import org.apache.commons.math3.linear.ArrayRealVector
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader

class EmbeddingModel(
    private val tokenizer: Tokenizer,
    absoluteModelPath: String?
) {
    private val module = LiteModuleLoader.load(absoluteModelPath)
    init {
    }
    fun embed(text: String): FloatArray? {
        return try {
            // Get input tensors from tokenizer
            val (inputTensor, maskTensor) = tokenizer.getInputTensors(text)

            // Run inference
            val outputTensor = module!!.forward(
                IValue.from(inputTensor),
                IValue.from(maskTensor)
            ).toTensor()
            outputTensor.dataAsFloatArray // Shape: [384]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun embed(inputs: List<String>): List<ArrayRealVector> {
        return TODO("Provide the return value")
    }
}