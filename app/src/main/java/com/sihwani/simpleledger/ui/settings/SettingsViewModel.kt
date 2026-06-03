package com.sihwani.simpleledger.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sihwani.simpleledger.BuildConfig
import com.sihwani.simpleledger.data.date.AppDateProvider
import com.sihwani.simpleledger.data.layout.ScreenLayoutPreferenceRepository
import com.sihwani.simpleledger.data.premium.PremiumRepository
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.domain.layout.ScreenLayoutPreference
import com.sihwani.simpleledger.domain.recurring.RecurringTransactionScheduler
import com.sihwani.simpleledger.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val receiptImageCount: Int = 0,
    val isPremium: Boolean = false,
    val screenLayoutPreference: ScreenLayoutPreference = ScreenLayoutPreference.AUTO,
    val currentDateIso: String = DateUtils.todayIso(),
    val testDateIso: String? = null,
    val isUsingTestDate: Boolean = false,
    val debugDateMessage: String? = null
)

class SettingsViewModel(
    transactionRepository: TransactionRepository,
    private val premiumRepository: PremiumRepository,
    private val screenLayoutPreferenceRepository: ScreenLayoutPreferenceRepository,
    private val appDateProvider: AppDateProvider,
    private val recurringTransactionScheduler: RecurringTransactionScheduler
) : ViewModel() {
    private val debugDateMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        transactionRepository.observeAllTransactions(),
        premiumRepository.isPremium,
        screenLayoutPreferenceRepository.screenLayoutPreference,
        appDateProvider.state,
        debugDateMessage
    ) { transactions, isPremium, screenLayoutPreference, dateState, message ->
            SettingsUiState(
                receiptImageCount = transactions.count { transaction ->
                    !transaction.receiptImagePath.isNullOrBlank()
                },
                isPremium = isPremium,
                screenLayoutPreference = screenLayoutPreference,
                currentDateIso = dateState.currentDateIso,
                testDateIso = dateState.testDateIso,
                isUsingTestDate = dateState.isUsingTestDate,
                debugDateMessage = message
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

    fun setScreenLayoutPreference(preference: ScreenLayoutPreference) {
        screenLayoutPreferenceRepository.setScreenLayoutPreference(preference)
    }

    fun setTestDateForDebug(dateIso: String) {
        if (!BuildConfig.DEBUG) {
            return
        }
        if (!DateUtils.isValidIsoDate(dateIso)) {
            debugDateMessage.value = "테스트 날짜는 YYYY-MM-DD 형식으로 선택해주세요."
            return
        }

        appDateProvider.setTestDateForDebug(dateIso)
        debugDateMessage.value = "테스트 날짜를 $dateIso 기준으로 설정했습니다."
    }

    fun clearTestDateForDebug() {
        if (!BuildConfig.DEBUG) {
            return
        }

        appDateProvider.clearTestDateForDebug()
        debugDateMessage.value = "실제 오늘 날짜 기준으로 되돌렸습니다."
    }

    fun runScheduledSyncForDebug() {
        if (!BuildConfig.DEBUG) {
            return
        }

        viewModelScope.launch {
            debugDateMessage.value = "예정/반복 거래 동기화 실행 중입니다."
            runCatching {
                recurringTransactionScheduler.sync()
            }.onSuccess {
                debugDateMessage.value = "예정/반복 거래 동기화를 실행했습니다."
            }.onFailure { throwable ->
                debugDateMessage.value = throwable.message ?: "예정/반복 거래 동기화에 실패했습니다."
            }
        }
    }
}

class SettingsViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val premiumRepository: PremiumRepository,
    private val screenLayoutPreferenceRepository: ScreenLayoutPreferenceRepository,
    private val appDateProvider: AppDateProvider,
    private val recurringTransactionScheduler: RecurringTransactionScheduler
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(
                transactionRepository = transactionRepository,
                premiumRepository = premiumRepository,
                screenLayoutPreferenceRepository = screenLayoutPreferenceRepository,
                appDateProvider = appDateProvider,
                recurringTransactionScheduler = recurringTransactionScheduler
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
