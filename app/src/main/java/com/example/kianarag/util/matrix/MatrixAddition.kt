package com.example.kianarag.util.matrix


class MatrixAddition {
    companion object {
        init {
            System.loadLibrary("matrix")
        }

    }
    private external fun cal(a: FloatArray, b: FloatArray, c: FloatArray, size: Int): Unit

    fun cal(a: FloatArray, b: FloatArray): FloatArray {
        val c = FloatArray(a.size)
        val size = a.size
        cal(a, b, c, size)
        return c
    }

}