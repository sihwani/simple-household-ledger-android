package com.sihwani.simpleledger.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sihwani.simpleledger.BuildConfig
import com.sihwani.simpleledger.data.backup.BackupFileManager
import com.sihwani.simpleledger.data.date.AppDateProvider
import com.sihwani.simpleledger.data.layout.ScreenLayoutPreferenceRepository
import com.sihwani.simpleledger.data.pdf.PdfExportManager
import com.sihwani.simpleledger.data.premium.PremiumRepository
import com.sihwani.simpleledger.data.repository.AccountRepository
import com.sihwani.simpleledger.data.repository.RecurringTransactionRepository
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.data.storage.ReceiptImageStorage
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.domain.recurring.RecurringTransactionScheduler
import com.sihwani.simpleledger.ui.account.AccountManagementScreen
import com.sihwani.simpleledger.ui.account.AccountManagementViewModel
import com.sihwani.simpleledger.ui.account.AccountManagementViewModelFactory
import com.sihwani.simpleledger.ui.detail.TransactionDetailScreen
import com.sihwani.simpleledger.ui.detail.TransactionDetailViewModel
import com.sihwani.simpleledger.ui.detail.TransactionDetailViewModelFactory
import com.sihwani.simpleledger.ui.form.TransactionFormScreen
import com.sihwani.simpleledger.ui.form.TransactionFormViewModel
import com.sihwani.simpleledger.ui.form.TransactionFormViewModelFactory
import com.sihwani.simpleledger.ui.history.DataManagementViewModel
import com.sihwani.simpleledger.ui.history.DataManagementViewModelFactory
import com.sihwani.simpleledger.ui.history.HistoryScreen
import com.sihwani.simpleledger.ui.history.HistoryViewModel
import com.sihwani.simpleledger.ui.history.HistoryViewModelFactory
import com.sihwani.simpleledger.ui.home.HomeScreen
import com.sihwani.simpleledger.ui.home.HomeViewModel
import com.sihwani.simpleledger.ui.home.HomeViewModelFactory
import com.sihwani.simpleledger.ui.recurring.RecurringTransactionScreen
import com.sihwani.simpleledger.ui.recurring.RecurringTransactionViewModel
import com.sihwani.simpleledger.ui.recurring.RecurringTransactionViewModelFactory
import com.sihwani.simpleledger.ui.settings.SettingsScreen
import com.sihwani.simpleledger.ui.settings.SettingsViewModel
import com.sihwani.simpleledger.ui.settings.SettingsViewModelFactory

