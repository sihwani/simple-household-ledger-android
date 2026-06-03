package com.sihwani.simpleledger.ui.recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sihwani.simpleledger.data.date.AppDateProvider
import com.sihwani.simpleledger.data.premium.PremiumRepository
import com.sihwani.simpleledger.data.repository.AccountRepository
import com.sihwani.simpleledger.data.repository.RecurringTransactionRepository
import com.sihwani.simpleledger.domain.model.Account
import com.sihwani.simpleledger.domain.model.RecurringRepeatType
import com.sihwani.simpleledger.domain.model.RecurringTransaction
import com.sihwani.simpleledger.domain.model.TransactionCategories
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.domain.premium.PremiumPolicy
import com.sihwani.simpleledger.domain.recurring.RecurringTransactionScheduler
import com.sihwani.simpleledger.util.AccountFormatter
import com.sihwani.simpleledger.util.DateUtils
import com.sihwani.simpleledger.util.MoneyInputFormatter
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecurringTransactionUiState(
    val activeRules: List<RecurringRuleItem> = emptyList(),
    val inactiveRules: List<RecurringRuleItem> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val isPremium: Boolean = false,
    val showInactiveRules: Boolean = false,
    val form: RecurringRuleFormUiState? = null,
    val ruleIdPendingDeactivate: String? = null,
    val ruleIdPendingReactivate: String? = null,
    val ruleIdPendingDelete: String? = null,
    val showPremiumDialog: Boolean = false,
    val message: String? = null
)

data class RecurringRuleItem(
    val rule: RecurringTransaction,
    val accountLabel: String,
    val nextOccurrenceLabel: String
)

data class RecurringRuleFormUiState(
    val editingRuleId: String? = null,
    val type: TransactionType = TransactionType.EXPENSE,
    val amountText: String = "",
    val title: String = "",
    val category: String = TransactionCategories.forType(type).first(),
    val accountId: String? = null,
    val repeatType: RecurringRepeatType = RecurringRepeatType.MONTHLY,
    val startDate: String = DateUtils.todayIso(),
    val endDate: String = "",
    val memo: String = "",
    val errorMessage: String? = null,
    val isSaving: Boolean = false
) {
    val isEditMode: Boolean
        get() = editingRuleId != null

    val categories: List<String>
        get() = TransactionCategories.forType(type)
}

private data class RecurringRuleScreenState(
    val showInactiveRules: Boolean = false,
    val form: RecurringRuleFormUiState? = null,
    val ruleIdPendingDeactivate: String? = null,
    val ruleIdPendingReactivate: String? = null,
    val ruleIdPendingDelete: String? = null,
    val showPremiumDialog: Boolean = false,
    val message: String? = null
)

