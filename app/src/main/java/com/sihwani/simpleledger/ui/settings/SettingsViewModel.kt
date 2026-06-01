package com.sihwani.simpleledger.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sihwani.simpleledger.data.premium.PremiumRepository
import com.sihwani.simpleledger.data.repository.TransactionRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

data class SettingsUiState(
    val receiptImageCount: Int = 0,
    val isPremium: Boolean = false
)

class SettingsViewModel(
    transactionRepository: TransactionRepository,
    private val premiumRepository: PremiumRepository
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = combine(
        transactionRepository.observeAllTransactions(),
        premiumRepository.isPremium
    ) { transactions, isPremium ->
            SettingsUiState(
                receiptImageCount = transactions.count { transaction ->
                    !transaction.receiptImagePath.isNullOrBlank()
                },
                isPremium = isPremium
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState()
        )

    fun setPremiumForDebug(isPremium: Boolean) {
        premiumRepository.setPremiumForDebug(isPremium)
    }
}

class SettingsViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val premiumRepository: PremiumRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(
                transactionRepository = transactionRepository,
                premiumRepository = premiumRepository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
