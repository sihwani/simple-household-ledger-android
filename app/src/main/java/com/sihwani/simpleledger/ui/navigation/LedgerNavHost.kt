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
import com.sihwani.simpleledger.data.premium.PremiumRepository
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.data.storage.ReceiptImageStorage
import com.sihwani.simpleledger.domain.model.TransactionType
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
import com.sihwani.simpleledger.ui.settings.SettingsScreen
import com.sihwani.simpleledger.ui.settings.SettingsViewModel
import com.sihwani.simpleledger.ui.settings.SettingsViewModelFactory

@Composable
fun LedgerNavHost(
    transactionRepository: TransactionRepository,
    receiptImageStorage: ReceiptImageStorage,
    backupFileManager: BackupFileManager,
    premiumRepository: PremiumRepository,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val isPremium by premiumRepository.isPremium.collectAsState()

    NavHost(
        navController = navController,
        startDestination = LedgerRoutes.Home,
        modifier = modifier
    ) {
        composable(LedgerRoutes.Home) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(transactionRepository)
            )
            val uiState by homeViewModel.uiState.collectAsState()

            HomeScreen(
                uiState = uiState,
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
                    receiptImageStorage = receiptImageStorage,
                    type = TransactionType.INCOME
                )
            )
            val uiState by formViewModel.uiState.collectAsState()

            TransactionFormScreen(
                uiState = uiState,
                onAmountChange = formViewModel::onAmountChange,
                onTitleChange = formViewModel::onTitleChange,
                onCategoryChange = formViewModel::onCategoryChange,
                onDateChange = formViewModel::onDateChange,
                onMemoChange = formViewModel::onMemoChange,
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
                    receiptImageStorage = receiptImageStorage,
                    type = TransactionType.EXPENSE
                )
            )
            val uiState by formViewModel.uiState.collectAsState()

            TransactionFormScreen(
                uiState = uiState,
                onAmountChange = formViewModel::onAmountChange,
                onTitleChange = formViewModel::onTitleChange,
                onCategoryChange = formViewModel::onCategoryChange,
                onDateChange = formViewModel::onDateChange,
                onMemoChange = formViewModel::onMemoChange,
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
                factory = HistoryViewModelFactory(transactionRepository)
            )
            val uiState by historyViewModel.uiState.collectAsState()

            HistoryScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onTransactionClick = { transactionId ->
                    navController.navigate(LedgerRoutes.transactionDetail(transactionId))
                }
            )
        }

        composable(LedgerRoutes.Settings) {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(
                    transactionRepository = transactionRepository,
                    premiumRepository = premiumRepository
                )
            )
            val settingsUiState by settingsViewModel.uiState.collectAsState()
            val dataManagementViewModel: DataManagementViewModel = viewModel(
                factory = DataManagementViewModelFactory(
                    transactionRepository = transactionRepository,
                    backupFileManager = backupFileManager,
                    receiptImageStorage = receiptImageStorage
                )
            )
            val dataManagementUiState by dataManagementViewModel.uiState.collectAsState()

            SettingsScreen(
                uiState = settingsUiState,
                dataManagementUiState = dataManagementUiState,
                onBack = { navController.popBackStack() },
                versionName = BuildConfig.VERSION_NAME,
                versionCode = BuildConfig.VERSION_CODE,
                packageName = BuildConfig.APPLICATION_ID,
                showDebugPremiumToggle = BuildConfig.DEBUG,
                onDebugPremiumChange = settingsViewModel::setPremiumForDebug,
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
                    receiptImageStorage = receiptImageStorage,
                    type = TransactionType.EXPENSE,
                    transactionId = transactionId
                )
            )
            val uiState by formViewModel.uiState.collectAsState()

            TransactionFormScreen(
                uiState = uiState,
                onAmountChange = formViewModel::onAmountChange,
                onTitleChange = formViewModel::onTitleChange,
                onCategoryChange = formViewModel::onCategoryChange,
                onDateChange = formViewModel::onDateChange,
                onMemoChange = formViewModel::onMemoChange,
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
