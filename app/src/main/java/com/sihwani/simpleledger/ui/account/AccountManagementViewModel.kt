package com.sihwani.simpleledger.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sihwani.simpleledger.data.date.AppDateProvider
import com.sihwani.simpleledger.data.premium.PremiumRepository
import com.sihwani.simpleledger.data.repository.AccountRepository
import com.sihwani.simpleledger.data.repository.RecurringTransactionRepository
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.domain.account.AccountBalanceCalculator
import com.sihwani.simpleledger.domain.model.Account
import com.sihwani.simpleledger.domain.premium.PremiumPolicy
import com.sihwani.simpleledger.util.AccountFormatter
import com.sihwani.simpleledger.util.DateUtils
import com.sihwani.simpleledger.util.MoneyInputFormatter
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AccountManagementUiState(
    val accounts: List<AccountBalanceItem> = emptyList(),
    val inactiveAccounts: List<AccountBalanceItem> = emptyList(),
    val showInactiveAccounts: Boolean = false,
    val isPremium: Boolean = false,
    val activeAccountCount: Int = 0,
    val form: AccountFormUiState? = null,
    val accountIdPendingDeactivate: String? = null,
    val accountIdPendingReactivate: String? = null,
    val deleteDialog: AccountDeleteDialogUiState? = null,
    val showPremiumDialog: Boolean = false,
    val message: String? = null
)

data class AccountBalanceItem(
    val account: Account,
    val calculatedBalance: Long,
    val scheduledIncome: Long = 0L,
    val scheduledExpense: Long = 0L,
    val expectedMonthEndBalance: Long = calculatedBalance,
    val scheduledMonthLabel: String = DateUtils.formatMonthLabel(DateUtils.currentMonthKey())
)

data class AccountFormUiState(
    val editingAccountId: String? = null,
    val name: String = "",
    val bankName: String = "",
    val identifier: String = "",
    val baseBalanceText: String = "",
    val baseDate: String = DateUtils.todayIso(),
    val memo: String = "",
    val errorMessage: String? = null,
    val isSaving: Boolean = false
) {
    val isEditMode: Boolean
        get() = editingAccountId != null
}

data class AccountDeleteDialogUiState(
    val accountId: String,
    val accountLabel: String,
    val linkedTransactionCount: Int
)

private data class AccountManagementFormState(
    val form: AccountFormUiState? = null,
    val showInactiveAccounts: Boolean = false,
    val accountIdPendingDeactivate: String? = null,
    val accountIdPendingReactivate: String? = null,
    val deleteDialog: AccountDeleteDialogUiState? = null,
    val showPremiumDialog: Boolean = false,
    val message: String? = null
)

