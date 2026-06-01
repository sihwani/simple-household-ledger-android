package com.sihwani.simpleledger.ui.history

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.domain.premium.PremiumPolicy
import com.sihwani.simpleledger.util.DateUtils
import com.sihwani.simpleledger.util.MoneyFormatter

@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    onBack: () -> Unit,
    onTransactionClick: (String) -> Unit,
    onRequestMonthlyPdf: (String) -> Unit,
    onRequestYearlyPdf: (Int) -> Unit,
    onDismissPdfConfirmation: () -> Unit,
    onConfirmPdfGeneration: () -> Unit,
    onDismissPdfPremiumDialog: () -> Unit,
    onPdfShareIntentHandled: () -> Unit,
    onPdfOpenFailed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shareUriString = uiState.pdfShareUriString

    LaunchedEffect(shareUriString) {
        if (shareUriString == null) {
            return@LaunchedEffect
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, Uri.parse(shareUriString))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val launched = runCatching {
            context.startActivity(Intent.createChooser(shareIntent, "PDF 공유"))
        }.isSuccess
        onPdfShareIntentHandled()
        if (!launched) {
            onPdfOpenFailed()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7F9))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        HistoryHeader(onBack = onBack)

        uiState.pdfMessage?.let { message ->
            PdfStatusCard(message = message)
        }

        when {
            uiState.isLoading -> HistoryNotice(
                title = "내역을 불러오는 중입니다.",
                description = null
            )

            uiState.yearSections.isEmpty() -> HistoryNotice(
                title = "아직 기록된 내역이 없습니다.",
                description = "수입이나 지출을 먼저 추가해보세요."
            )

            else -> uiState.yearSections.forEach { section ->
                YearHistorySectionCard(
                    section = section,
                    isExportingPdf = uiState.isExportingPdf,
                    onRequestYearlyPdf = onRequestYearlyPdf,
                    onRequestMonthlyPdf = onRequestMonthlyPdf,
                    onTransactionClick = onTransactionClick
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }

    uiState.pendingPdfRequest?.let { request ->
        PdfConfirmDialog(
            request = request,
            isPremium = uiState.isPremium,
            remainingTrial = uiState.pdfTrialRemaining,
            isExporting = uiState.isExportingPdf,
            onConfirm = onConfirmPdfGeneration,
            onDismiss = onDismissPdfConfirmation
        )
    }

    if (uiState.showPdfPremiumDialog) {
        PdfPremiumDialog(onDismiss = onDismissPdfPremiumDialog)
    }
}

@Composable
private fun HistoryHeader(
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onBack,
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "뒤로")
        }
        Text(
            text = "전체 내역",
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF18181B)
        )
    }
}

@Composable
private fun YearHistorySectionCard(
    section: YearlyHistorySection,
    isExportingPdf: Boolean,
    onRequestYearlyPdf: (Int) -> Unit,
    onRequestMonthlyPdf: (String) -> Unit,
    onTransactionClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        YearSummaryCard(
            section = section,
            isExportingPdf = isExportingPdf,
            onRequestYearlyPdf = onRequestYearlyPdf
        )

        section.months.forEach { monthSection ->
            MonthlyHistorySectionCard(
                section = monthSection,
                isExportingPdf = isExportingPdf,
                onRequestMonthlyPdf = onRequestMonthlyPdf,
                onTransactionClick = onTransactionClick
            )
        }
    }
}

