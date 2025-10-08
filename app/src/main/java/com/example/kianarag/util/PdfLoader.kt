package com.example.kianarag.util

import android.app.Application
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import java.io.File

class PdfLoader(
    private val application: Application
) {
    // content + file path
    fun load(localFileName: String): Pair<String, String> {
        var content = ""
        val filePath = File(application.filesDir, localFileName).absolutePath
        val reader = PdfReader(filePath)
        val n = reader.numberOfPages
        for (i in 1..n) {
            content += PdfTextExtractor.getTextFromPage(reader, i).trim() + "\n"
        }

        return content to filePath
    }

    fun load(localFileNames: List<String>): List<Pair<String, String>> {
        return localFileNames.map { load(it) }

    }
}