class RecurringTransactionViewModel(
    private val recurringTransactionRepository: RecurringTransactionRepository,
    private val accountRepository: AccountRepository,
    private val premiumRepository: PremiumRepository,
    private val scheduler: RecurringTransactionScheduler,
    private val appDateProvider: AppDateProvider
) : ViewModel() {
    private val screenState = MutableStateFlow(RecurringRuleScreenState())

    val uiState: StateFlow<RecurringTransactionUiState> = combine(
        recurringTransactionRepository.observeAll(),
        accountRepository.observeAccounts(),
        premiumRepository.isPremium,
        screenState
    ) { rules, accounts, isPremium, state ->
        val items = rules.map { rule ->
            RecurringRuleItem(
                rule = rule,
                accountLabel = accountLabel(rule, accounts),
                nextOccurrenceLabel = nextOccurrenceLabel(rule)
            )
        }

        RecurringTransactionUiState(
            activeRules = items.filter { item -> item.rule.isActive },
            inactiveRules = items.filterNot { item -> item.rule.isActive },
            accounts = accounts.filter { account -> account.isActive },
            isPremium = isPremium,
            showInactiveRules = state.showInactiveRules,
            form = state.form,
            ruleIdPendingDeactivate = state.ruleIdPendingDeactivate,
            ruleIdPendingReactivate = state.ruleIdPendingReactivate,
            ruleIdPendingDelete = state.ruleIdPendingDelete,
            showPremiumDialog = state.showPremiumDialog,
            message = state.message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RecurringTransactionUiState()
    )

    fun requestAddRule() {
        if (screenState.value.form?.isSaving == true) {
            return
        }

        val state = uiState.value
        if (!state.isPremium && state.activeRules.size >= PremiumPolicy.FreeRecurringRuleLimit) {
            showPremiumDialog()
            return
        }

        screenState.update {
            it.copy(
                form = RecurringRuleFormUiState(
                    startDate = appDateProvider.today().plusDays(1).toString()
                ),
                message = null
            )
        }
    }

    fun requestEditRule(ruleId: String) {
        if (screenState.value.form?.isSaving == true) {
            return
        }

        val rule = findRule(ruleId) ?: return
        screenState.update {
            it.copy(
                form = RecurringRuleFormUiState(
                    editingRuleId = rule.id,
                    type = rule.type,
                    amountText = MoneyInputFormatter.formatAmountInput(rule.amount.toString()),
                    title = rule.title,
                    category = rule.category,
                    accountId = rule.accountId,
                    repeatType = rule.repeatType,
                    startDate = rule.startDate,
                    endDate = rule.endDate.orEmpty(),
                    memo = rule.memo.orEmpty()
                ),
                message = null
            )
        }
    }

    fun dismissForm() {
        if (screenState.value.form?.isSaving == true) {
            return
        }
        screenState.update { it.copy(form = null) }
    }

    fun onTypeChange(type: TransactionType) {
        updateForm {
            it.copy(
                type = type,
                category = TransactionCategories.forType(type).first(),
                errorMessage = null
            )
        }
    }

    fun onAmountChange(value: String) {
        updateForm {
            it.copy(
                amountText = MoneyInputFormatter.formatAmountInput(value),
                errorMessage = null
            )
        }
    }

    fun onTitleChange(value: String) {
        updateForm { it.copy(title = value, errorMessage = null) }
    }

    fun onCategoryChange(value: String) {
        updateForm { it.copy(category = value, errorMessage = null) }
    }

    fun onAccountChange(accountId: String?) {
        updateForm { it.copy(accountId = accountId, errorMessage = null) }
    }

    fun onRepeatTypeChange(repeatType: RecurringRepeatType) {
        updateForm { it.copy(repeatType = repeatType, errorMessage = null) }
    }

    fun onStartDateChange(value: String) {
        updateForm { it.copy(startDate = value, errorMessage = null) }
    }

    fun onEndDateChange(value: String) {
        updateForm { it.copy(endDate = value, errorMessage = null) }
    }

    fun onMemoChange(value: String) {
        updateForm { it.copy(memo = value) }
    }

    fun saveRule() {
        val form = screenState.value.form ?: return
        if (form.isSaving) {
            return
        }

        val amount = MoneyInputFormatter.parseAmountInput(form.amountText)
        val title = form.title.trim()
        val memo = form.memo.trim().ifEmpty { null }
        val endDate = form.endDate.trim().ifEmpty { null }
        val startDate = runCatching { LocalDate.parse(form.startDate) }.getOrNull()
        val existingRule = findRule(form.editingRuleId)
        val today = appDateProvider.today()
        val shouldValidateFutureStart = existingRule == null || existingRule.startDate != form.startDate
        val validationMessage = when {
            form.isEditMode && existingRule == null -> "수정할 반복 거래를 찾을 수 없습니다."
            amount == null || amount < 1L -> "금액은 1원 이상 입력해주세요."
            title.isBlank() -> "제목을 입력해주세요."
            form.category.isBlank() -> "카테고리를 선택해주세요."
            !DateUtils.isValidIsoDate(form.startDate) -> "시작 날짜는 YYYY-MM-DD 형식으로 입력해주세요."
            shouldValidateFutureStart && startDate != null && !startDate.isAfter(today) -> {
                "반복 거래는 내일 이후 날짜부터 등록할 수 있습니다."
            }
            endDate != null && !DateUtils.isValidIsoDate(endDate) -> "종료 날짜는 YYYY-MM-DD 형식으로 입력해주세요."
            endDate != null && startDate != null && LocalDate.parse(endDate).isBefore(startDate) -> "종료 날짜는 시작 날짜보다 빠를 수 없습니다."
            else -> null
        }

        if (validationMessage != null) {
            updateForm { it.copy(errorMessage = validationMessage) }
            return
        }

        val validAmount = amount ?: return
        val validStartDate = startDate ?: return
        updateForm { it.copy(isSaving = true, errorMessage = null) }

        viewModelScope.launch {
            runCatching {
                val now = System.currentTimeMillis()
                recurringTransactionRepository.upsert(
                    RecurringTransaction(
                        id = existingRule?.id ?: UUID.randomUUID().toString(),
                        title = title,
                        type = form.type,
                        amount = validAmount,
                        category = form.category,
                        accountId = form.accountId,
                        memo = memo,
                        repeatType = form.repeatType,
                        repeatDay = validStartDate.dayOfMonth,
                        repeatMonth = validStartDate.monthValue,
                        startDate = form.startDate,
                        endDate = endDate,
                        isActive = existingRule?.isActive ?: true,
                        createdAt = existingRule?.createdAt ?: now,
                        updatedAt = if (existingRule != null) now else null
                    )
                )
                scheduler.sync()
            }.onSuccess {
                screenState.update {
                    it.copy(
                        form = null,
                        message = if (form.isEditMode) "반복 거래를 수정했습니다." else "반복 거래를 추가했습니다."
                    )
                }
            }.onFailure { throwable ->
                updateForm {
                    it.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "반복 거래를 저장하지 못했습니다."
                    )
                }
            }
        }
    }

    fun toggleInactiveRules() {
        screenState.update { it.copy(showInactiveRules = !it.showInactiveRules) }
    }

    fun requestDeactivate(ruleId: String) {
        screenState.update {
            it.copy(
                ruleIdPendingDeactivate = ruleId,
                message = null
            )
        }
    }

    fun dismissDeactivateDialog() {
        screenState.update { it.copy(ruleIdPendingDeactivate = null) }
    }

    fun confirmDeactivate() {
        val ruleId = screenState.value.ruleIdPendingDeactivate ?: return
        screenState.update { it.copy(ruleIdPendingDeactivate = null) }

        viewModelScope.launch {
            runCatching {
                recurringTransactionRepository.setActive(ruleId, false)
            }.onSuccess {
                screenState.update {
                    it.copy(
                        message = "반복 거래를 비활성화했습니다."
                    )
                }
            }.onFailure { throwable ->
                screenState.update {
                    it.copy(
                        message = throwable.message ?: "반복 거래를 비활성화하지 못했습니다."
                    )
                }
            }
        }
    }

    fun requestReactivate(ruleId: String) {
        val state = uiState.value
        if (!state.isPremium && state.activeRules.size >= PremiumPolicy.FreeRecurringRuleLimit) {
            showPremiumDialog()
            return
        }

        screenState.update {
            it.copy(
                ruleIdPendingReactivate = ruleId,
                message = null
            )
        }
    }

    fun dismissReactivateDialog() {
        screenState.update { it.copy(ruleIdPendingReactivate = null) }
    }

    fun confirmReactivate() {
        val ruleId = screenState.value.ruleIdPendingReactivate ?: return
        screenState.update { it.copy(ruleIdPendingReactivate = null) }

        viewModelScope.launch {
            runCatching {
                recurringTransactionRepository.setActive(ruleId, true)
                scheduler.sync()
            }.onSuccess {
                screenState.update {
                    it.copy(
                        message = "반복 거래를 다시 사용하도록 변경했습니다."
                    )
                }
            }.onFailure { throwable ->
                screenState.update {
                    it.copy(
                        message = throwable.message ?: "반복 거래를 다시 사용하도록 변경하지 못했습니다."
                    )
                }
            }
        }
    }

    fun requestDelete(ruleId: String) {
        screenState.update {
            it.copy(
                ruleIdPendingDelete = ruleId,
                message = null
            )
        }
    }

    fun dismissDeleteDialog() {
        screenState.update { it.copy(ruleIdPendingDelete = null) }
    }

    fun confirmDelete() {
        val ruleId = screenState.value.ruleIdPendingDelete ?: return
        screenState.update { it.copy(ruleIdPendingDelete = null) }

        viewModelScope.launch {
            runCatching {
                recurringTransactionRepository.delete(ruleId)
            }.onSuccess {
                screenState.update {
                    it.copy(
                        message = "반복 거래 원본을 삭제했습니다. 이미 생성된 거래는 유지됩니다."
                    )
                }
            }.onFailure { throwable ->
                screenState.update {
                    it.copy(
                        message = throwable.message ?: "반복 거래를 삭제하지 못했습니다."
                    )
                }
            }
        }
    }

    fun dismissPremiumDialog() {
        screenState.update { it.copy(showPremiumDialog = false) }
    }

    private fun updateForm(
        transform: (RecurringRuleFormUiState) -> RecurringRuleFormUiState
    ) {
        screenState.update { state ->
            state.copy(form = state.form?.let(transform))
        }
    }

    private fun showPremiumDialog() {
        screenState.update {
            it.copy(
                showPremiumDialog = true,
                message = null
            )
        }
    }

    private fun findRule(ruleId: String?): RecurringTransaction? {
        if (ruleId == null) {
            return null
        }

        return uiState.value.activeRules
            .plus(uiState.value.inactiveRules)
            .firstOrNull { item -> item.rule.id == ruleId }
            ?.rule
    }

    private fun accountLabel(
        rule: RecurringTransaction,
        accounts: List<Account>
    ): String {
        val accountId = rule.accountId ?: return "계좌 선택 안 함"
        val account = accounts.firstOrNull { item -> item.id == accountId }
            ?: return "삭제된 계좌"
        val suffix = if (account.isActive) "" else " · 비활성 계좌에 연결됨"
        return "${AccountFormatter.displayName(account)}$suffix"
    }

    private fun nextOccurrenceLabel(rule: RecurringTransaction): String {
        val today = appDateProvider.today()
        val startDate = LocalDate.parse(rule.startDate)
        val endDate = rule.endDate?.let { LocalDate.parse(it) }
        val searchStart = if (today.isAfter(startDate)) today else startDate
        val dates = (0..14).mapNotNull { offset ->
            nextCandidate(rule, startDate, searchStart.plusMonths(offset.toLong()))
        }.filter { date ->
            !date.isBefore(searchStart) && (endDate == null || !date.isAfter(endDate))
        }

        return dates.minOrNull()?.let { DateUtils.formatFullDate(it.toString()) } ?: "다음 예정일 없음"
    }

    private fun nextCandidate(
        rule: RecurringTransaction,
        startDate: LocalDate,
        basisDate: LocalDate
    ): LocalDate? {
        val day = rule.repeatDay ?: startDate.dayOfMonth
        return when (rule.repeatType) {
            RecurringRepeatType.MONTHLY -> YearMonth.from(basisDate).atCoercedDay(day)
            RecurringRepeatType.QUARTERLY -> {
                val startMonth = startDate.monthValue
                val monthDiff = (basisDate.year - startDate.year) * 12 + basisDate.monthValue - startMonth
                val step = if (monthDiff <= 0) 0 else (monthDiff + 2) / 3
                YearMonth.from(startDate).plusMonths(step.toLong() * 3L).atCoercedDay(day)
            }

            RecurringRepeatType.YEARLY -> {
                val month = (rule.repeatMonth ?: startDate.monthValue).coerceIn(1, 12)
                YearMonth.of(basisDate.year, month).atCoercedDay(day)
            }
        }
    }

    private fun YearMonth.atCoercedDay(day: Int): LocalDate {
        return atDay(day.coerceIn(1, lengthOfMonth()))
    }
}

class RecurringTransactionViewModelFactory(
    private val recurringTransactionRepository: RecurringTransactionRepository,
    private val accountRepository: AccountRepository,
    private val premiumRepository: PremiumRepository,
    private val scheduler: RecurringTransactionScheduler,
    private val appDateProvider: AppDateProvider
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecurringTransactionViewModel::class.java)) {
            return RecurringTransactionViewModel(
                recurringTransactionRepository = recurringTransactionRepository,
                accountRepository = accountRepository,
                premiumRepository = premiumRepository,
                scheduler = scheduler,
                appDateProvider = appDateProvider
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
