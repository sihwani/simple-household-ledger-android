package com.sihwani.simpleledger.data.pdf

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ExportedPdf(
    val fileName: String,
    val uri: Uri
)

class PdfExportManager(
    private val context: Context,
    private val generator: MonthlyLedgerPdfGenerator = MonthlyLedgerPdfGenerator(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun exportMonthlyLedger(
        reportData: MonthlyLedgerReportData
    ): ExportedPdf = withContext(ioDispatcher) {
        val exportDir = File(context.cacheDir, PDF_EXPORT_DIR_NAME).apply {
            if (!exists()) {
                mkdirs()
            }
        }
        val fileName = "hannun-ledger-${reportData.monthKey}-${timestamp()}.pdf"
        val outputFile = File(exportDir, fileName)

        generator.generate(
            reportData = reportData,
            outputFile = outputFile
        )

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            outputFile
        )

        ExportedPdf(
            fileName = fileName,
            uri = uri
        )
    }

    private fun timestamp(): String {
        return LocalDateTime.now().format(timestampFormatter)
    }

    private companion object {
        const val PDF_EXPORT_DIR_NAME = "pdf_exports"
        val timestampFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
    }
}
