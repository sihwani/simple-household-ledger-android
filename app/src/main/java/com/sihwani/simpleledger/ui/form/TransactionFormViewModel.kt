package com.sihwani.simpleledger.ui.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sihwani.simpleledger.data.repository.AccountRepository
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.data.storage.ReceiptImageStorage
import com.sihwani.simpleledger.domain.model.Account
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionCategories
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.domain.premium.PremiumPolicy
import com.sihwani.simpleledger.util.DateUtils
import com.sihwani.simpleledger.util.MoneyInputFormatter
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionFormUiState(
    val type: TransactionType,
    val amountText: String = "",
    val title: String = "",
    val category: String = TransactionCategories.forType(type).first(),
    val date: String = DateUtils.todayIso(),
    val memo: String = "",
    val selectedAccountId: String? = null,
    val accounts: List<Account> = emptyList(),
    val categories: List<String> = TransactionCategories.forType(type),
    val receiptImagePath: String? = null,
    val selectedReceiptImageUri: String? = null,
    val isReceiptMarkedForDeletion: Boolean = false,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val notFound: Boolean = false,
    val errorMessage: String? = null,
    val saveCompleted: Boolean = false,
    val savedTransactionId: String? = null
) {
    val screenTitle: String
        get() = when (type) {
            TransactionType.INCOME -> if (isEditMode) "수입 수정" else "수입 작성"
            TransactionType.EXPENSE -> if (isEditMode) "지출 수정" else "지출 작성"
        }

    val titleLabel: String
        get() = when (type) {
            TransactionType.INCOME -> "수입명"
            TransactionType.EXPENSE -> "사용처"
        }

    val showReceiptSection: Boolean
        get() = type == TransactionType.EXPENSE

    val receiptPreviewSource: String?
        get() = selectedReceiptImageUri ?: receiptImagePath.takeUnless { isReceiptMarkedForDeletion }

    val accountOptions: List<Account>
        get() = accounts.filter { account -> account.isActive || account.id == selectedAccountId }
}

