package com.sihwani.simpleledger.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HistoryUiState(
    val isLoading: Boolean = true,
    val sections: List<MonthlyHistorySection> = emptyList()
)

data class MonthlyHistorySection(
    val monthKey: String,
    val monthLabel: String,
    val incomeTotal: Long,
    val expenseTotal: Long,
    val balance: Long,
    val transactions: List<Transaction>
)

class HistoryViewModel(
    transactionRepository: TransactionRepository
) : ViewModel() {
    val uiState: StateFlow<HistoryUiState> = transactionRepository.observeAllTransactions()
        .map { transactions ->
            HistoryUiState(
                isLoading = false,
                sections = transactions.toMonthlySections()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistoryUiState()
        )

    private fun List<Transaction>.toMonthlySections(): List<MonthlyHistorySection> {
        return sortedWith(transactionSort())
            .groupBy { transaction -> DateUtils.monthKey(transaction.date) }
            .entries
            .sortedByDescending { entry -> entry.key }
            .map { entry ->
                val monthTransactions = entry.value.sortedWith(transactionSort())
                val incomeTotal = monthTransactions
                    .filter { transaction -> transaction.type == TransactionType.INCOME }
                    .sumOf { transaction -> transaction.amount }
                val expenseTotal = monthTransactions
                    .filter { transaction -> transaction.type == TransactionType.EXPENSE }
                    .sumOf { transaction -> transaction.amount }

                MonthlyHistorySection(
                    monthKey = entry.key,
                    monthLabel = DateUtils.formatMonthLabel(entry.key),
                    incomeTotal = incomeTotal,
                    expenseTotal = expenseTotal,
                    balance = incomeTotal - expenseTotal,
                    transactions = monthTransactions
                )
            }
    }

    private fun transactionSort(): Comparator<Transaction> {
        return compareByDescending<Transaction> { transaction -> transaction.date }
            .thenByDescending { transaction -> transaction.createdAt }
    }
}

class HistoryViewModelFactory(
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(transactionRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
