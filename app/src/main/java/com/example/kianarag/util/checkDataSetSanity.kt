package com.example.kianarag.util

// Kiểm tra xem:
//  Tập vector không rỗng,
//  Tập không chứa vector rỗng,
//  Các vector có cùng chiều
fun checkDataSetSanity(inputData: Array<FloatArray>) {
    require(inputData.isNotEmpty()) { "Data set is empty." }
    requireNotNull(inputData[0]) { "Bad data set format." }
    val dimension = inputData[0].size
    val length = inputData.size
    for (i in 1..<length) {
        require(inputData[i].size == dimension) { "Bad data set format." }
    }
}