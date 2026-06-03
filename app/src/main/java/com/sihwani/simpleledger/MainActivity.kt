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
import com.sihwani.simpleledger.data.date.AppDateProvider
import com.sihwani.simpleledger.data.layout.ScreenLayoutPreferenceRepository
import com.sihwani.simpleledger.data.local.LedgerDatabase
import com.sihwani.simpleledger.data.pdf.PdfExportManager
import com.sihwani.simpleledger.data.premium.PremiumRepository
import com.sihwani.simpleledger.data.repository.AccountRepository
import com.sihwani.simpleledger.data.repository.DataManagementRepository
import com.sihwani.simpleledger.data.repository.RecurringTransactionRepository
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.data.storage.ReceiptImageStorage
import com.sihwani.simpleledger.domain.recurring.RecurringTransactionScheduler
import com.sihwani.simpleledger.ui.navigation.LedgerNavHost
import com.sihwani.simpleledger.ui.theme.LedgerTheme

class MainActivity : ComponentActivity() {
    private val appDateProvider: AppDateProvider by lazy {
        AppDateProvider(applicationContext)
    }
    private val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(
            transactionDao = LedgerDatabase
                .getInstance(applicationContext)
                .transactionDao()
        )
    }
    private val accountRepository: AccountRepository by lazy {
        AccountRepository(
            accountDao = LedgerDatabase
                .getInstance(applicationContext)
                .accountDao()
        )
    }
    private val recurringTransactionRepository: RecurringTransactionRepository by lazy {
        RecurringTransactionRepository(
            recurringTransactionDao = LedgerDatabase
                .getInstance(applicationContext)
                .recurringTransactionDao()
        )
    }
    private val dataManagementRepository: DataManagementRepository by lazy {
        DataManagementRepository(
            database = LedgerDatabase.getInstance(applicationContext),
            appDateProvider = appDateProvider
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
    private val screenLayoutPreferenceRepository: ScreenLayoutPreferenceRepository by lazy {
        ScreenLayoutPreferenceRepository(applicationContext)
    }
    private val pdfExportManager: PdfExportManager by lazy {
        PdfExportManager(applicationContext)
    }
    private val recurringTransactionScheduler: RecurringTransactionScheduler by lazy {
        RecurringTransactionScheduler(
            recurringTransactionRepository = recurringTransactionRepository,
            transactionRepository = transactionRepository,
            accountRepository = accountRepository,
            appDateProvider = appDateProvider
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        MobileAdsInitializer.initialize(applicationContext)
        setContent {
            HannunLedgerApp(
                transactionRepository = transactionRepository,
                accountRepository = accountRepository,
                recurringTransactionRepository = recurringTransactionRepository,
                dataManagementRepository = dataManagementRepository,
                receiptImageStorage = receiptImageStorage,
                backupFileManager = backupFileManager,
                premiumRepository = premiumRepository,
                screenLayoutPreferenceRepository = screenLayoutPreferenceRepository,
                pdfExportManager = pdfExportManager,
                appDateProvider = appDateProvider,
                recurringTransactionScheduler = recurringTransactionScheduler
            )
        }
    }
}

@Composable
private fun HannunLedgerApp(
    transactionRepository: TransactionRepository,
    accountRepository: AccountRepository,
    recurringTransactionRepository: RecurringTransactionRepository,
    dataManagementRepository: DataManagementRepository,
    receiptImageStorage: ReceiptImageStorage,
    backupFileManager: BackupFileManager,
    premiumRepository: PremiumRepository,
    screenLayoutPreferenceRepository: ScreenLayoutPreferenceRepository,
    pdfExportManager: PdfExportManager,
    appDateProvider: AppDateProvider,
    recurringTransactionScheduler: RecurringTransactionScheduler
) {
    LedgerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LedgerNavHost(
                transactionRepository = transactionRepository,
                accountRepository = accountRepository,
                recurringTransactionRepository = recurringTransactionRepository,
                dataManagementRepository = dataManagementRepository,
                receiptImageStorage = receiptImageStorage,
                backupFileManager = backupFileManager,
                premiumRepository = premiumRepository,
                screenLayoutPreferenceRepository = screenLayoutPreferenceRepository,
                pdfExportManager = pdfExportManager,
                appDateProvider = appDateProvider,
                recurringTransactionScheduler = recurringTransactionScheduler
            )
        }
    }
}
