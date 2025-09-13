package com.example.kianarag.presentation.index_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun IndexScreen(
    modifier: Modifier = Modifier,
    onIndexClick: () -> Unit,
    onRetrieveClick: () -> Unit,
    retrievedText: String = ""
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onIndexClick
        ) {
            Text("Rag Index")
        }
        Button(
            onClick = onRetrieveClick
        ) {
            Text("Rag Retrieve")
        }
        Text(retrievedText)
    }
}