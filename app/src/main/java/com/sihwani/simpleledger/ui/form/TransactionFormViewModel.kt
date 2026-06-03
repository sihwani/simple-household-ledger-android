package com.sihwani.simpleledger.ui.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sihwani.simpleledger.data.date.AppDateProvider
import com.sihwani.simpleledger.data.premium.PremiumRepository
import com.sihwani.simpleledger.data.repository.AccountRepository
import com.sihwani.simpleledger.data.repository.RecurringTransactionRepository
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.data.storage.ReceiptImageStorage
import com.sihwani.simpleledger.domain.model.Account
import com.sihwani.simpleledger.domain.model.RecurringRepeatType
import com.sihwani.simpleledger.domain.model.RecurringTransaction
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionCategories
import com.sihwani.simpleledger.domain.model.TransactionStatus
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.domain.premium.PremiumPolicy
import com.sihwani.simpleledger.domain.recurring.RecurringTransactionScheduler
import com.sihwani.simpleledger.util.DateUtils
import com.sihwani.simpleledger.util.MoneyInputFormatter
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.combine
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
    val transactionStatus: TransactionStatus = TransactionStatus.POSTED,
    val useRecurringRule: Boolean = false,
    val recurringRepeatType: RecurringRepeatType = RecurringRepeatType.MONTHLY,
    val recurringEndDate: String = "",
    val todayIso: String = DateUtils.todayIso(),
    val isPremium: Boolean = false,
    val activeRecurringRuleCount: Int = 0,
    val categories: List<String> = TransactionCategories.forType(type),
    val receiptImagePath: String? = null,
    val selectedReceiptImageUri: String? = null,
    val isReceiptMarkedForDeletion: Boolean = false,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val notFound: Boolean = false,
    val showRecurringPremiumDialog: Boolean = false,
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
        get() = type == TransactionType.EXPENSE && !useRecurringRule

    val showRecurringSection: Boolean
        get() = !isEditMode

    val isRecurringRuleLocked: Boolean
        get() = !isPremium && activeRecurringRuleCount >= PremiumPolicy.FreeRecurringRuleLimit

    val receiptPreviewSource: String?
        get() = selectedReceiptImageUri ?: receiptImagePath.takeUnless { isReceiptMarkedForDeletion }

    val accountOptions: List<Account>
        get() = accounts.filter { account -> account.isActive || account.id == selectedAccountId }

    val datePolicyNotice: String?
        get() {
            val selectedDate = runCatching { LocalDate.parse(date) }.getOrNull() ?: return null
            val today = runCatching { LocalDate.parse(todayIso) }.getOrNull() ?: return null
            if (selectedDate.isAfter(today)) {
                return null
            }

            return when {
                useRecurringRule -> "반복 거래는 내일 이후 날짜부터 등록할 수 있습니다."
                transactionStatus == TransactionStatus.SCHEDULED -> "예정 거래는 내일 이후 날짜로만 등록할 수 있습니다."
                else -> null
            }
        }
}

