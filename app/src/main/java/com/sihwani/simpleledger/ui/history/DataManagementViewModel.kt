package com.sihwani.simpleledger.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sihwani.simpleledger.data.backup.BackupFileManager
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.data.storage.ReceiptImageStorage
import com.sihwani.simpleledger.domain.model.Transaction
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
    val pendingImportCount: Int = 0
)

class DataManagementViewModel(
    private val transactionRepository: TransactionRepository,
    private val backupFileManager: BackupFileManager,
    private val receiptImageStorage: ReceiptImageStorage
) : ViewModel() {
    private val _uiState = MutableStateFlow(DataManagementUiState())
    val uiState: StateFlow<DataManagementUiState> = _uiState

    private var pendingImportTransactions: List<Transaction> = emptyList()

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
                backupFileManager.writeBackup(
                    uriString = uriString,
                    transactions = transactions.map { transaction ->
                        transaction.copy(receiptImagePath = null)
                    }
                )
                transactions.size
            }.onSuccess { count ->
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        message = "거래 내역 ${count}건을 내보냈습니다. 영수증 사진은 포함되지 않았습니다."
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
                    .map { transaction -> transaction.copy(receiptImagePath = null) }
            }.onSuccess { transactions ->
                pendingImportTransactions = transactions
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        showImportModeDialog = true,
                        pendingImportCount = transactions.size
                    )
                }
            }.onFailure { throwable ->
                pendingImportTransactions = emptyList()
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        pendingImportCount = 0,
                        message = throwable.message ?: "백업 파일을 가져오지 못했습니다."
                    )
                }
            }
        }
    }

    fun mergePendingImport() {
        val importTransactions = pendingImportTransactions
        if (importTransactions.isEmpty()) {
            dismissImportModeDialog()
            showMessage("가져올 거래 내역이 없습니다.")
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
                val newTransactions = importTransactions
                    .filterNot { transaction -> transaction.id in existingIds }

                if (newTransactions.isNotEmpty()) {
                    transactionRepository.upsertAll(newTransactions)
                }

                newTransactions.size
            }.onSuccess { mergedCount ->
                clearPendingImport()
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        message = "백업 데이터 병합 완료: 새 거래 ${mergedCount}건을 추가했습니다."
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
                transactionRepository.replaceAll(importTransactions)
                deleteReceiptImages(existingTransactions)
                importTransactions.size
            }.onSuccess { importedCount ->
                clearPendingImport()
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        message = "백업 데이터로 교체 완료: 거래 ${importedCount}건을 복원했습니다."
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
                deleteReceiptImages(existingTransactions)
                existingTransactions.size
            }.onSuccess { deletedCount ->
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        message = "전체 거래 내역 ${deletedCount}건을 삭제했습니다."
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
        _uiState.update {
            it.copy(
                pendingImportCount = 0,
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
}

class DataManagementViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val backupFileManager: BackupFileManager,
    private val receiptImageStorage: ReceiptImageStorage
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DataManagementViewModel::class.java)) {
            return DataManagementViewModel(
                transactionRepository = transactionRepository,
                backupFileManager = backupFileManager,
                receiptImageStorage = receiptImageStorage
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
