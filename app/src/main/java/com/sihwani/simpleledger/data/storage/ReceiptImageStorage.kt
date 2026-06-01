package com.sihwani.simpleledger.data.storage

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.IOException
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReceiptImageStorage(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun copyToInternalStorage(
        sourceUriString: String,
        transactionId: String
    ): String = withContext(ioDispatcher) {
        val sourceUri = Uri.parse(sourceUriString)
        val mimeType = context.contentResolver.getType(sourceUri)

        if (mimeType != null && !mimeType.startsWith("image/")) {
            throw IllegalArgumentException("이미지 파일만 첨부할 수 있습니다.")
        }

        val knownSize = queryFileSize(sourceUri)
        if (knownSize != null && knownSize > MAX_RECEIPT_IMAGE_BYTES) {
            throw IllegalArgumentException("영수증 이미지는 8MB 이하만 첨부할 수 있습니다.")
        }

        val receiptsDir = File(context.filesDir, RECEIPT_DIR_NAME).apply {
            if (!exists()) {
                mkdirs()
            }
        }
        val extension = extensionForMimeType(mimeType)
        val targetFile = File(
            receiptsDir,
            "receipt_${transactionId}_${UUID.randomUUID()}.$extension"
        )

        var copiedBytes = 0L
        try {
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                targetFile.outputStream().use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) {
                            break
                        }

                        copiedBytes += read
                        if (copiedBytes > MAX_RECEIPT_IMAGE_BYTES) {
                            throw IllegalArgumentException("영수증 이미지는 8MB 이하만 첨부할 수 있습니다.")
                        }
                        output.write(buffer, 0, read)
                    }
                }
            } ?: throw IOException("이미지를 읽을 수 없습니다.")
        } catch (exception: Exception) {
            targetFile.delete()
            throw exception
        }

        targetFile.absolutePath
    }

    suspend fun delete(path: String?) = withContext(ioDispatcher) {
        if (path.isNullOrBlank()) {
            return@withContext
        }

        val receiptsDir = File(context.filesDir, RECEIPT_DIR_NAME).canonicalFile
        val targetFile = File(path).canonicalFile
        val isReceiptFile = targetFile.path.startsWith(receiptsDir.path) && targetFile.isFile

        if (isReceiptFile) {
            targetFile.delete()
        }
    }

    private fun queryFileSize(uri: Uri): Long? {
        return context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.SIZE),
            null,
            null,
            null
        )?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex == -1 || !cursor.moveToFirst()) {
                null
            } else {
                cursor.getLong(sizeIndex).takeIf { size -> size >= 0L }
            }
        }
    }

    private fun extensionForMimeType(mimeType: String?): String {
        return when (mimeType) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> "jpg"
        }
    }

    private companion object {
        const val RECEIPT_DIR_NAME = "receipts"
        const val MAX_RECEIPT_IMAGE_BYTES = 8L * 1024L * 1024L
    }
}
