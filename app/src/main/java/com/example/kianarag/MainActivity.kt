package com.example.kianarag

import android.content.Context
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
import com.example.kianarag.rag.embedding.EmbeddingModel
import com.example.kianarag.rag.embedding.Tokenizer
import com.example.kianarag.ui.theme.KianaRAGTheme
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tokenizer = Tokenizer(
            context = this
        )

        val embedder = EmbeddingModel(
            tokenizer = tokenizer,
            absoluteModelPath = assetFilePath(this, "gte-small.pt")
        )
        val string = "Kiana" // [5, 7, 9]
        val embeddingString = embedder.embed(string)
        enableEdgeToEdge()
        setContent {
            KianaRAGTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = embeddingString.toContentString(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun assetFilePath(context: Context, assetName: String): String? {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }
        context.assets.open(assetName).use { `is` ->
            FileOutputStream(file).use { os ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (`is`.read(buffer).also { read = it } != -1) {
                    os.write(buffer, 0, read)
                }
                os.flush()
            }
            return file.absolutePath
        }
    }
}

private fun FloatArray?.toContentString(): String {
    return this?.joinToString(separator = ", ") ?: "null"
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