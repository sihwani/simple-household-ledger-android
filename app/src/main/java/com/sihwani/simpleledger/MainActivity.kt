package com.sihwani.simpleledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sihwani.simpleledger.data.backup.BackupFileManager
import com.sihwani.simpleledger.data.local.LedgerDatabase
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.data.storage.ReceiptImageStorage
import com.sihwani.simpleledger.ui.navigation.LedgerNavHost

class MainActivity : ComponentActivity() {
    private val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(
            transactionDao = LedgerDatabase
                .getInstance(applicationContext)
                .transactionDao()
        )
    }
    private val receiptImageStorage: ReceiptImageStorage by lazy {
        ReceiptImageStorage(applicationContext)
    }
    private val backupFileManager: BackupFileManager by lazy {
        BackupFileManager(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HannunLedgerApp(
                transactionRepository = transactionRepository,
                receiptImageStorage = receiptImageStorage,
                backupFileManager = backupFileManager
            )
        }
    }
}

@Composable
private fun HannunLedgerApp(
    transactionRepository: TransactionRepository,
    receiptImageStorage: ReceiptImageStorage,
    backupFileManager: BackupFileManager
) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LedgerNavHost(
                transactionRepository = transactionRepository,
                receiptImageStorage = receiptImageStorage,
                backupFileManager = backupFileManager
            )
        }
    }
}