class TransactionFormViewModel(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val recurringTransactionRepository: RecurringTransactionRepository,
    private val premiumRepository: PremiumRepository,
    private val recurringTransactionScheduler: RecurringTransactionScheduler,
    private val receiptImageStorage: ReceiptImageStorage,
    private val appDateProvider: AppDateProvider,
    type: TransactionType,
    private val transactionId: String? = null
) : ViewModel() {
    private var originalTransaction: Transaction? = null
    private val _uiState = MutableStateFlow(
        TransactionFormUiState(
            type = type,
            date = appDateProvider.todayIso(),
            todayIso = appDateProvider.todayIso(),
            isEditMode = transactionId != null,
            isLoading = transactionId != null
        )
    )
    val uiState: StateFlow<TransactionFormUiState> = _uiState

    init {
        observeFormDependencies()
        transactionId?.let { id -> loadTransaction(id) }
    }

    private fun observeFormDependencies() {
        combine(
            accountRepository.observeAccounts(),
            premiumRepository.isPremium,
            recurringTransactionRepository.observeActive(),
            appDateProvider.state
        ) { accounts, isPremium, activeRecurringRules, dateState ->
            FormDependencies(
                accounts = accounts,
                isPremium = isPremium,
                activeRecurringRuleCount = activeRecurringRules.size,
                todayIso = dateState.currentDateIso
            )
        }
            .onEach { dependencies ->
                _uiState.update {
                    it.copy(
                        accounts = dependencies.accounts,
                        isPremium = dependencies.isPremium,
                        activeRecurringRuleCount = dependencies.activeRecurringRuleCount,
                        todayIso = dependencies.todayIso
                    )
                }
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
                    transactionStatus = transaction.transactionStatus,
                    useRecurringRule = false,
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

    fun onTransactionStatusChange(status: TransactionStatus) {
        _uiState.update {
            it.copy(
                transactionStatus = status,
                errorMessage = null
            )
        }
    }

    fun onUseRecurringRuleChange(useRecurringRule: Boolean) {
        val state = _uiState.value
        if (state.isEditMode) {
            return
        }
        if (useRecurringRule && state.isRecurringRuleLocked) {
            _uiState.update {
                it.copy(
                    showRecurringPremiumDialog = true,
                    useRecurringRule = false,
                    errorMessage = null
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                useRecurringRule = useRecurringRule,
                errorMessage = null
            )
        }
    }

    fun onRecurringRepeatTypeChange(repeatType: RecurringRepeatType) {
        _uiState.update {
            it.copy(
                recurringRepeatType = repeatType,
                errorMessage = null
            )
        }
    }

    fun onRecurringEndDateChange(value: String) {
        _uiState.update {
            it.copy(
                recurringEndDate = value,
                errorMessage = null
            )
        }
    }

    fun showRecurringPremiumInfo() {
        _uiState.update {
            it.copy(showRecurringPremiumDialog = true)
        }
    }

    fun dismissRecurringPremiumInfo() {
        _uiState.update {
            it.copy(showRecurringPremiumDialog = false)
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
        val recurringEndDate = state.recurringEndDate.trim().ifEmpty { null }
        val startDate = runCatching { LocalDate.parse(state.date) }.getOrNull()
        val today = appDateProvider.today()

        val validationMessage = when {
            state.isEditMode && original == null -> "수정할 내역을 찾을 수 없습니다."
            amount == null || amount < 1L -> "금액은 1원 이상 입력해주세요."
            title.isEmpty() -> "${state.titleLabel}를 입력해주세요."
            state.category.isBlank() -> "카테고리를 선택해주세요."
            !DateUtils.isValidIsoDate(state.date) -> "날짜는 YYYY-MM-DD 형식으로 입력해주세요."
            state.useRecurringRule && state.isRecurringRuleLocked -> "무료 체험 한도를 모두 사용했습니다."
            state.useRecurringRule && startDate != null && !startDate.isAfter(today) -> {
                "반복 거래는 내일 이후 날짜부터 등록할 수 있습니다."
            }
            !state.useRecurringRule &&
                state.transactionStatus == TransactionStatus.SCHEDULED &&
                startDate != null &&
                !startDate.isAfter(today) -> {
                "예정 거래는 내일 이후 날짜로만 등록할 수 있습니다."
            }
            state.useRecurringRule && recurringEndDate != null && !DateUtils.isValidIsoDate(recurringEndDate) -> {
                "종료일은 YYYY-MM-DD 형식으로 입력해주세요."
            }
            state.useRecurringRule && recurringEndDate != null && startDate != null &&
                LocalDate.parse(recurringEndDate).isBefore(startDate) -> {
                "종료일은 시작 날짜보다 빠를 수 없습니다."
            }
            else -> null
        }

        if (validationMessage != null) {
            _uiState.update { it.copy(errorMessage = validationMessage) }
            return
        }

        val validAmount = amount ?: return
        _uiState.update {
            it.copy(
                isSaving = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            var copiedReceiptPath: String? = null

            runCatching {
                val now = System.currentTimeMillis()
                if (state.useRecurringRule) {
                    saveRecurringRule(
                        state = state,
                        title = title,
                        amount = validAmount,
                        memo = memo,
                        endDate = recurringEndDate,
                        startDate = startDate ?: LocalDate.parse(state.date),
                        now = now
                    )
                    return@runCatching null
                }

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
                        updatedAt = if (state.isEditMode) now else null,
                        transactionStatus = state.transactionStatus,
                        recurringRuleId = original?.recurringRuleId,
                        recurringOccurrenceKey = original?.recurringOccurrenceKey
                    )
                )
                recurringTransactionScheduler.sync()

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

    private suspend fun saveRecurringRule(
        state: TransactionFormUiState,
        title: String,
        amount: Long,
        memo: String?,
        endDate: String?,
        startDate: LocalDate,
        now: Long
    ) {
        recurringTransactionRepository.upsert(
            RecurringTransaction(
                id = UUID.randomUUID().toString(),
                title = title,
                type = state.type,
                amount = amount,
                category = state.category,
                accountId = state.selectedAccountId,
                memo = memo,
                repeatType = state.recurringRepeatType,
                repeatDay = startDate.dayOfMonth,
                repeatMonth = startDate.monthValue,
                startDate = state.date,
                endDate = endDate,
                isActive = true,
                createdAt = now,
                updatedAt = null
            )
        )
        recurringTransactionScheduler.sync()
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

private data class FormDependencies(
    val accounts: List<Account>,
    val isPremium: Boolean,
    val activeRecurringRuleCount: Int,
    val todayIso: String
)

class TransactionFormViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val recurringTransactionRepository: RecurringTransactionRepository,
    private val premiumRepository: PremiumRepository,
    private val recurringTransactionScheduler: RecurringTransactionScheduler,
    private val receiptImageStorage: ReceiptImageStorage,
    private val appDateProvider: AppDateProvider,
    private val type: TransactionType,
    private val transactionId: String? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionFormViewModel::class.java)) {
            return TransactionFormViewModel(
                transactionRepository = transactionRepository,
                accountRepository = accountRepository,
                recurringTransactionRepository = recurringTransactionRepository,
                premiumRepository = premiumRepository,
                recurringTransactionScheduler = recurringTransactionScheduler,
                receiptImageStorage = receiptImageStorage,
                appDateProvider = appDateProvider,
                type = type,
                transactionId = transactionId
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
