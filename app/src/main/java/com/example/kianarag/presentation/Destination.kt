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
    INDEX("songs", "Songs", Icons.Default.AdUnits, "Index"),
    CHAT("album", "Album", Icons.Default.ChatBubble, "Chat"),
}