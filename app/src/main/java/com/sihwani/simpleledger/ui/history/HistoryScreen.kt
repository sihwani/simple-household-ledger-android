package com.sihwani.simpleledger.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.util.DateUtils
import com.sihwani.simpleledger.util.MoneyFormatter

@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    dataManagementUiState: DataManagementUiState,
    onBack: () -> Unit,
    onTransactionClick: (String) -> Unit,
    onExportBackup: (String) -> Unit,
    onImportBackup: (String) -> Unit,
    onMergeImport: () -> Unit,
    onRequestReplaceImport: () -> Unit,
    onConfirmReplaceImport: () -> Unit,
    onDismissImportModeDialog: () -> Unit,
    onDismissReplaceConfirmDialog: () -> Unit,
    onRequestDeleteAll: () -> Unit,
    onConfirmDeleteAll: () -> Unit,
    onDismissDeleteAllConfirmDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7F9))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        HistoryHeader(onBack = onBack)

        when {
            uiState.isLoading -> HistoryNotice(
                title = "내역을 불러오는 중입니다.",
                description = null
            )

            uiState.sections.isEmpty() -> HistoryNotice(
                title = "아직 기록된 내역이 없습니다.",
                description = "수입이나 지출을 먼저 추가해보세요."
            )

            else -> uiState.sections.forEach { section ->
                MonthlyHistorySectionCard(
                    section = section,
                    onTransactionClick = onTransactionClick
                )
            }
        }

        DataManagementSection(
            uiState = dataManagementUiState,
            onExportBackup = onExportBackup,
            onImportBackup = onImportBackup,
            onMergeImport = onMergeImport,
            onRequestReplaceImport = onRequestReplaceImport,
            onConfirmReplaceImport = onConfirmReplaceImport,
            onDismissImportModeDialog = onDismissImportModeDialog,
            onDismissReplaceConfirmDialog = onDismissReplaceConfirmDialog,
            onRequestDeleteAll = onRequestDeleteAll,
            onConfirmDeleteAll = onConfirmDeleteAll,
            onDismissDeleteAllConfirmDialog = onDismissDeleteAllConfirmDialog
        )

        Spacer(modifier = Modifier.height(48.dp))
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
private fun MonthlyHistorySectionCard(
    section: MonthlyHistorySection,
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
                Text(
                    text = section.monthLabel,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF18181B)
                )
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

    MaterialTheme {
        Surface {
            HistoryScreen(
                uiState = HistoryUiState(
                    isLoading = false,
                    sections = listOf(
                        MonthlyHistorySection(
                            monthKey = "2026-06",
                            monthLabel = "2026년 6월",
                            incomeTotal = 2_800_000L,
                            expenseTotal = 126_000L,
                            balance = 2_674_000L,
                            transactions = transactions
                        )
                    )
                ),
                dataManagementUiState = DataManagementUiState(),
                onBack = {},
                onTransactionClick = {},
                onExportBackup = {},
                onImportBackup = {},
                onMergeImport = {},
                onRequestReplaceImport = {},
                onConfirmReplaceImport = {},
                onDismissImportModeDialog = {},
                onDismissReplaceConfirmDialog = {},
                onRequestDeleteAll = {},
                onConfirmDeleteAll = {},
                onDismissDeleteAllConfirmDialog = {}
            )
        }
    }
}
