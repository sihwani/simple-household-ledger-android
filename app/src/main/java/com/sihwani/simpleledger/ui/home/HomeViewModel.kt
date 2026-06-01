package com.sihwani.simpleledger.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.domain.model.MonthlySummary
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.util.DateUtils
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
    val summary: MonthlySummary = MonthlySummary(
        income = 0L,
        expense = 0L,
        balance = 0L
    )
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val selectedMonthKey = MutableStateFlow(DateUtils.currentMonthKey())

    private val monthlyTransactions = selectedMonthKey.flatMapLatest { monthKey ->
        transactionRepository.observeTransactionsByMonth(monthKey)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        selectedMonthKey,
        monthlyTransactions
    ) { monthKey, transactions ->
        val incomeTransactions = transactions.filter { it.type == TransactionType.INCOME }
        val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
        val incomeTotal = incomeTransactions.sumOf { it.amount }
        val expenseTotal = expenseTransactions.sumOf { it.amount }

        HomeUiState(
            selectedMonthKey = monthKey,
            monthLabel = DateUtils.formatMonthLabel(monthKey),
            incomeTransactions = incomeTransactions,
            expenseTransactions = expenseTransactions,
            summary = MonthlySummary(
                income = incomeTotal,
                expense = expenseTotal,
                balance = incomeTotal - expenseTotal
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

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

class HomeViewModelFactory(
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(transactionRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
