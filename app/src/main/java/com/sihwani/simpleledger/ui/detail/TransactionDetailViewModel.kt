package com.sihwani.simpleledger.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.data.storage.ReceiptImageStorage
import com.sihwani.simpleledger.domain.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    val errorMessage: String? = null
)

class TransactionDetailViewModel(
    private val transactionRepository: TransactionRepository,
    private val receiptImageStorage: ReceiptImageStorage,
    private val transactionId: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionDetailUiState())
    val uiState: StateFlow<TransactionDetailUiState> = _uiState

    init {
        observeTransaction()
    }

    private fun observeTransaction() {
        transactionRepository.observeTransaction(transactionId)
            .onEach { transaction ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        transaction = transaction,
                        notFound = transaction == null,
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
    private val receiptImageStorage: ReceiptImageStorage,
    private val transactionId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionDetailViewModel::class.java)) {
            return TransactionDetailViewModel(
                transactionRepository = transactionRepository,
                receiptImageStorage = receiptImageStorage,
                transactionId = transactionId
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
