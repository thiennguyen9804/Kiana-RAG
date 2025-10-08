package com.example.kianarag.util

import android.app.Application
import android.util.Log
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import java.io.File

class PdfLoader(
    private val application: Application
) {
    var durationPerPage: Long = 0
    // content + file path
    fun load(localFileName: String): Pair<String, String> {
        val content = StringBuilder()
        val filePath = File(application.filesDir, localFileName).absolutePath
        val reader = PdfReader(filePath)
        val n = reader.numberOfPages
        val start = System.nanoTime()
        for (i in 1..n) {
            val startPerPage = System.nanoTime()
            content.append(PdfTextExtractor.getTextFromPage(reader, i).trim() + "\n")
            durationPerPage = (System.nanoTime() - startPerPage) / 1_000_000 // ms
            Log.d(TAG, "Duration for PDF's page $i: $durationPerPage ms")

        }
        val duration = (System.nanoTime() - start) / 1_000_000
        Log.d(TAG, "Duration for a PDF: $duration ms")


        return content.toString() to filePath

    }

    companion object {
        private const val TAG = "PdfLoader"
    }

    fun load(localFileNames: List<String>): List<Pair<String, String>> {
        return localFileNames.map { load(it) }

    }
}