class TransactionFormViewModel(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val receiptImageStorage: ReceiptImageStorage,
    type: TransactionType,
    private val transactionId: String? = null
) : ViewModel() {
    private var originalTransaction: Transaction? = null
    private val _uiState = MutableStateFlow(
        TransactionFormUiState(
            type = type,
            isEditMode = transactionId != null,
            isLoading = transactionId != null
        )
    )
    val uiState: StateFlow<TransactionFormUiState> = _uiState

    init {
        observeAccounts()
        transactionId?.let { id -> loadTransaction(id) }
    }

    private fun observeAccounts() {
        accountRepository.observeAccounts()
            .onEach { accounts ->
                _uiState.update { it.copy(accounts = accounts) }
            }
            .launchIn(viewModelScope)
    }

    private fun loadTransaction(id: String) {
        viewModelScope.launch {
            val transaction = transactionRepository.getTransaction(id)

            if (transaction == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        notFound = true,
                        errorMessage = "내역을 찾을 수 없습니다."
                    )
                }
                return@launch
            }

            originalTransaction = transaction
            _uiState.update {
                it.copy(
                    type = transaction.type,
                    amountText = MoneyInputFormatter.formatAmountInput(transaction.amount.toString()),
                    title = transaction.title,
                    category = transaction.category,
                    date = transaction.date,
                    memo = transaction.memo.orEmpty(),
                    selectedAccountId = transaction.accountId,
                    categories = TransactionCategories.forType(transaction.type),
                    receiptImagePath = transaction.receiptImagePath,
                    selectedReceiptImageUri = null,
                    isReceiptMarkedForDeletion = false,
                    isLoading = false,
                    notFound = false,
                    errorMessage = null
                )
            }
        }
    }

    fun onAmountChange(value: String) {
        _uiState.update {
            it.copy(
                amountText = MoneyInputFormatter.formatAmountInput(value),
                errorMessage = null
            )
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update {
            it.copy(
                title = value,
                errorMessage = null
            )
        }
    }

    fun onCategoryChange(value: String) {
        _uiState.update {
            it.copy(
                category = value,
                errorMessage = null
            )
        }
    }

    fun onDateChange(value: String) {
        _uiState.update {
            it.copy(
                date = value,
                errorMessage = null
            )
        }
    }

    fun onMemoChange(value: String) {
        _uiState.update {
            it.copy(memo = value)
        }
    }

    fun onAccountChange(accountId: String?) {
        _uiState.update {
            it.copy(
                selectedAccountId = accountId,
                errorMessage = null
            )
        }
    }

    fun onReceiptImageSelected(uriString: String) {
        val state = _uiState.value
        if (state.type != TransactionType.EXPENSE) {
            return
        }

        _uiState.update {
            it.copy(
                selectedReceiptImageUri = uriString,
                isReceiptMarkedForDeletion = false,
                errorMessage = null
            )
        }
    }

    fun onReceiptImageRemove() {
        val state = _uiState.value
        if (state.type != TransactionType.EXPENSE) {
            return
        }

        _uiState.update {
            it.copy(
                selectedReceiptImageUri = null,
                isReceiptMarkedForDeletion = true,
                errorMessage = null
            )
        }
    }

    fun save() {
        val state = _uiState.value

        if (state.isLoading || state.isSaving) {
            return
        }

        val amount = MoneyInputFormatter.parseAmountInput(state.amountText)
        val title = state.title.trim()
        val memo = state.memo.trim().ifEmpty { null }
        val original = originalTransaction

        val validationMessage = when {
            state.isEditMode && original == null -> "수정할 내역을 찾을 수 없습니다."
            amount == null || amount < 1L -> "금액은 1원 이상 입력해주세요."
            title.isEmpty() -> "${state.titleLabel}를 입력해주세요."
            state.category.isBlank() -> "카테고리를 선택해주세요."
            !DateUtils.isValidIsoDate(state.date) -> "날짜는 YYYY-MM-DD 형식으로 입력해주세요."
            else -> null
        }

        if (validationMessage != null) {
            _uiState.update { it.copy(errorMessage = validationMessage) }
            return
        }

        val validAmount = amount ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null
                )
            }

            var copiedReceiptPath: String? = null

            runCatching {
                val now = System.currentTimeMillis()
                val transactionId = original?.id ?: UUID.randomUUID().toString()
                val selectedAccount = state.accounts.firstOrNull { account ->
                    account.id == state.selectedAccountId
                }
                val receiptImagePath = resolveReceiptImagePath(
                    state = state,
                    transactionId = transactionId
                ).also { path ->
                    if (state.selectedReceiptImageUri != null) {
                        copiedReceiptPath = path
                    }
                }

                transactionRepository.upsert(
                    Transaction(
                        id = transactionId,
                        type = state.type,
                        title = title,
                        amount = validAmount,
                        category = state.category,
                        date = state.date,
                        memo = memo,
                        receiptImagePath = receiptImagePath,
                        accountId = state.selectedAccountId,
                        accountSnapshotName = resolveAccountSnapshotName(
                            selectedAccount = selectedAccount,
                            original = original,
                            selectedAccountId = state.selectedAccountId
                        ),
                        accountSnapshotBankName = resolveAccountSnapshotBankName(
                            selectedAccount = selectedAccount,
                            original = original,
                            selectedAccountId = state.selectedAccountId
                        ),
                        accountSnapshotIdentifier = resolveAccountSnapshotIdentifier(
                            selectedAccount = selectedAccount,
                            original = original,
                            selectedAccountId = state.selectedAccountId
                        ),
                        createdAt = original?.createdAt ?: now,
                        updatedAt = if (state.isEditMode) now else null
                    )
                )

                val originalReceiptPath = original?.receiptImagePath
                if (originalReceiptPath != null && originalReceiptPath != receiptImagePath) {
                    runCatching {
                        receiptImageStorage.delete(originalReceiptPath)
                    }
                }

                transactionId
            }.onSuccess { savedTransactionId ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveCompleted = true,
                        savedTransactionId = savedTransactionId
                    )
                }
            }.onFailure { throwable ->
                copiedReceiptPath?.let { path -> receiptImageStorage.delete(path) }
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "저장하지 못했습니다. 다시 시도해주세요."
                    )
                }
            }
        }
    }

    private suspend fun resolveReceiptImagePath(
        state: TransactionFormUiState,
        transactionId: String
    ): String? {
        if (state.type != TransactionType.EXPENSE) {
            return null
        }

        state.selectedReceiptImageUri?.let { uriString ->
            return receiptImageStorage.copyToInternalStorage(
                sourceUriString = uriString,
                transactionId = transactionId,
                maxBytes = PremiumPolicy.ReceiptImageMaxBytes
            )
        }

        return if (state.isReceiptMarkedForDeletion) {
            null
        } else {
            originalTransaction?.receiptImagePath
        }
    }

    private fun resolveAccountSnapshotName(
        selectedAccount: Account?,
        original: Transaction?,
        selectedAccountId: String?
    ): String? {
        return when {
            selectedAccountId == null -> null
            selectedAccount != null -> selectedAccount.name
            selectedAccountId == original?.accountId -> original.accountSnapshotName
            else -> null
        }
    }

    private fun resolveAccountSnapshotBankName(
        selectedAccount: Account?,
        original: Transaction?,
        selectedAccountId: String?
    ): String? {
        return when {
            selectedAccountId == null -> null
            selectedAccount != null -> selectedAccount.bankName
            selectedAccountId == original?.accountId -> original.accountSnapshotBankName
            else -> null
        }
    }

    private fun resolveAccountSnapshotIdentifier(
        selectedAccount: Account?,
        original: Transaction?,
        selectedAccountId: String?
    ): String? {
        return when {
            selectedAccountId == null -> null
            selectedAccount != null -> selectedAccount.identifier
            selectedAccountId == original?.accountId -> original.accountSnapshotIdentifier
            else -> null
        }
    }
}

class TransactionFormViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val receiptImageStorage: ReceiptImageStorage,
    private val type: TransactionType,
    private val transactionId: String? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionFormViewModel::class.java)) {
            return TransactionFormViewModel(
                transactionRepository = transactionRepository,
                accountRepository = accountRepository,
                receiptImageStorage = receiptImageStorage,
                type = type,
                transactionId = transactionId
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
