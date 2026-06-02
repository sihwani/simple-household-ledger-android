package com.sihwani.simpleledger.data.backup

import android.content.Context
import android.net.Uri
import com.sihwani.simpleledger.domain.model.Account
import com.sihwani.simpleledger.domain.model.RecurringSkippedOccurrence
import com.sihwani.simpleledger.domain.model.RecurringTransaction
import com.sihwani.simpleledger.domain.model.Transaction
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackupFileManager(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun writeBackup(
        uriString: String,
        transactions: List<Transaction>,
        accounts: List<Account>,
        recurringTransactions: List<RecurringTransaction> = emptyList(),
        recurringSkippedOccurrences: List<RecurringSkippedOccurrence> = emptyList()
    ) = withContext(ioDispatcher) {
        val uri = Uri.parse(uriString)
        val jsonText = BackupJson.encode(
            transactions = transactions,
            accounts = accounts,
            recurringTransactions = recurringTransactions,
            recurringSkippedOccurrences = recurringSkippedOccurrences
        )

        context.contentResolver.openOutputStream(uri, "wt")?.use { outputStream ->
            outputStream.write(jsonText.toByteArray(Charsets.UTF_8))
        } ?: throw IOException("백업 파일을 저장할 수 없습니다.")
    }

    suspend fun readBackup(uriString: String): BackupData = withContext(ioDispatcher) {
        val uri = Uri.parse(uriString)
        val jsonText = context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.bufferedReader(Charsets.UTF_8).readText()
        } ?: throw IOException("백업 파일을 읽을 수 없습니다.")

        BackupJson.decode(jsonText)
    }
}
