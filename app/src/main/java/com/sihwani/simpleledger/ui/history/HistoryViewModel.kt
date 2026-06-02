package com.sihwani.simpleledger.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sihwani.simpleledger.data.date.AppDateProvider
import com.sihwani.simpleledger.data.pdf.MonthlyLedgerReportData
import com.sihwani.simpleledger.data.pdf.PdfExportManager
import com.sihwani.simpleledger.data.pdf.YearlyLedgerMonthSection
import com.sihwani.simpleledger.data.pdf.YearlyLedgerReportData
import com.sihwani.simpleledger.data.premium.PremiumRepository
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.domain.model.MonthlySummary
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionStatus
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.domain.premium.PremiumPolicy
import com.sihwani.simpleledger.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryUiState(
    val isLoading: Boolean = true,
    val sections: List<MonthlyHistorySection> = emptyList(),
    val yearSections: List<YearlyHistorySection> = emptyList(),
    val isPremium: Boolean = false,
    val pdfTrialUsed: Int = 0,
    val pdfTrialRemaining: Int = PremiumPolicy.FreePdfTrialLimit,
    val isExportingPdf: Boolean = false,
    val pdfMessage: String? = null,
    val pdfShareUriString: String? = null,
    val pendingPdfRequest: PendingLedgerPdfRequest? = null,
    val showPdfPremiumDialog: Boolean = false
)

data class MonthlyHistorySection(
    val monthKey: String,
    val monthLabel: String,
    val incomeTotal: Long,
    val expenseTotal: Long,
    val balance: Long,
    val transactions: List<Transaction>
)

data class YearlyHistorySection(
    val year: Int,
    val incomeTotal: Long,
    val expenseTotal: Long,
    val balance: Long,
    val months: List<MonthlyHistorySection>
)

enum class LedgerPdfReportType {
    MONTHLY,
    YEARLY
}

data class PendingLedgerPdfRequest(
    val type: LedgerPdfReportType,
    val title: String,
    val description: String,
    val monthKey: String? = null,
    val year: Int? = null
)

private data class HistoryPdfState(
    val isExportingPdf: Boolean = false,
    val pdfMessage: String? = null,
    val pdfShareUriString: String? = null,
    val pendingPdfRequest: PendingLedgerPdfRequest? = null,
    val showPdfPremiumDialog: Boolean = false
)

