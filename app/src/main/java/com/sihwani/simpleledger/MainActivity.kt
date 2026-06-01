package com.sihwani.simpleledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sihwani.simpleledger.data.ads.MobileAdsInitializer
import com.sihwani.simpleledger.data.backup.BackupFileManager
import com.sihwani.simpleledger.data.local.LedgerDatabase
import com.sihwani.simpleledger.data.pdf.PdfExportManager
import com.sihwani.simpleledger.data.premium.PremiumRepository
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.data.storage.ReceiptImageStorage
import com.sihwani.simpleledger.ui.navigation.LedgerNavHost
import com.sihwani.simpleledger.ui.theme.LedgerTheme

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
    private val premiumRepository: PremiumRepository by lazy {
        PremiumRepository(applicationContext)
    }
    private val pdfExportManager: PdfExportManager by lazy {
        PdfExportManager(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        MobileAdsInitializer.initialize(applicationContext)
        setContent {
            HannunLedgerApp(
                transactionRepository = transactionRepository,
                receiptImageStorage = receiptImageStorage,
                backupFileManager = backupFileManager,
                premiumRepository = premiumRepository,
                pdfExportManager = pdfExportManager
            )
        }
    }
}

@Composable
private fun HannunLedgerApp(
    transactionRepository: TransactionRepository,
    receiptImageStorage: ReceiptImageStorage,
    backupFileManager: BackupFileManager,
    premiumRepository: PremiumRepository,
    pdfExportManager: PdfExportManager
) {
    LedgerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LedgerNavHost(
                transactionRepository = transactionRepository,
                receiptImageStorage = receiptImageStorage,
                backupFileManager = backupFileManager,
                premiumRepository = premiumRepository,
                pdfExportManager = pdfExportManager
            )
        }
    }
}
