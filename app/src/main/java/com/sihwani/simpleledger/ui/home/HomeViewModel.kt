package com.sihwani.simpleledger.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sihwani.simpleledger.data.date.AppDateProvider
import com.sihwani.simpleledger.data.repository.AccountRepository
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.domain.account.AccountBalanceCalculator
import com.sihwani.simpleledger.domain.model.MonthlySummary
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionStatus
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.domain.recurring.RecurringTransactionScheduler
import com.sihwani.simpleledger.util.AccountFormatter
import com.sihwani.simpleledger.util.DateUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class HomeUiState(
    val selectedMonthKey: String = DateUtils.currentMonthKey(),
    val monthLabel: String = DateUtils.formatMonthLabel(DateUtils.currentMonthKey()),
    val incomeTransactions: List<Transaction> = emptyList(),
    val expenseTransactions: List<Transaction> = emptyList(),
    val accountSummary: HomeAccountSummaryUiState = HomeAccountSummaryUiState(),
    val summary: MonthlySummary = MonthlySummary(
        income = 0L,
        expense = 0L,
        balance = 0L
    )
)

data class HomeAccountSummaryUiState(
    val totalCalculatedBalance: Long = 0L,
    val scheduledIncome: Long = 0L,
    val scheduledExpense: Long = 0L,
    val expectedMonthEndBalance: Long = 0L,
    val scheduledMonthLabel: String = DateUtils.formatMonthLabel(DateUtils.currentMonthKey()),
    val previewAccounts: List<HomeAccountBalanceItem> = emptyList(),
    val hiddenAccountCount: Int = 0,
    val hasActiveAccounts: Boolean = false
)

data class HomeAccountBalanceItem(
    val id: String,
    val name: String,
    val calculatedBalance: Long,
    val expectedMonthEndBalance: Long
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val appDateProvider: AppDateProvider,
    private val recurringTransactionScheduler: RecurringTransactionScheduler
) : ViewModel() {
    private val selectedMonthKey = MutableStateFlow(appDateProvider.currentMonthKey())

    private val monthlyTransactions = selectedMonthKey.flatMapLatest { monthKey ->
        transactionRepository.observeTransactionsByMonth(monthKey)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        selectedMonthKey,
        monthlyTransactions,
        accountRepository.observeActiveAccounts(),
        transactionRepository.observeAllTransactions()
    ) { monthKey, transactions, activeAccounts, allTransactions ->
        val incomeTransactions = transactions.filter { it.type == TransactionType.INCOME }
        val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
        val postedTransactions = transactions.filter { it.transactionStatus == TransactionStatus.POSTED }
        val incomeTotal = postedTransactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        val expenseTotal = postedTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
        val accountBalances = AccountBalanceCalculator.summarizeActive(
            accounts = activeAccounts,
            transactions = allTransactions,
            monthKey = monthKey
        )
        val previewAccounts = accountBalances.take(HomeAccountPreviewLimit).map { item ->
            HomeAccountBalanceItem(
                id = item.account.id,
                name = AccountFormatter.shortName(item.account),
                calculatedBalance = item.calculatedBalance,
                expectedMonthEndBalance = item.expectedMonthEndBalance
            )
        }

        HomeUiState(
            selectedMonthKey = monthKey,
            monthLabel = DateUtils.formatMonthLabel(monthKey),
            incomeTransactions = incomeTransactions,
            expenseTransactions = expenseTransactions,
            accountSummary = HomeAccountSummaryUiState(
                totalCalculatedBalance = accountBalances.sumOf { item -> item.calculatedBalance },
                scheduledIncome = accountBalances.sumOf { item -> item.scheduledIncome },
                scheduledExpense = accountBalances.sumOf { item -> item.scheduledExpense },
                expectedMonthEndBalance = accountBalances.sumOf { item -> item.expectedMonthEndBalance },
                scheduledMonthLabel = DateUtils.formatMonthLabel(monthKey),
                previewAccounts = previewAccounts,
                hiddenAccountCount = (accountBalances.size - previewAccounts.size).coerceAtLeast(0),
                hasActiveAccounts = accountBalances.isNotEmpty()
            ),
            summary = MonthlySummary(
                income = incomeTotal,
                expense = expenseTotal,
                balance = incomeTotal - expenseTotal
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(
            selectedMonthKey = appDateProvider.currentMonthKey(),
            monthLabel = DateUtils.formatMonthLabel(appDateProvider.currentMonthKey())
        )
    )

    init {
        viewModelScope.launch {
            recurringTransactionScheduler.sync()
        }
    }

    fun movePreviousMonth() {
        selectedMonthKey.update { monthKey -> DateUtils.shiftMonth(monthKey, -1) }
    }

    fun moveNextMonth() {
        selectedMonthKey.update { monthKey -> DateUtils.shiftMonth(monthKey, 1) }
    }

    fun moveToMonth(monthKey: String) {
        if (DateUtils.isValidMonthKey(monthKey)) {
            selectedMonthKey.value = monthKey
        }
    }
}

private const val HomeAccountPreviewLimit = 2

class HomeViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val appDateProvider: AppDateProvider,
    private val recurringTransactionScheduler: RecurringTransactionScheduler
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                transactionRepository = transactionRepository,
                accountRepository = accountRepository,
                appDateProvider = appDateProvider,
                recurringTransactionScheduler = recurringTransactionScheduler
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
