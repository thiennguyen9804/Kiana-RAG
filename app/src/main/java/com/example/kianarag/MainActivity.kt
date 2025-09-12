package com.example.kianarag

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.kianarag.rag.KianaRAG
import com.example.kianarag.rag.embedding.EmbeddingModel
import com.example.kianarag.presentation.theme.KianaRAGTheme
import com.example.kianarag.util.toArrayRealVector
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    val kianaRag = KianaRAG(this)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Xử lý kết quả
        val readGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
        val writeGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false

        if (readGranted && writeGranted) {
            Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickPdfLauncher = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { results ->
        val fileNames = mutableListOf<String>()
        results.forEachIndexed { index, result ->
            val selectedFileUri = result
            selectedFileUri.let { uri ->
                val fileName = "pdf_file_${index}.pdf"
                fileNames.add(fileName)
                copyFileToAppDirectory(uri, fileName)
            }
        }

        kianaRag.index(fileNames)
    }

    private fun copyFileToAppDirectory(pdfUri: Uri, destinationFileName: String) {
        val inputStream = contentResolver.openInputStream(pdfUri)
        val outputFile = File(filesDir, destinationFileName)
        val outputStream = FileOutputStream(outputFile)
        inputStream?.copyTo(outputStream)
    }

    private fun checkAndRequestStoragePermissions() {
        val permissionsToRequest = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestStoragePermissions()
        enableEdgeToEdge()
        setContent {
            KianaRAGTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                        onClick = {
                            val mimetypes = arrayOf("application/pdf")
                            pickPdfLauncher.launch(mimetypes)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier, onClick: () -> Unit) {
    Box(modifier = modifier
        .fillMaxSize()
    ) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = onClick
        ) {
            Text("Rag Index")
        }
    }
}