@Composable
fun LedgerNavHost(
    transactionRepository: TransactionRepository,
    accountRepository: AccountRepository,
    recurringTransactionRepository: RecurringTransactionRepository,
    receiptImageStorage: ReceiptImageStorage,
    backupFileManager: BackupFileManager,
    premiumRepository: PremiumRepository,
    screenLayoutPreferenceRepository: ScreenLayoutPreferenceRepository,
    pdfExportManager: PdfExportManager,
    appDateProvider: AppDateProvider,
    recurringTransactionScheduler: RecurringTransactionScheduler,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LedgerRoutes.Home,
        modifier = modifier
    ) {
        composable(LedgerRoutes.Home) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(
                    transactionRepository = transactionRepository,
                    accountRepository = accountRepository,
                    appDateProvider = appDateProvider,
                    recurringTransactionScheduler = recurringTransactionScheduler
                )
            )
            val uiState by homeViewModel.uiState.collectAsState()
            val isPremium by premiumRepository.isPremium.collectAsState()
            val screenLayoutPreference by screenLayoutPreferenceRepository
                .screenLayoutPreference
                .collectAsState()

            HomeScreen(
                uiState = uiState,
                screenLayoutPreference = screenLayoutPreference,
                onPreviousMonth = homeViewModel::movePreviousMonth,
                onNextMonth = homeViewModel::moveNextMonth,
                onAddExpense = { navController.navigate(LedgerRoutes.ExpenseForm) },
                onAddIncome = { navController.navigate(LedgerRoutes.IncomeForm) },
                onHome = {
                    navController.navigate(LedgerRoutes.Home) {
                        launchSingleTop = true
                    }
                },
                onShowHistory = { navController.navigate(LedgerRoutes.History) },
                onOpenSettings = { navController.navigate(LedgerRoutes.Settings) },
                onOpenAccounts = { navController.navigate(LedgerRoutes.Accounts) },
                onMonthSelected = homeViewModel::moveToMonth,
                onTransactionClick = { transactionId ->
                    navController.navigate(LedgerRoutes.transactionDetail(transactionId))
                },
                isPremium = isPremium
            )
        }

        composable(LedgerRoutes.IncomeForm) {
            val formViewModel: TransactionFormViewModel = viewModel(
                factory = TransactionFormViewModelFactory(
                    transactionRepository = transactionRepository,
                    accountRepository = accountRepository,
                    recurringTransactionRepository = recurringTransactionRepository,
                    premiumRepository = premiumRepository,
                    recurringTransactionScheduler = recurringTransactionScheduler,
                    receiptImageStorage = receiptImageStorage,
                    appDateProvider = appDateProvider,
                    type = TransactionType.INCOME
                )
            )
            val uiState by formViewModel.uiState.collectAsState()
            val screenLayoutPreference by screenLayoutPreferenceRepository
                .screenLayoutPreference
                .collectAsState()

            TransactionFormScreen(
                uiState = uiState,
                screenLayoutPreference = screenLayoutPreference,
                onAmountChange = formViewModel::onAmountChange,
                onTitleChange = formViewModel::onTitleChange,
                onCategoryChange = formViewModel::onCategoryChange,
                onDateChange = formViewModel::onDateChange,
                onTransactionStatusChange = formViewModel::onTransactionStatusChange,
                onUseRecurringRuleChange = formViewModel::onUseRecurringRuleChange,
                onRecurringRepeatTypeChange = formViewModel::onRecurringRepeatTypeChange,
                onRecurringEndDateChange = formViewModel::onRecurringEndDateChange,
                onShowRecurringPremiumInfo = formViewModel::showRecurringPremiumInfo,
                onDismissRecurringPremiumInfo = formViewModel::dismissRecurringPremiumInfo,
                onMemoChange = formViewModel::onMemoChange,
                onAccountChange = formViewModel::onAccountChange,
                onReceiptImageSelected = formViewModel::onReceiptImageSelected,
                onReceiptImageRemove = formViewModel::onReceiptImageRemove,
                onSave = formViewModel::save,
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.popBackStack(
                        route = LedgerRoutes.Home,
                        inclusive = false
                    )
                }
            )
        }

        composable(LedgerRoutes.ExpenseForm) {
            val formViewModel: TransactionFormViewModel = viewModel(
                factory = TransactionFormViewModelFactory(
                    transactionRepository = transactionRepository,
                    accountRepository = accountRepository,
                    recurringTransactionRepository = recurringTransactionRepository,
                    premiumRepository = premiumRepository,
                    recurringTransactionScheduler = recurringTransactionScheduler,
                    receiptImageStorage = receiptImageStorage,
                    appDateProvider = appDateProvider,
                    type = TransactionType.EXPENSE
                )
            )
            val uiState by formViewModel.uiState.collectAsState()
            val screenLayoutPreference by screenLayoutPreferenceRepository
                .screenLayoutPreference
                .collectAsState()

            TransactionFormScreen(
                uiState = uiState,
                screenLayoutPreference = screenLayoutPreference,
                onAmountChange = formViewModel::onAmountChange,
                onTitleChange = formViewModel::onTitleChange,
                onCategoryChange = formViewModel::onCategoryChange,
                onDateChange = formViewModel::onDateChange,
                onTransactionStatusChange = formViewModel::onTransactionStatusChange,
                onUseRecurringRuleChange = formViewModel::onUseRecurringRuleChange,
                onRecurringRepeatTypeChange = formViewModel::onRecurringRepeatTypeChange,
                onRecurringEndDateChange = formViewModel::onRecurringEndDateChange,
                onShowRecurringPremiumInfo = formViewModel::showRecurringPremiumInfo,
                onDismissRecurringPremiumInfo = formViewModel::dismissRecurringPremiumInfo,
                onMemoChange = formViewModel::onMemoChange,
                onAccountChange = formViewModel::onAccountChange,
                onReceiptImageSelected = formViewModel::onReceiptImageSelected,
                onReceiptImageRemove = formViewModel::onReceiptImageRemove,
                onSave = formViewModel::save,
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.popBackStack(
                        route = LedgerRoutes.Home,
                        inclusive = false
                    )
                }
            )
        }

        composable(LedgerRoutes.History) {
            val historyViewModel: HistoryViewModel = viewModel(
                factory = HistoryViewModelFactory(
                    transactionRepository = transactionRepository,
                    premiumRepository = premiumRepository,
                    pdfExportManager = pdfExportManager,
                    appDateProvider = appDateProvider
                )
            )
            val uiState by historyViewModel.uiState.collectAsState()

            HistoryScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onTransactionClick = { transactionId ->
                    navController.navigate(LedgerRoutes.transactionDetail(transactionId))
                },
                onRequestMonthlyPdf = historyViewModel::requestMonthlyPdf,
                onRequestYearlyPdf = historyViewModel::requestYearlyPdf,
                onDismissPdfConfirmation = historyViewModel::dismissPdfConfirmation,
                onConfirmPdfGeneration = historyViewModel::confirmPdfGeneration,
                onDismissPdfPremiumDialog = historyViewModel::dismissPdfPremiumDialog,
                onPdfShareIntentHandled = historyViewModel::clearPdfShareUri,
                onPdfOpenFailed = historyViewModel::showPdfOpenFailedMessage
            )
        }

        composable(LedgerRoutes.Settings) {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(
                    transactionRepository = transactionRepository,
                    premiumRepository = premiumRepository,
                    screenLayoutPreferenceRepository = screenLayoutPreferenceRepository,
                    appDateProvider = appDateProvider,
                    recurringTransactionScheduler = recurringTransactionScheduler
                )
            )
            val settingsUiState by settingsViewModel.uiState.collectAsState()
            val dataManagementViewModel: DataManagementViewModel = viewModel(
                factory = DataManagementViewModelFactory(
                    transactionRepository = transactionRepository,
                    accountRepository = accountRepository,
                    recurringTransactionRepository = recurringTransactionRepository,
                    recurringTransactionScheduler = recurringTransactionScheduler,
                    backupFileManager = backupFileManager,
                    receiptImageStorage = receiptImageStorage
                )
            )
            val dataManagementUiState by dataManagementViewModel.uiState.collectAsState()

            SettingsScreen(
                uiState = settingsUiState,
                dataManagementUiState = dataManagementUiState,
                onBack = { navController.popBackStack() },
                onOpenAccounts = { navController.navigate(LedgerRoutes.Accounts) },
                onOpenRecurringTransactions = {
                    navController.navigate(LedgerRoutes.RecurringTransactions)
                },
                versionName = BuildConfig.VERSION_NAME,
                versionCode = BuildConfig.VERSION_CODE,
                packageName = BuildConfig.APPLICATION_ID,
                showDebugPremiumToggle = BuildConfig.DEBUG,
                showDebugDateTools = BuildConfig.DEBUG,
                onDebugPremiumChange = settingsViewModel::setPremiumForDebug,
                onDebugDateSelected = settingsViewModel::setTestDateForDebug,
                onClearDebugDate = settingsViewModel::clearTestDateForDebug,
                onRunScheduledSync = settingsViewModel::runScheduledSyncForDebug,
                onScreenLayoutPreferenceChange = settingsViewModel::setScreenLayoutPreference,
                onExportBackup = dataManagementViewModel::exportBackup,
                onImportBackup = dataManagementViewModel::importBackup,
                onMergeImport = dataManagementViewModel::mergePendingImport,
                onRequestReplaceImport = dataManagementViewModel::requestReplaceImport,
                onConfirmReplaceImport = dataManagementViewModel::confirmReplaceImport,
                onDismissImportModeDialog = dataManagementViewModel::dismissImportModeDialog,
                onDismissReplaceConfirmDialog = dataManagementViewModel::dismissReplaceConfirmDialog,
                onRequestDeleteAll = dataManagementViewModel::requestDeleteAll,
                onConfirmDeleteAll = dataManagementViewModel::confirmDeleteAll,
                onDismissDeleteAllConfirmDialog = dataManagementViewModel::dismissDeleteAllConfirmDialog
            )
        }

        composable(LedgerRoutes.Accounts) {
            val accountManagementViewModel: AccountManagementViewModel = viewModel(
                factory = AccountManagementViewModelFactory(
                    accountRepository = accountRepository,
                    transactionRepository = transactionRepository,
                    recurringTransactionRepository = recurringTransactionRepository,
                    premiumRepository = premiumRepository,
                    appDateProvider = appDateProvider
                )
            )
            val uiState by accountManagementViewModel.uiState.collectAsState()
            val screenLayoutPreference by screenLayoutPreferenceRepository
                .screenLayoutPreference
                .collectAsState()

            AccountManagementScreen(
                uiState = uiState,
                screenLayoutPreference = screenLayoutPreference,
                onBack = { navController.popBackStack() },
                onAddAccount = accountManagementViewModel::requestAddAccount,
                onEditAccount = accountManagementViewModel::requestEditAccount,
                onRequestDeactivate = accountManagementViewModel::requestDeactivate,
                onDismissDeactivate = accountManagementViewModel::dismissDeactivateDialog,
                onConfirmDeactivate = accountManagementViewModel::confirmDeactivate,
                onToggleInactiveAccounts = accountManagementViewModel::toggleInactiveAccounts,
                onRequestReactivate = accountManagementViewModel::requestReactivate,
                onDismissReactivate = accountManagementViewModel::dismissReactivateDialog,
                onConfirmReactivate = accountManagementViewModel::confirmReactivate,
                onRequestDelete = accountManagementViewModel::requestDelete,
                onDismissDelete = accountManagementViewModel::dismissDeleteDialog,
                onConfirmDelete = accountManagementViewModel::confirmDelete,
                onDismissPremiumDialog = accountManagementViewModel::dismissPremiumDialog,
                onDismissForm = accountManagementViewModel::dismissForm,
                onNameChange = accountManagementViewModel::onNameChange,
                onBankNameChange = accountManagementViewModel::onBankNameChange,
                onIdentifierChange = accountManagementViewModel::onIdentifierChange,
                onBaseBalanceChange = accountManagementViewModel::onBaseBalanceChange,
                onBaseDateChange = accountManagementViewModel::onBaseDateChange,
                onMemoChange = accountManagementViewModel::onMemoChange,
                onSaveAccount = accountManagementViewModel::saveAccount
            )
        }

        composable(LedgerRoutes.RecurringTransactions) {
            val recurringViewModel: RecurringTransactionViewModel = viewModel(
                factory = RecurringTransactionViewModelFactory(
                    recurringTransactionRepository = recurringTransactionRepository,
                    accountRepository = accountRepository,
                    premiumRepository = premiumRepository,
                    scheduler = recurringTransactionScheduler,
                    appDateProvider = appDateProvider
                )
            )
            val uiState by recurringViewModel.uiState.collectAsState()
            val screenLayoutPreference by screenLayoutPreferenceRepository
                .screenLayoutPreference
                .collectAsState()

            RecurringTransactionScreen(
                uiState = uiState,
                screenLayoutPreference = screenLayoutPreference,
                onBack = { navController.popBackStack() },
                onAddRule = recurringViewModel::requestAddRule,
                onEditRule = recurringViewModel::requestEditRule,
                onRequestDeactivate = recurringViewModel::requestDeactivate,
                onDismissDeactivate = recurringViewModel::dismissDeactivateDialog,
                onConfirmDeactivate = recurringViewModel::confirmDeactivate,
                onToggleInactiveRules = recurringViewModel::toggleInactiveRules,
                onRequestReactivate = recurringViewModel::requestReactivate,
                onDismissReactivate = recurringViewModel::dismissReactivateDialog,
                onConfirmReactivate = recurringViewModel::confirmReactivate,
                onRequestDelete = recurringViewModel::requestDelete,
                onDismissDelete = recurringViewModel::dismissDeleteDialog,
                onConfirmDelete = recurringViewModel::confirmDelete,
                onDismissPremiumDialog = recurringViewModel::dismissPremiumDialog,
                onDismissForm = recurringViewModel::dismissForm,
                onTypeChange = recurringViewModel::onTypeChange,
                onAmountChange = recurringViewModel::onAmountChange,
                onTitleChange = recurringViewModel::onTitleChange,
                onCategoryChange = recurringViewModel::onCategoryChange,
                onAccountChange = recurringViewModel::onAccountChange,
                onRepeatTypeChange = recurringViewModel::onRepeatTypeChange,
                onStartDateChange = recurringViewModel::onStartDateChange,
                onEndDateChange = recurringViewModel::onEndDateChange,
                onMemoChange = recurringViewModel::onMemoChange,
                onSaveRule = recurringViewModel::saveRule
            )
        }

        composable(
            route = LedgerRoutes.TransactionDetail,
            arguments = listOf(
                navArgument(LedgerRoutes.TransactionIdArg) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments
                ?.getString(LedgerRoutes.TransactionIdArg)
                .orEmpty()
            val detailViewModel: TransactionDetailViewModel = viewModel(
                factory = TransactionDetailViewModelFactory(
                    transactionRepository = transactionRepository,
                    accountRepository = accountRepository,
                    recurringTransactionRepository = recurringTransactionRepository,
                    receiptImageStorage = receiptImageStorage,
                    transactionId = transactionId
                )
            )
            val uiState by detailViewModel.uiState.collectAsState()

            LaunchedEffect(uiState.deleteCompleted) {
                if (uiState.deleteCompleted) {
                    navController.popBackStack(
                        route = LedgerRoutes.Home,
                        inclusive = false
                    )
                }
            }

            TransactionDetailScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(LedgerRoutes.transactionEdit(transactionId)) },
                onDeleteClick = detailViewModel::requestDelete,
                onDismissDelete = detailViewModel::dismissDeleteDialog,
                onConfirmDelete = detailViewModel::deleteTransaction
            )
        }

        composable(
            route = LedgerRoutes.TransactionEdit,
            arguments = listOf(
                navArgument(LedgerRoutes.TransactionIdArg) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments
                ?.getString(LedgerRoutes.TransactionIdArg)
                .orEmpty()
            val formViewModel: TransactionFormViewModel = viewModel(
                factory = TransactionFormViewModelFactory(
                    transactionRepository = transactionRepository,
                    accountRepository = accountRepository,
                    recurringTransactionRepository = recurringTransactionRepository,
                    premiumRepository = premiumRepository,
                    recurringTransactionScheduler = recurringTransactionScheduler,
                    receiptImageStorage = receiptImageStorage,
                    appDateProvider = appDateProvider,
                    type = TransactionType.EXPENSE,
                    transactionId = transactionId
                )
            )
            val uiState by formViewModel.uiState.collectAsState()
            val screenLayoutPreference by screenLayoutPreferenceRepository
                .screenLayoutPreference
                .collectAsState()

            TransactionFormScreen(
                uiState = uiState,
                screenLayoutPreference = screenLayoutPreference,
                onAmountChange = formViewModel::onAmountChange,
                onTitleChange = formViewModel::onTitleChange,
                onCategoryChange = formViewModel::onCategoryChange,
                onDateChange = formViewModel::onDateChange,
                onTransactionStatusChange = formViewModel::onTransactionStatusChange,
                onUseRecurringRuleChange = formViewModel::onUseRecurringRuleChange,
                onRecurringRepeatTypeChange = formViewModel::onRecurringRepeatTypeChange,
                onRecurringEndDateChange = formViewModel::onRecurringEndDateChange,
                onShowRecurringPremiumInfo = formViewModel::showRecurringPremiumInfo,
                onDismissRecurringPremiumInfo = formViewModel::dismissRecurringPremiumInfo,
                onMemoChange = formViewModel::onMemoChange,
                onAccountChange = formViewModel::onAccountChange,
                onReceiptImageSelected = formViewModel::onReceiptImageSelected,
                onReceiptImageRemove = formViewModel::onReceiptImageRemove,
                onSave = formViewModel::save,
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.popBackStack()
                }
            )
        }
    }
}
