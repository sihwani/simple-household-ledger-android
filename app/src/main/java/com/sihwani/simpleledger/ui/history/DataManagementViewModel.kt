package com.sihwani.simpleledger.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sihwani.simpleledger.data.backup.BackupFileManager
import com.sihwani.simpleledger.data.repository.AccountRepository
import com.sihwani.simpleledger.data.repository.RecurringTransactionRepository
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.data.storage.ReceiptImageStorage
import com.sihwani.simpleledger.domain.model.Account
import com.sihwani.simpleledger.domain.model.RecurringSkippedOccurrence
import com.sihwani.simpleledger.domain.model.RecurringTransaction
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.recurring.RecurringTransactionScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DataManagementUiState(
    val isBusy: Boolean = false,
    val message: String? = null,
    val showImportModeDialog: Boolean = false,
    val showReplaceConfirmDialog: Boolean = false,
    val showDeleteAllConfirmDialog: Boolean = false,
    val pendingImportCount: Int = 0,
    val pendingImportAccountCount: Int = 0,
    val pendingImportRecurringCount: Int = 0
)

class DataManagementViewModel(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val recurringTransactionRepository: RecurringTransactionRepository,
    private val recurringTransactionScheduler: RecurringTransactionScheduler,
    private val backupFileManager: BackupFileManager,
    private val receiptImageStorage: ReceiptImageStorage
) : ViewModel() {
    private val _uiState = MutableStateFlow(DataManagementUiState())
    val uiState: StateFlow<DataManagementUiState> = _uiState

    private var pendingImportTransactions: List<Transaction> = emptyList()
    private var pendingImportAccounts: List<Account> = emptyList()
    private var pendingImportRecurringTransactions: List<RecurringTransaction> = emptyList()
    private var pendingImportSkippedOccurrences: List<RecurringSkippedOccurrence> = emptyList()

    fun exportBackup(uriString: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isBusy = true,
                    message = null
                )
            }

            runCatching {
                val transactions = transactionRepository.getAllTransactions()
                val accounts = accountRepository.getAllAccounts()
                val recurringTransactions = recurringTransactionRepository.getAll()
                val skippedOccurrences = recurringTransactionRepository.getSkippedOccurrences()
                backupFileManager.writeBackup(
                    uriString = uriString,
                    transactions = transactions.map { transaction ->
                        transaction.copy(receiptImagePath = null)
                    },
                    accounts = accounts,
                    recurringTransactions = recurringTransactions,
                    recurringSkippedOccurrences = skippedOccurrences
                )
                Triple(transactions.size, accounts.size, recurringTransactions.size)
            }.onSuccess { count ->
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        message = "거래 ${count.first}건, 계좌/지갑 ${count.second}건, 반복 거래 ${count.third}건을 내보냈습니다. 영수증 사진은 포함되지 않았습니다."
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        message = throwable.message ?: "데이터를 내보내지 못했습니다."
                    )
                }
            }
        }
    }

    fun importBackup(uriString: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isBusy = true,
                    message = null,
                    showImportModeDialog = false,
                    showReplaceConfirmDialog = false
                )
            }

            runCatching {
                backupFileManager.readBackup(uriString)
            }.onSuccess { backupData ->
                pendingImportTransactions = backupData.transactions
                    .map { transaction -> transaction.copy(receiptImagePath = null) }
                pendingImportAccounts = backupData.accounts
                pendingImportRecurringTransactions = backupData.recurringTransactions
                pendingImportSkippedOccurrences = backupData.recurringSkippedOccurrences
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        showImportModeDialog = true,
                        pendingImportCount = pendingImportTransactions.size,
                        pendingImportAccountCount = pendingImportAccounts.size,
                        pendingImportRecurringCount = pendingImportRecurringTransactions.size
                    )
                }
            }.onFailure { throwable ->
                pendingImportTransactions = emptyList()
                pendingImportAccounts = emptyList()
                pendingImportRecurringTransactions = emptyList()
                pendingImportSkippedOccurrences = emptyList()
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        pendingImportCount = 0,
                        pendingImportAccountCount = 0,
                        pendingImportRecurringCount = 0,
                        message = throwable.message ?: "백업 파일을 가져오지 못했습니다."
                    )
                }
            }
        }
    }

    fun mergePendingImport() {
        val importTransactions = pendingImportTransactions
        val importAccounts = pendingImportAccounts
        val importRecurringTransactions = pendingImportRecurringTransactions
        val importSkippedOccurrences = pendingImportSkippedOccurrences
        if (importTransactions.isEmpty() && importAccounts.isEmpty() && importRecurringTransactions.isEmpty()) {
            dismissImportModeDialog()
            showMessage("가져올 데이터가 없습니다.")
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isBusy = true,
                    showImportModeDialog = false,
                    message = null
                )
            }

            runCatching {
                val existingIds = transactionRepository.getAllTransactions()
                    .map { transaction -> transaction.id }
                    .toSet()
                val existingAccountIds = accountRepository.getAllAccounts()
                    .map { account -> account.id }
                    .toSet()
                val existingRecurringIds = recurringTransactionRepository.getAll()
                    .map { rule -> rule.id }
                    .toSet()
                val importAccountIds = importAccounts.map { account -> account.id }.toSet()
                val validAccountIds = existingAccountIds + importAccountIds
                val sanitizedTransactions = sanitizeImportedTransactions(
                    transactions = importTransactions,
                    validAccountIds = validAccountIds
                )
                val sanitizedRecurringTransactions = sanitizeImportedRecurringTransactions(
                    rules = importRecurringTransactions,
                    validAccountIds = validAccountIds
                )
                val newTransactions = sanitizedTransactions
                    .filterNot { transaction -> transaction.id in existingIds }
                val newAccounts = importAccounts
                    .filterNot { account -> account.id in existingAccountIds }
                val newRecurringTransactions = sanitizedRecurringTransactions
                    .filterNot { rule -> rule.id in existingRecurringIds }

                if (newAccounts.isNotEmpty()) {
                    accountRepository.upsertAll(newAccounts)
                }
                if (newRecurringTransactions.isNotEmpty()) {
                    recurringTransactionRepository.upsertAll(newRecurringTransactions)
                }
                if (importSkippedOccurrences.isNotEmpty()) {
                    recurringTransactionRepository.upsertSkippedOccurrences(importSkippedOccurrences)
                }
                if (newTransactions.isNotEmpty()) {
                    transactionRepository.upsertAll(newTransactions)
                }
                recurringTransactionScheduler.sync()

                Triple(newTransactions.size, newAccounts.size, newRecurringTransactions.size)
            }.onSuccess { mergedCount ->
                clearPendingImport()
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        message = "백업 데이터 병합 완료: 새 거래 ${mergedCount.first}건, 새 계좌/지갑 ${mergedCount.second}건, 새 반복 거래 ${mergedCount.third}건을 추가했습니다."
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        message = throwable.message ?: "백업 데이터를 병합하지 못했습니다."
                    )
                }
            }
        }
    }

    fun requestReplaceImport() {
        _uiState.update {
            it.copy(
                showImportModeDialog = false,
                showReplaceConfirmDialog = true
            )
        }
    }

    fun confirmReplaceImport() {
        val importTransactions = pendingImportTransactions
        val importAccounts = pendingImportAccounts
        val importRecurringTransactions = pendingImportRecurringTransactions
        val importSkippedOccurrences = pendingImportSkippedOccurrences

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isBusy = true,
                    showReplaceConfirmDialog = false,
                    message = null
                )
            }

            runCatching {
                val existingTransactions = transactionRepository.getAllTransactions()
                val importAccountIds = importAccounts.map { account -> account.id }.toSet()
                val sanitizedTransactions = sanitizeImportedTransactions(
                    transactions = importTransactions,
                    validAccountIds = importAccountIds
                )
                val sanitizedRecurringTransactions = sanitizeImportedRecurringTransactions(
                    rules = importRecurringTransactions,
                    validAccountIds = importAccountIds
                )
                recurringTransactionRepository.replaceAll(
                    rules = sanitizedRecurringTransactions,
                    skippedOccurrences = importSkippedOccurrences
                )
                accountRepository.replaceAll(importAccounts)
                transactionRepository.replaceAll(sanitizedTransactions)
                recurringTransactionScheduler.sync()
                deleteReceiptImages(existingTransactions)
                Triple(sanitizedTransactions.size, importAccounts.size, sanitizedRecurringTransactions.size)
            }.onSuccess { importedCount ->
                clearPendingImport()
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        message = "백업 데이터로 교체 완료: 거래 ${importedCount.first}건, 계좌/지갑 ${importedCount.second}건, 반복 거래 ${importedCount.third}건을 복원했습니다."
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        message = throwable.message ?: "백업 데이터로 교체하지 못했습니다."
                    )
                }
            }
        }
    }

    fun requestDeleteAll() {
        _uiState.update {
            it.copy(
                showDeleteAllConfirmDialog = true,
                message = null
            )
        }
    }

    fun confirmDeleteAll() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isBusy = true,
                    showDeleteAllConfirmDialog = false,
                    message = null
                )
            }

            runCatching {
                val existingTransactions = transactionRepository.getAllTransactions()
                transactionRepository.deleteAll()
                recurringTransactionRepository.deleteAll()
                accountRepository.deleteAll()
                deleteReceiptImages(existingTransactions)
                existingTransactions.size
            }.onSuccess { deletedCount ->
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        message = "전체 거래 내역 ${deletedCount}건과 계좌/지갑, 반복 거래 데이터를 삭제했습니다."
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        message = throwable.message ?: "전체 데이터를 삭제하지 못했습니다."
                    )
                }
            }
        }
    }

    fun dismissImportModeDialog() {
        _uiState.update {
            it.copy(showImportModeDialog = false)
        }
    }

    fun dismissReplaceConfirmDialog() {
        _uiState.update {
            it.copy(showReplaceConfirmDialog = false)
        }
    }

    fun dismissDeleteAllConfirmDialog() {
        _uiState.update {
            it.copy(showDeleteAllConfirmDialog = false)
        }
    }

    private suspend fun deleteReceiptImages(transactions: List<Transaction>) {
        transactions.forEach { transaction ->
            runCatching {
                receiptImageStorage.delete(transaction.receiptImagePath)
            }
        }
    }

    private fun clearPendingImport() {
        pendingImportTransactions = emptyList()
        pendingImportAccounts = emptyList()
        pendingImportRecurringTransactions = emptyList()
        pendingImportSkippedOccurrences = emptyList()
        _uiState.update {
            it.copy(
                pendingImportCount = 0,
                pendingImportAccountCount = 0,
                pendingImportRecurringCount = 0,
                showImportModeDialog = false,
                showReplaceConfirmDialog = false
            )
        }
    }

    private fun showMessage(message: String) {
        _uiState.update {
            it.copy(message = message)
        }
    }

    private fun sanitizeImportedTransactions(
        transactions: List<Transaction>,
        validAccountIds: Set<String>
    ): List<Transaction> {
        return transactions.map { transaction ->
            val accountId = transaction.accountId
            val hasSnapshot = !transaction.accountSnapshotName.isNullOrBlank() ||
                !transaction.accountSnapshotBankName.isNullOrBlank() ||
                !transaction.accountSnapshotIdentifier.isNullOrBlank()

            if (accountId == null || accountId in validAccountIds || hasSnapshot) {
                transaction
            } else {
                transaction.copy(accountId = null)
            }
        }
    }

    private fun sanitizeImportedRecurringTransactions(
        rules: List<RecurringTransaction>,
        validAccountIds: Set<String>
    ): List<RecurringTransaction> {
        return rules.map { rule ->
            val accountId = rule.accountId
            if (accountId == null || accountId in validAccountIds) {
                rule
            } else {
                rule.copy(accountId = null)
            }
        }
    }
}

class DataManagementViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val recurringTransactionRepository: RecurringTransactionRepository,
    private val recurringTransactionScheduler: RecurringTransactionScheduler,
    private val backupFileManager: BackupFileManager,
    private val receiptImageStorage: ReceiptImageStorage
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DataManagementViewModel::class.java)) {
            return DataManagementViewModel(
                transactionRepository = transactionRepository,
                accountRepository = accountRepository,
                recurringTransactionRepository = recurringTransactionRepository,
                recurringTransactionScheduler = recurringTransactionScheduler,
                backupFileManager = backupFileManager,
                receiptImageStorage = receiptImageStorage
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
