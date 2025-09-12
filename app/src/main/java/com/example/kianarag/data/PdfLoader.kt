package com.example.kianarag.data

import android.content.Context
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import java.io.File

class PdfLoader(
    private val context: Context
) {
    // content + file path
    fun load(localFileName: String): Pair<String, String> {
        var content = ""
        val filePath = File(context.filesDir, localFileName).absolutePath
        val reader = PdfReader(filePath)
        val n = reader.numberOfPages
        for (i in 1..n) {
            content += PdfTextExtractor.getTextFromPage(reader, i).trim() + "\n"
        }

        return content to filePath
    }

    fun batchLoad(localFileNames: List<String>): List<Pair<String, String>> {
        return localFileNames.map { load(it) }

    }
}