class HistoryViewModel(
    private val transactionRepository: TransactionRepository,
    private val premiumRepository: PremiumRepository,
    private val pdfExportManager: PdfExportManager,
    private val appDateProvider: AppDateProvider
) : ViewModel() {
    private val pdfState = MutableStateFlow(HistoryPdfState())

    val uiState: StateFlow<HistoryUiState> = combine(
        transactionRepository.observeAllTransactions(),
        premiumRepository.isPremium,
        premiumRepository.pdfTrialUsed,
        pdfState
    ) { transactions, isPremium, pdfTrialUsed, currentPdfState ->
        val monthlySections = transactions.toMonthlySections()
        val yearSections = monthlySections.toYearlySections()
        HistoryUiState(
            isLoading = false,
            sections = monthlySections,
            yearSections = yearSections,
            isPremium = isPremium,
            pdfTrialUsed = pdfTrialUsed,
            pdfTrialRemaining = (PremiumPolicy.FreePdfTrialLimit - pdfTrialUsed).coerceAtLeast(0),
            isExportingPdf = currentPdfState.isExportingPdf,
            pdfMessage = currentPdfState.pdfMessage,
            pdfShareUriString = currentPdfState.pdfShareUriString,
            pendingPdfRequest = currentPdfState.pendingPdfRequest,
            showPdfPremiumDialog = currentPdfState.showPdfPremiumDialog
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HistoryUiState()
    )

    fun requestMonthlyPdf(monthKey: String) {
        val state = uiState.value
        val section = state.sections.firstOrNull { it.monthKey == monthKey } ?: run {
            showPdfMessage("PDF로 만들 월 내역을 찾을 수 없습니다.")
            return
        }
        if (shouldBlockFreePdf(state)) {
            showPdfPremiumDialog()
            return
        }

        pdfState.update {
            it.copy(
                pdfMessage = null,
                pendingPdfRequest = PendingLedgerPdfRequest(
                    type = LedgerPdfReportType.MONTHLY,
                    title = "월 가계부 PDF",
                    description = "${section.monthLabel} 가계부를 PDF로 만듭니다.",
                    monthKey = section.monthKey
                ),
                showPdfPremiumDialog = false
            )
        }
    }

    fun requestYearlyPdf(year: Int) {
        val state = uiState.value
        val section = state.yearSections.firstOrNull { it.year == year } ?: run {
            showPdfMessage("PDF로 만들 연도 내역을 찾을 수 없습니다.")
            return
        }
        if (shouldBlockFreePdf(state)) {
            showPdfPremiumDialog()
            return
        }

        pdfState.update {
            it.copy(
                pdfMessage = null,
                pendingPdfRequest = PendingLedgerPdfRequest(
                    type = LedgerPdfReportType.YEARLY,
                    title = "연도 가계부 PDF",
                    description = "${section.year}년 가계부를 PDF로 만듭니다.",
                    year = section.year
                ),
                showPdfPremiumDialog = false
            )
        }
    }

    fun dismissPdfConfirmation() {
        pdfState.update { it.copy(pendingPdfRequest = null) }
    }

    fun confirmPdfGeneration() {
        val state = uiState.value
        val request = state.pendingPdfRequest ?: return
        if (state.isExportingPdf) {
            return
        }
        if (shouldBlockFreePdf(state)) {
            pdfState.update {
                it.copy(
                    pendingPdfRequest = null,
                    showPdfPremiumDialog = true
                )
            }
            return
        }

        pdfState.update {
            it.copy(
                isExportingPdf = true,
                pdfMessage = null
            )
        }

        viewModelScope.launch {
            runCatching {
                when (request.type) {
                    LedgerPdfReportType.MONTHLY -> {
                        val monthKey = request.monthKey.orEmpty()
                        val section = uiState.value.sections.first { it.monthKey == monthKey }
                        pdfExportManager.exportMonthlyLedger(section.toMonthlyReportData())
                    }

                    LedgerPdfReportType.YEARLY -> {
                        val year = requireNotNull(request.year)
                        val section = uiState.value.yearSections.first { it.year == year }
                        pdfExportManager.exportYearlyLedger(section.toYearlyReportData())
                    }
                }
            }.onSuccess { exportedPdf ->
                if (!state.isPremium) {
                    premiumRepository.recordPdfTrialUse()
                }
                pdfState.update {
                    it.copy(
                        isExportingPdf = false,
                        pendingPdfRequest = null,
                        pdfMessage = "${request.title}를 만들었습니다.",
                        pdfShareUriString = exportedPdf.uri.toString(),
                        showPdfPremiumDialog = false
                    )
                }
            }.onFailure {
                pdfState.update {
                    it.copy(
                        isExportingPdf = false,
                        pendingPdfRequest = null,
                        pdfMessage = "PDF 생성에 실패했습니다. 다시 시도해주세요."
                    )
                }
            }
        }
    }

    fun dismissPdfPremiumDialog() {
        pdfState.update { it.copy(showPdfPremiumDialog = false) }
    }

    fun clearPdfShareUri() {
        pdfState.update { it.copy(pdfShareUriString = null) }
    }

    fun showPdfOpenFailedMessage() {
        showPdfMessage("PDF를 공유할 앱을 열 수 없습니다.")
    }

    private fun showPdfMessage(message: String) {
        pdfState.update { it.copy(pdfMessage = message) }
    }

    private fun showPdfPremiumDialog() {
        pdfState.update {
            it.copy(
                pendingPdfRequest = null,
                showPdfPremiumDialog = true
            )
        }
    }

    private fun shouldBlockFreePdf(state: HistoryUiState): Boolean {
        return !state.isPremium && state.pdfTrialRemaining <= 0
    }

    private fun List<Transaction>.toMonthlySections(): List<MonthlyHistorySection> {
        return sortedWith(transactionSort())
            .groupBy { transaction -> DateUtils.monthKey(transaction.date) }
            .entries
            .sortedByDescending { entry -> entry.key }
            .map { entry ->
                val monthTransactions = entry.value.sortedWith(transactionSort())
                val postedTransactions = monthTransactions
                    .filter { transaction -> transaction.transactionStatus == TransactionStatus.POSTED }
                val incomeTotal = postedTransactions
                    .filter { transaction -> transaction.type == TransactionType.INCOME }
                    .sumOf { transaction -> transaction.amount }
                val expenseTotal = postedTransactions
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

    private fun List<MonthlyHistorySection>.toYearlySections(): List<YearlyHistorySection> {
        return groupBy { section -> section.monthKey.take(4).toInt() }
            .entries
            .sortedByDescending { entry -> entry.key }
            .map { entry ->
                val months = entry.value.sortedByDescending { section -> section.monthKey }
                val incomeTotal = months.sumOf { section -> section.incomeTotal }
                val expenseTotal = months.sumOf { section -> section.expenseTotal }
                YearlyHistorySection(
                    year = entry.key,
                    incomeTotal = incomeTotal,
                    expenseTotal = expenseTotal,
                    balance = incomeTotal - expenseTotal,
                    months = months
                )
            }
    }

    private fun MonthlyHistorySection.toMonthlyReportData(): MonthlyLedgerReportData {
        return MonthlyLedgerReportData(
            monthKey = monthKey,
            summary = MonthlySummary(
                income = incomeTotal,
                expense = expenseTotal,
                balance = balance
            ),
            transactions = transactions
                .filter { transaction -> transaction.transactionStatus == TransactionStatus.POSTED },
            generatedDateIso = appDateProvider.todayIso()
        )
    }

    private fun YearlyHistorySection.toYearlyReportData(): YearlyLedgerReportData {
        return YearlyLedgerReportData(
            year = year,
            incomeTotal = incomeTotal,
            expenseTotal = expenseTotal,
            balance = balance,
            sections = months.map { month ->
                YearlyLedgerMonthSection(
                    monthKey = month.monthKey,
                    monthLabel = month.monthLabel,
                    incomeTotal = month.incomeTotal,
                    expenseTotal = month.expenseTotal,
                    balance = month.balance,
                    transactions = month.transactions
                        .filter { transaction -> transaction.transactionStatus == TransactionStatus.POSTED }
                )
            },
            generatedDateIso = appDateProvider.todayIso()
        )
    }

    private fun transactionSort(): Comparator<Transaction> {
        return compareByDescending<Transaction> { transaction -> transaction.date }
            .thenByDescending { transaction -> transaction.createdAt }
    }
}

class HistoryViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val premiumRepository: PremiumRepository,
    private val pdfExportManager: PdfExportManager,
    private val appDateProvider: AppDateProvider
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(
                transactionRepository = transactionRepository,
                premiumRepository = premiumRepository,
                pdfExportManager = pdfExportManager,
                appDateProvider = appDateProvider
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
