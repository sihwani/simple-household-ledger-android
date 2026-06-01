package com.sihwani.simpleledger.data.pdf

import com.sihwani.simpleledger.domain.model.Transaction

data class YearlyLedgerReportData(
    val year: Int,
    val incomeTotal: Long,
    val expenseTotal: Long,
    val balance: Long,
    val sections: List<YearlyLedgerMonthSection>,
    val generatedDateIso: String
)

data class YearlyLedgerMonthSection(
    val monthKey: String,
    val monthLabel: String,
    val incomeTotal: Long,
    val expenseTotal: Long,
    val balance: Long,
    val transactions: List<Transaction>
)
