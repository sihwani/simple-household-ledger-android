package com.sihwani.simpleledger.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sihwani.simpleledger.data.repository.AccountRepository
import com.sihwani.simpleledger.data.repository.RecurringTransactionRepository
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.data.storage.ReceiptImageStorage
import com.sihwani.simpleledger.domain.model.RecurringSkippedOccurrence
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.util.AccountFormatter
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionDetailUiState(
    val isLoading: Boolean = true,
    val transaction: Transaction? = null,
    val notFound: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isDeleting: Boolean = false,
    val deleteCompleted: Boolean = false,
    val accountLabel: String? = null,
    val errorMessage: String? = null
)

class TransactionDetailViewModel(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val recurringTransactionRepository: RecurringTransactionRepository,
    private val receiptImageStorage: ReceiptImageStorage,
    private val transactionId: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionDetailUiState())
    val uiState: StateFlow<TransactionDetailUiState> = _uiState

    init {
        observeTransaction()
    }

    private fun observeTransaction() {
        combine(
            transactionRepository.observeTransaction(transactionId),
            accountRepository.observeAccounts()
        ) { transaction, accounts ->
            val accountLabel = transaction?.accountId?.let { accountId ->
                accounts.firstOrNull { account -> account.id == accountId }
                    ?.let { account -> AccountFormatter.displayName(account) }
                    ?: AccountFormatter.displaySnapshot(
                        name = transaction.accountSnapshotName,
                        bankName = transaction.accountSnapshotBankName,
                        identifier = transaction.accountSnapshotIdentifier
                    )?.let { snapshotLabel -> "$snapshotLabel (삭제된 계좌 정보)" }
            }
            transaction to accountLabel
        }
            .collectWithState()
    }

    private fun Flow<Pair<Transaction?, String?>>.collectWithState() {
        onEach { (transaction, accountLabel) ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        transaction = transaction,
                        notFound = transaction == null,
                        accountLabel = accountLabel,
                        errorMessage = null
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun requestDelete() {
        _uiState.update {
            it.copy(
                showDeleteDialog = true,
                errorMessage = null
            )
        }
    }

    fun dismissDeleteDialog() {
        if (_uiState.value.isDeleting) {
            return
        }

        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun deleteTransaction() {
        val transaction = _uiState.value.transaction ?: return
        val id = transaction.id
        val receiptImagePath = transaction.receiptImagePath

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDeleting = true,
                    errorMessage = null
                )
            }

            runCatching {
                val recurringRuleId = transaction.recurringRuleId
                val recurringOccurrenceKey = transaction.recurringOccurrenceKey
                if (recurringRuleId != null && recurringOccurrenceKey != null) {
                    recurringTransactionRepository.addSkippedOccurrence(
                        RecurringSkippedOccurrence(
                            id = UUID.randomUUID().toString(),
                            recurringRuleId = recurringRuleId,
                            recurringOccurrenceKey = recurringOccurrenceKey,
                            createdAt = System.currentTimeMillis()
                        )
                    )
                }
                transactionRepository.delete(id)
                runCatching {
                    receiptImageStorage.delete(receiptImagePath)
                }
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        showDeleteDialog = false,
                        isDeleting = false,
                        deleteCompleted = true
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        errorMessage = "삭제하지 못했습니다. 다시 시도해주세요."
                    )
                }
            }
        }
    }
}

class TransactionDetailViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val recurringTransactionRepository: RecurringTransactionRepository,
    private val receiptImageStorage: ReceiptImageStorage,
    private val transactionId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionDetailViewModel::class.java)) {
            return TransactionDetailViewModel(
                transactionRepository = transactionRepository,
                accountRepository = accountRepository,
                recurringTransactionRepository = recurringTransactionRepository,
                receiptImageStorage = receiptImageStorage,
                transactionId = transactionId
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
