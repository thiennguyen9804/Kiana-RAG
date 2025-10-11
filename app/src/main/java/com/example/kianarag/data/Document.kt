package com.example.kianarag.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

data class Document(
    val id: String, // id
    val pointer: String,
)
//
//@Entity
//data class User(
//    @Id
//    var id: Long = 0,
//    var name: String? = null
//)