package com.example.kianarag

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.kianarag.ui.theme.KianaRAGTheme
import com.example.kianarag.util.matrix.MatrixAddition

val matrix = MatrixAddition()
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val c = matrix.cal(
            floatArrayOf(1.0f, 2.0f, 3.0f),
            floatArrayOf(4.0f, 5.0f, 6.0f)
        )
        val string = c.contentToString() // [5, 7, 9]
        enableEdgeToEdge()
        setContent {
            KianaRAGTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = string,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KianaRAGTheme {
        Greeting("Android")
    }
}