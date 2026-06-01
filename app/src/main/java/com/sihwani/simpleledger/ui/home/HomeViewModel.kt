package com.sihwani.simpleledger.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sihwani.simpleledger.data.pdf.MonthlyLedgerReportData
import com.sihwani.simpleledger.data.pdf.PdfExportManager
import com.sihwani.simpleledger.data.premium.PremiumRepository
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.domain.model.MonthlySummary
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.domain.premium.PremiumPolicy
import com.sihwani.simpleledger.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedMonthKey: String = DateUtils.currentMonthKey(),
    val monthLabel: String = DateUtils.formatMonthLabel(DateUtils.currentMonthKey()),
    val incomeTransactions: List<Transaction> = emptyList(),
    val expenseTransactions: List<Transaction> = emptyList(),
    val summary: MonthlySummary = MonthlySummary(
        income = 0L,
        expense = 0L,
        balance = 0L
    ),
    val isPremium: Boolean = false,
    val monthlyPdfTrialUsed: Int = 0,
    val monthlyPdfTrialRemaining: Int = PremiumPolicy.FreeMonthlyPdfTrialLimit,
    val isExportingMonthlyPdf: Boolean = false,
    val pdfExportMessage: String? = null,
    val pdfShareUriString: String? = null,
    val showPdfPremiumDialog: Boolean = false
)

private data class PdfExportUiState(
    val isExporting: Boolean = false,
    val message: String? = null,
    val shareUriString: String? = null,
    val showPremiumDialog: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val transactionRepository: TransactionRepository,
    private val premiumRepository: PremiumRepository,
    private val pdfExportManager: PdfExportManager
) : ViewModel() {
    private val selectedMonthKey = MutableStateFlow(DateUtils.currentMonthKey())
    private val pdfExportUiState = MutableStateFlow(PdfExportUiState())

    private val monthlyTransactions = selectedMonthKey.flatMapLatest { monthKey ->
        transactionRepository.observeTransactionsByMonth(monthKey)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        selectedMonthKey,
        monthlyTransactions,
        premiumRepository.isPremium,
        premiumRepository.monthlyPdfTrialUsed,
        pdfExportUiState
    ) { monthKey, transactions, isPremium, monthlyPdfTrialUsed, pdfState ->
        val incomeTransactions = transactions.filter { it.type == TransactionType.INCOME }
        val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
        val incomeTotal = incomeTransactions.sumOf { it.amount }
        val expenseTotal = expenseTransactions.sumOf { it.amount }
        val remainingTrialCount = (PremiumPolicy.FreeMonthlyPdfTrialLimit - monthlyPdfTrialUsed)
            .coerceAtLeast(0)

        HomeUiState(
            selectedMonthKey = monthKey,
            monthLabel = DateUtils.formatMonthLabel(monthKey),
            incomeTransactions = incomeTransactions,
            expenseTransactions = expenseTransactions,
            summary = MonthlySummary(
                income = incomeTotal,
                expense = expenseTotal,
                balance = incomeTotal - expenseTotal
            ),
            isPremium = isPremium,
            monthlyPdfTrialUsed = monthlyPdfTrialUsed,
            monthlyPdfTrialRemaining = remainingTrialCount,
            isExportingMonthlyPdf = pdfState.isExporting,
            pdfExportMessage = pdfState.message,
            pdfShareUriString = pdfState.shareUriString,
            showPdfPremiumDialog = pdfState.showPremiumDialog
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

    fun exportMonthlyPdf() {
        val currentState = uiState.value
        if (currentState.isExportingMonthlyPdf) {
            return
        }

        if (!currentState.isPremium && currentState.monthlyPdfTrialRemaining <= 0) {
            pdfExportUiState.update {
                it.copy(
                    showPremiumDialog = true,
                    message = null
                )
            }
            return
        }

        viewModelScope.launch {
            pdfExportUiState.update {
                it.copy(
                    isExporting = true,
                    message = null,
                    shareUriString = null,
                    showPremiumDialog = false
                )
            }

            val transactions = (currentState.incomeTransactions + currentState.expenseTransactions)
            val reportData = MonthlyLedgerReportData(
                monthKey = currentState.selectedMonthKey,
                summary = currentState.summary,
                transactions = transactions,
                generatedDateIso = DateUtils.todayIso()
            )

            runCatching {
                pdfExportManager.exportMonthlyLedger(reportData)
            }.onSuccess { exportedPdf ->
                if (!currentState.isPremium) {
                    premiumRepository.recordMonthlyPdfTrialUse()
                }
                pdfExportUiState.update {
                    it.copy(
                        isExporting = false,
                        message = "${exportedPdf.fileName} PDF를 만들었습니다.",
                        shareUriString = exportedPdf.uri.toString()
                    )
                }
            }.onFailure { throwable ->
                pdfExportUiState.update {
                    it.copy(
                        isExporting = false,
                        message = throwable.message ?: "PDF를 만들지 못했습니다."
                    )
                }
            }
        }
    }

    fun dismissPdfPremiumDialog() {
        pdfExportUiState.update { it.copy(showPremiumDialog = false) }
    }

    fun clearPdfShareUri() {
        pdfExportUiState.update { it.copy(shareUriString = null) }
    }

    fun showPdfOpenFailedMessage() {
        pdfExportUiState.update {
            it.copy(message = "PDF를 열거나 공유할 앱을 찾지 못했습니다.")
        }
    }
}

class HomeViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val premiumRepository: PremiumRepository,
    private val pdfExportManager: PdfExportManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                transactionRepository = transactionRepository,
                premiumRepository = premiumRepository,
                pdfExportManager = pdfExportManager
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