@Composable
private fun YearSummaryCard(
    section: YearlyHistorySection,
    isExportingPdf: Boolean,
    onRequestYearlyPdf: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "${section.year}년",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "총 수입 ${MoneyFormatter.formatWon(section.incomeTotal)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD4D4D8),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "총 지출 ${MoneyFormatter.formatWon(section.expenseTotal)} · 잔액 ${MoneyFormatter.formatSignedWon(section.balance)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD4D4D8),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Button(
                onClick = { onRequestYearlyPdf(section.year) },
                enabled = !isExportingPdf,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF18181B)
                )
            ) {
                Text(
                    text = "연도 PDF",
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun MonthlyHistorySectionCard(
    section: MonthlyHistorySection,
    isExportingPdf: Boolean,
    onRequestMonthlyPdf: (String) -> Unit,
    onTransactionClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = section.monthLabel,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF18181B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    OutlinedButton(
                        onClick = { onRequestMonthlyPdf(section.monthKey) },
                        enabled = !isExportingPdf,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "PDF",
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1
                        )
                    }
                }
                MonthlySummaryLine(
                    label = "총 수입",
                    amount = "+${MoneyFormatter.formatWon(section.incomeTotal)}",
                    color = Color(0xFF047857)
                )
                MonthlySummaryLine(
                    label = "총 지출",
                    amount = "-${MoneyFormatter.formatWon(section.expenseTotal)}",
                    color = Color(0xFFBE123C)
                )
                MonthlySummaryLine(
                    label = "${section.monthKey.takeLast(2).toInt()}월 잔액",
                    amount = MoneyFormatter.formatSignedWon(section.balance),
                    color = if (section.balance >= 0L) Color(0xFF047857) else Color(0xFFBE123C)
                )
            }

            HorizontalDivider(color = Color(0xFFE4E4E7))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                section.transactions.forEach { transaction ->
                    HistoryTransactionItem(
                        transaction = transaction,
                        onClick = { onTransactionClick(transaction.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthlySummaryLine(
    label: String,
    amount: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.8f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF71717A),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = amount,
            modifier = Modifier.weight(1.4f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun HistoryTransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val amountText = when (transaction.type) {
        TransactionType.INCOME -> "+${MoneyFormatter.formatWon(transaction.amount)}"
        TransactionType.EXPENSE -> "-${MoneyFormatter.formatWon(transaction.amount)}"
    }
    val amountColor = when (transaction.type) {
        TransactionType.INCOME -> Color(0xFF047857)
        TransactionType.EXPENSE -> Color(0xFFBE123C)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                color = Color(0xFFF9FAFB),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = DateUtils.formatMonthDayLabel(transaction.date),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF71717A)
        )
        Text(
            text = transaction.title,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF18181B),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = amountText,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            color = amountColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PdfStatusCard(
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(14.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF047857)
        )
    }
}

@Composable
private fun HistoryNotice(
    title: String,
    description: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF18181B)
            )
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF71717A)
                )
            }
        }
    }
}

@Composable
private fun PdfConfirmDialog(
    request: PendingLedgerPdfRequest,
    isPremium: Boolean,
    remainingTrial: Int,
    isExporting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!isExporting) {
                onDismiss()
            }
        },
        title = { Text(text = request.title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = request.description)
                Text(
                    text = if (isPremium) {
                        "프리미엄 상태에서는 PDF를 제한 없이 만들 수 있습니다."
                    } else {
                        "무료 체험 ${remainingTrial}회 남음"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isExporting
            ) {
                Text(text = if (isExporting) "만드는 중" else "만들기")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isExporting
            ) {
                Text(text = "취소")
            }
        }
    )
}

@Composable
private fun PdfPremiumDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "프리미엄 안내") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "PDF 내보내기 무료 체험 ${PremiumPolicy.FreePdfTrialLimit}회를 모두 사용했습니다.")
                Text(text = "프리미엄을 구매하면 광고 없이 사용할 수 있고, 월별/연도별 가계부 PDF를 제한 없이 만들 수 있습니다.")
                Text(
                    text = "예상 가격: 1,500원 1회 구매",
                    fontWeight = FontWeight.Bold
                )
                Text(text = "※ 결제 기능은 출시 준비 단계에서 연결될 예정입니다.")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "확인")
            }
        }
    )
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun HistoryScreenPreview() {
    val transactions = listOf(
        Transaction(
            id = "income",
            type = TransactionType.INCOME,
            title = "월급",
            amount = 2_800_000L,
            category = "월급",
            date = "2026-06-25",
            memo = null,
            receiptImagePath = null,
            createdAt = 2L,
            updatedAt = null
        ),
        Transaction(
            id = "expense",
            type = TransactionType.EXPENSE,
            title = "이마트 트레이더스에서 생활용품과 식료품을 한 번에 많이 구매한 내역",
            amount = 126_000L,
            category = "생활",
            date = "2026-06-02",
            memo = null,
            receiptImagePath = null,
            createdAt = 1L,
            updatedAt = null
        )
    )
    val monthSection = MonthlyHistorySection(
        monthKey = "2026-06",
        monthLabel = "2026년 6월",
        incomeTotal = 2_800_000L,
        expenseTotal = 126_000L,
        balance = 2_674_000L,
        transactions = transactions
    )

    MaterialTheme {
        Surface {
            HistoryScreen(
                uiState = HistoryUiState(
                    isLoading = false,
                    sections = listOf(monthSection),
                    yearSections = listOf(
                        YearlyHistorySection(
                            year = 2026,
                            incomeTotal = 2_800_000L,
                            expenseTotal = 126_000L,
                            balance = 2_674_000L,
                            months = listOf(monthSection)
                        )
                    )
                ),
                onBack = {},
                onTransactionClick = {},
                onRequestMonthlyPdf = {},
                onRequestYearlyPdf = {},
                onDismissPdfConfirmation = {},
                onConfirmPdfGeneration = {},
                onDismissPdfPremiumDialog = {},
                onPdfShareIntentHandled = {},
                onPdfOpenFailed = {}
            )
        }
    }
}