class AccountManagementViewModel(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val recurringTransactionRepository: RecurringTransactionRepository,
    private val premiumRepository: PremiumRepository,
    private val appDateProvider: AppDateProvider
) : ViewModel() {
    private val formState = MutableStateFlow(AccountManagementFormState())

    val uiState: StateFlow<AccountManagementUiState> = combine(
        accountRepository.observeAccounts(),
        transactionRepository.observeAllTransactions(),
        premiumRepository.isPremium,
        appDateProvider.state,
        formState
    ) { accounts, transactions, isPremium, dateState, currentFormState ->
        val currentMonthKey = DateUtils.monthKey(dateState.currentDateIso)
        val accountItems = accounts.map { account ->
            val scheduled = AccountBalanceCalculator.calculateScheduledForMonth(
                account = account,
                transactions = transactions,
                monthKey = currentMonthKey
            )
            val calculatedBalance = AccountBalanceCalculator.calculate(account, transactions)
            AccountBalanceItem(
                account = account,
                calculatedBalance = calculatedBalance,
                scheduledIncome = scheduled.income,
                scheduledExpense = scheduled.expense,
                expectedMonthEndBalance = calculatedBalance + scheduled.income - scheduled.expense,
                scheduledMonthLabel = DateUtils.formatMonthLabel(currentMonthKey)
            )
        }
        AccountManagementUiState(
            accounts = accountItems.filter { item -> item.account.isActive },
            inactiveAccounts = accountItems.filterNot { item -> item.account.isActive },
            showInactiveAccounts = currentFormState.showInactiveAccounts,
            isPremium = isPremium,
            activeAccountCount = accountItems.count { item -> item.account.isActive },
            form = currentFormState.form,
            accountIdPendingDeactivate = currentFormState.accountIdPendingDeactivate,
            accountIdPendingReactivate = currentFormState.accountIdPendingReactivate,
            deleteDialog = currentFormState.deleteDialog,
            showPremiumDialog = currentFormState.showPremiumDialog,
            message = currentFormState.message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AccountManagementUiState()
    )

    fun requestAddAccount() {
        val state = uiState.value
        if (isFreeLimitReached(state)) {
            showPremiumDialog()
            return
        }

        formState.update {
            it.copy(
                form = AccountFormUiState(baseDate = appDateProvider.todayIso()),
                message = null
            )
        }
    }

    fun requestEditAccount(accountId: String) {
        val account = findAccount(accountId) ?: return

        formState.update {
            it.copy(
                form = AccountFormUiState(
                    editingAccountId = account.id,
                    name = account.name,
                    bankName = account.bankName.orEmpty(),
                    identifier = account.identifier.orEmpty(),
                    baseBalanceText = MoneyInputFormatter.formatAmountInput(account.baseBalance.toString()),
                    baseDate = account.baseDate,
                    memo = account.memo.orEmpty()
                ),
                message = null
            )
        }
    }

    fun dismissForm() {
        if (formState.value.form?.isSaving == true) {
            return
        }

        formState.update { it.copy(form = null) }
    }

    fun onNameChange(value: String) {
        updateForm { it.copy(name = value, errorMessage = null) }
    }

    fun onBankNameChange(value: String) {
        updateForm { it.copy(bankName = value, errorMessage = null) }
    }

    fun onIdentifierChange(value: String) {
        updateForm { it.copy(identifier = value.take(20), errorMessage = null) }
    }

    fun onBaseBalanceChange(value: String) {
        updateForm {
            it.copy(
                baseBalanceText = MoneyInputFormatter.formatAmountInput(value),
                errorMessage = null
            )
        }
    }

    fun onBaseDateChange(value: String) {
        updateForm { it.copy(baseDate = value, errorMessage = null) }
    }

    fun onMemoChange(value: String) {
        updateForm { it.copy(memo = value) }
    }

    fun saveAccount() {
        val form = formState.value.form ?: return
        if (form.isSaving) {
            return
        }

        val name = form.name.trim()
        val baseBalance = MoneyInputFormatter.parseAmountInput(form.baseBalanceText)
        val bankName = form.bankName.trim().ifEmpty { null }
        val identifier = form.identifier.trim().ifEmpty { null }
        val memo = form.memo.trim().ifEmpty { null }
        val validationMessage = when {
            name.isBlank() -> "계좌/지갑 이름을 입력해주세요."
            baseBalance == null -> "기준 잔액을 입력해주세요."
            !DateUtils.isValidIsoDate(form.baseDate) -> "기준 날짜는 YYYY-MM-DD 형식으로 입력해주세요."
            else -> null
        }

        if (validationMessage != null) {
            updateForm { it.copy(errorMessage = validationMessage) }
            return
        }

        viewModelScope.launch {
            updateForm { it.copy(isSaving = true, errorMessage = null) }

            runCatching {
                val now = System.currentTimeMillis()
                val existingAccount = findAccount(form.editingAccountId)

                accountRepository.upsert(
                    Account(
                        id = existingAccount?.id ?: UUID.randomUUID().toString(),
                        name = name,
                        bankName = bankName,
                        identifier = identifier,
                        baseBalance = baseBalance ?: 0L,
                        baseDate = form.baseDate,
                        memo = memo,
                        isActive = existingAccount?.isActive ?: true,
                        createdAt = existingAccount?.createdAt ?: now,
                        updatedAt = if (existingAccount != null) now else null
                    )
                )
            }.onSuccess {
                formState.update {
                    it.copy(
                        form = null,
                        message = if (form.isEditMode) {
                            "계좌/지갑을 수정했습니다."
                        } else {
                            "계좌/지갑을 추가했습니다."
                        }
                    )
                }
            }.onFailure { throwable ->
                updateForm {
                    it.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "계좌/지갑을 저장하지 못했습니다."
                    )
                }
            }
        }
    }

    fun toggleInactiveAccounts() {
        formState.update {
            it.copy(showInactiveAccounts = !it.showInactiveAccounts)
        }
    }

    fun requestDeactivate(accountId: String) {
        formState.update {
            it.copy(
                accountIdPendingDeactivate = accountId,
                message = null
            )
        }
    }

    fun dismissDeactivateDialog() {
        formState.update { it.copy(accountIdPendingDeactivate = null) }
    }

    fun confirmDeactivate() {
        val accountId = formState.value.accountIdPendingDeactivate ?: return
        viewModelScope.launch {
            runCatching {
                accountRepository.setActive(accountId, false)
            }.onSuccess {
                formState.update {
                    it.copy(
                        accountIdPendingDeactivate = null,
                        message = "계좌/지갑을 비활성화했습니다."
                    )
                }
            }.onFailure { throwable ->
                formState.update {
                    it.copy(
                        accountIdPendingDeactivate = null,
                        message = throwable.message ?: "계좌/지갑을 비활성화하지 못했습니다."
                    )
                }
            }
        }
    }

    fun requestReactivate(accountId: String) {
        val state = uiState.value
        if (isFreeLimitReached(state)) {
            showPremiumDialog()
            return
        }

        formState.update {
            it.copy(
                accountIdPendingReactivate = accountId,
                message = null
            )
        }
    }

    fun dismissReactivateDialog() {
        formState.update { it.copy(accountIdPendingReactivate = null) }
    }

    fun confirmReactivate() {
        val accountId = formState.value.accountIdPendingReactivate ?: return
        viewModelScope.launch {
            runCatching {
                accountRepository.setActive(accountId, true)
            }.onSuccess {
                formState.update {
                    it.copy(
                        accountIdPendingReactivate = null,
                        message = "계좌/지갑을 다시 사용하도록 변경했습니다."
                    )
                }
            }.onFailure { throwable ->
                formState.update {
                    it.copy(
                        accountIdPendingReactivate = null,
                        message = throwable.message ?: "계좌/지갑을 다시 사용하도록 변경하지 못했습니다."
                    )
                }
            }
        }
    }

    fun requestDelete(accountId: String) {
        val account = findAccount(accountId) ?: return
        viewModelScope.launch {
            runCatching {
                accountRepository.countTransactionsForAccount(accountId)
            }.onSuccess { linkedCount ->
                formState.update {
                    it.copy(
                        deleteDialog = AccountDeleteDialogUiState(
                            accountId = account.id,
                            accountLabel = AccountFormatter.displayName(account),
                            linkedTransactionCount = linkedCount
                        ),
                        message = null
                    )
                }
            }.onFailure { throwable ->
                formState.update {
                    it.copy(message = throwable.message ?: "계좌/지갑 삭제 정보를 확인하지 못했습니다.")
                }
            }
        }
    }

    fun dismissDeleteDialog() {
        formState.update { it.copy(deleteDialog = null) }
    }

    fun confirmDelete() {
        val deleteDialog = formState.value.deleteDialog ?: return
        val account = findAccount(deleteDialog.accountId) ?: return

        viewModelScope.launch {
            runCatching {
                if (deleteDialog.linkedTransactionCount > 0) {
                    transactionRepository.updateAccountSnapshot(account)
                }
                recurringTransactionRepository.clearAccount(account.id)
                accountRepository.deleteAccount(account.id)
            }.onSuccess {
                formState.update {
                    it.copy(
                        deleteDialog = null,
                        message = if (deleteDialog.linkedTransactionCount > 0) {
                            "계좌/지갑을 삭제했습니다. 기존 거래에는 삭제 당시 정보가 표시됩니다."
                        } else {
                            "계좌/지갑을 삭제했습니다."
                        }
                    )
                }
            }.onFailure { throwable ->
                formState.update {
                    it.copy(
                        deleteDialog = null,
                        message = throwable.message ?: "계좌/지갑을 삭제하지 못했습니다."
                    )
                }
            }
        }
    }

    fun dismissPremiumDialog() {
        formState.update { it.copy(showPremiumDialog = false) }
    }

    private fun updateForm(
        transform: (AccountFormUiState) -> AccountFormUiState
    ) {
        formState.update { state ->
            state.copy(form = state.form?.let(transform))
        }
    }

    private fun showPremiumDialog() {
        formState.update {
            it.copy(
                showPremiumDialog = true,
                message = null
            )
        }
    }

    private fun isFreeLimitReached(state: AccountManagementUiState): Boolean {
        return !state.isPremium && state.activeAccountCount >= PremiumPolicy.FreeAccountLimit
    }

    private fun findAccount(accountId: String?): Account? {
        if (accountId == null) {
            return null
        }

        return uiState.value.accounts
            .plus(uiState.value.inactiveAccounts)
            .firstOrNull { item -> item.account.id == accountId }
            ?.account
    }
}

class AccountManagementViewModelFactory(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val recurringTransactionRepository: RecurringTransactionRepository,
    private val premiumRepository: PremiumRepository,
    private val appDateProvider: AppDateProvider
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountManagementViewModel::class.java)) {
            return AccountManagementViewModel(
                accountRepository = accountRepository,
                transactionRepository = transactionRepository,
                recurringTransactionRepository = recurringTransactionRepository,
                premiumRepository = premiumRepository,
                appDateProvider = appDateProvider
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
