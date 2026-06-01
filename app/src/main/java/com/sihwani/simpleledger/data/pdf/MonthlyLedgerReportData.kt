package com.sihwani.simpleledger.data.pdf

import com.sihwani.simpleledger.domain.model.MonthlySummary
import com.sihwani.simpleledger.domain.model.Transaction

data class MonthlyLedgerReportData(
    val monthKey: String,
    val summary: MonthlySummary,
    val transactions: List<Transaction>,
    val generatedDateIso: String
)
