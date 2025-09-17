package com.example.kianarag

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kianarag.data.MetadataManager
import com.example.kianarag.presentation.Destination
import com.example.kianarag.presentation.chat_screen.ChatScreen
import com.example.kianarag.presentation.index_screen.IndexScreen
import com.example.kianarag.rag.KianaRAG
import com.example.kianarag.presentation.theme.KianaRAGTheme
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    lateinit var kianaRag: KianaRAG
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val readGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
        val writeGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false

        if (readGranted && writeGranted) {
            Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
        }
    }
    val query = "What is Mumbai's former name?"

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
        kianaRag = KianaRAG(this)
        checkAndRequestStoragePermissions()
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val startDestination = Destination.INDEX
            var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

            KianaRAGTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                            Destination.entries.forEachIndexed { index, destination ->
                                NavigationBarItem(
                                    selected = selectedDestination == index,
                                    onClick = {
                                        navController.navigate(route = destination.route)
                                        selectedDestination = index
                                    },
                                    icon = {
                                        Icon(
                                            destination.icon,
                                            contentDescription = destination.contentDescription
                                        )
                                    },
                                    label = { Text(destination.label) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = startDestination.route
                    ) {
                        composable(Destination.INDEX.route) {
                            var retrievedText by remember { mutableStateOf("") }
                            IndexScreen(
                                retrievedText = retrievedText,
                                onIndexClick = {
                                    val mimetypes = arrayOf("application/pdf")
                                    pickPdfLauncher.launch(mimetypes)
                                },
                                onRetrieveClick = {
                                    val resultList = kianaRag.retrieve(query, k = 1)
                                    Log.d("Rag#Retrieve", resultList.size.toString())
                                    resultList.forEachIndexed { index, (docId, _) ->
                                        val metadata = MetadataManager.getById(docId)
                                        Log.d("Rag#Retrieve", "Chunk #${index}: ${metadata.chunkContent}")
                                    }
                                    retrievedText = "Testing"
                                }
                            )
                        }

                        composable(Destination.CHAT.route) {
                            ChatScreen()
                        }
                    }

                }
            }
        }
    }
}

