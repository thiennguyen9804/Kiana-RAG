package com.example.kianarag.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdUnits
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    INDEX("index", "Index", Icons.Default.AdUnits, "Index"),
    CHAT("chat", "Chat", Icons.Default.ChatBubble, "Chat"),
}