package com.sihwani.simpleledger.ui.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sihwani.simpleledger.domain.model.MonthlySummary
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.domain.premium.PremiumPolicy
import com.sihwani.simpleledger.ui.ads.TopBannerAd
import com.sihwani.simpleledger.util.DateUtils
import com.sihwani.simpleledger.util.MoneyFormatter

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddExpense: () -> Unit,
    onAddIncome: () -> Unit,
    onHome: () -> Unit,
    onShowHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onMonthSelected: (String) -> Unit,
    onExportMonthlyPdf: () -> Unit,
    onDismissPdfPremiumDialog: () -> Unit,
    onPdfShareIntentHandled: () -> Unit,
    onPdfOpenFailed: () -> Unit,
    onTransactionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LaunchedEffect(uiState.pdfShareUriString) {
        val uriString = uiState.pdfShareUriString ?: return@LaunchedEffect
        val uri = Uri.parse(uriString)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, "PDF 공유")
        runCatching {
            context.startActivity(chooser)
        }.onFailure { throwable ->
            if (throwable is ActivityNotFoundException) {
                onPdfOpenFailed()
            }
        }
        onPdfShareIntentHandled()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF6F7F9),
        bottomBar = {
            Surface(color = Color(0xFFF6F7F9)) {
                HomeActionBar(
                    onAddExpense = onAddExpense,
                    onHome = onHome,
                    onAddIncome = onAddIncome,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F7F9))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HomeHeader(onOpenSettings = onOpenSettings)
            TopBannerAd(isPremium = uiState.isPremium)
            MonthSelector(
                selectedMonthKey = uiState.selectedMonthKey,
                monthLabel = uiState.monthLabel,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                onMonthSelected = onMonthSelected
            )
            SummaryCard(summary = uiState.summary)
            MonthlyPdfExportCard(
                isPremium = uiState.isPremium,
                trialUsed = uiState.monthlyPdfTrialUsed,
                trialRemaining = uiState.monthlyPdfTrialRemaining,
                isExporting = uiState.isExportingMonthlyPdf,
                message = uiState.pdfExportMessage,
                onExportMonthlyPdf = onExportMonthlyPdf
            )
            HistoryButton(onShowHistory = onShowHistory)
            TransactionColumns(
                expenseTransactions = uiState.expenseTransactions,
                incomeTransactions = uiState.incomeTransactions,
                onTransactionClick = onTransactionClick
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (uiState.showPdfPremiumDialog) {
        MonthlyPdfPremiumDialog(
            onDismiss = onDismissPdfPremiumDialog
        )
    }
}

@Composable
private fun HomeActionBar(
    onAddExpense: () -> Unit,
    onHome: () -> Unit,
    onAddIncome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onAddExpense,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18181B))
        ) {
            Text(text = "지출")
        }
        Button(
            onClick = onHome,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF4F4F5),
                contentColor = Color(0xFF18181B)
            )
        ) {
            Text(
                text = "홈",
                fontWeight = FontWeight.ExtraBold
            )
        }
        Button(
            onClick = onAddIncome,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18181B))
        ) {
            Text(text = "수입")
        }
    }
}

@Composable
private fun HomeHeader(
    onOpenSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "한눈 가계부",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF18181B)
            )
            Text(
                text = "월별 수입과 지출을 한눈에 확인하세요.",
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF71717A)
            )
        }
        OutlinedButton(
            onClick = onOpenSettings,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
        ) {
            Text(
                text = "설정",
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun MonthSelector(
    selectedMonthKey: String,
    monthLabel: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onMonthSelected: (String) -> Unit
) {
    var showMonthPicker by rememberSaveable { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F4F5)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onPreviousMonth,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "이전")
            }
            Text(
                text = monthLabel,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { showMonthPicker = true }
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF18181B)
            )
            OutlinedButton(
                onClick = onNextMonth,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "다음")
            }
        }
    }

    if (showMonthPicker) {
        MonthPickerDialog(
            selectedMonthKey = selectedMonthKey,
            onDismiss = { showMonthPicker = false },
            onConfirm = { monthKey ->
                onMonthSelected(monthKey)
                showMonthPicker = false
            }
        )
    }
}

@Composable
private fun MonthPickerDialog(
    selectedMonthKey: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val initialYear = selectedMonthKey.take(4).toIntOrNull()
        ?.coerceIn(1900, 2100)
        ?: 2026
    val initialMonth = selectedMonthKey.takeLast(2).toIntOrNull()
        ?.coerceIn(1, 12)
        ?: 1
    var selectedYear by rememberSaveable(selectedMonthKey) { mutableStateOf(initialYear) }
    var selectedMonth by rememberSaveable(selectedMonthKey) { mutableStateOf(initialMonth) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "월 선택") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF4F4F5))
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { selectedYear -= 1 },
                        enabled = selectedYear > 1900,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                    ) {
                        Text(text = "<")
                    }
                    Text(
                        text = "${selectedYear}년",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF18181B)
                    )
                    OutlinedButton(
                        onClick = { selectedYear += 1 },
                        enabled = selectedYear < 2100,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                    ) {
                        Text(text = ">")
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    (0 until 3).forEach { rowIndex ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            (1..4).forEach { columnIndex ->
                                val month = rowIndex * 4 + columnIndex
                                MonthPickerButton(
                                    month = month,
                                    selected = selectedMonth == month,
                                    onClick = { selectedMonth = month },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(DateUtils.monthKeyOf(selectedYear, selectedMonth))
                }
            ) {
                Text(text = "확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "취소")
            }
        }
    )
}

@Composable
private fun MonthPickerButton(
    month: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    val backgroundColor = if (selected) Color(0xFF18181B) else Color.White
    val contentColor = if (selected) Color.White else Color(0xFF18181B)
    val borderColor = if (selected) Color(0xFF18181B) else Color(0xFFD4D4D8)

    Box(
        modifier = modifier
            .height(44.dp)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .clip(shape)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${month}월",
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Clip
        )
    }
}

@Composable
private fun SummaryCard(summary: MonthlySummary) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "이번 달 잔액",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4D4D8)
            )
            Text(
                text = MoneyFormatter.formatWon(summary.balance),
                modifier = Modifier.padding(top = 10.dp),
                fontSize = 30.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (summary.balance >= 0L) Color(0xFF047857) else Color(0xFFBE123C)
            )
            Text(
                text = "수입 ${MoneyFormatter.formatWon(summary.income)} · 지출 ${MoneyFormatter.formatWon(summary.expense)}",
                modifier = Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE4E4E7)
            )
        }
    }
}

@Composable
private fun MonthlyPdfExportCard(
    isPremium: Boolean,
    trialUsed: Int,
    trialRemaining: Int,
    isExporting: Boolean,
    message: String?,
    onExportMonthlyPdf: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "월 가계부 PDF",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF18181B)
            )
            Text(
                text = if (isPremium) {
                    "프리미엄: 제한 없이 PDF를 만들 수 있습니다."
                } else {
                    "무료 체험 ${trialRemaining}회 남음 (${trialUsed}/${PremiumPolicy.FreeMonthlyPdfTrialLimit}회 사용)"
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF71717A)
            )
            Button(
                onClick = onExportMonthlyPdf,
                enabled = !isExporting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18181B))
            ) {
                Text(
                    text = if (isExporting) "PDF 만드는 중" else "프린트용 월 가계부 PDF 만들기",
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            message?.let {
                Text(
                    text = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF4F4F5),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF18181B)
                )
            }
        }
    }
}

@Composable
private fun MonthlyPdfPremiumDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "프리미엄 기능") },
        text = {
            Text(
                text = "프린트용 월 가계부 PDF는 프리미엄 기능입니다.\n\n" +
                    "무료 사용자는 ${PremiumPolicy.FreeMonthlyPdfTrialLimit}회까지 체험할 수 있습니다.\n" +
                    "프리미엄을 구매하면 광고 없이 사용할 수 있고, 월 가계부 PDF를 제한 없이 만들 수 있습니다.\n\n" +
                    "예상 가격: 1,500원 1회 구매",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF18181B)
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "확인")
            }
        }
    )
}

@Composable
private fun HistoryButton(
    onShowHistory: () -> Unit
) {
    OutlinedButton(
        onClick = onShowHistory,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "전체 내역 보기",
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun TransactionColumns(
    expenseTransactions: List<Transaction>,
    incomeTransactions: List<Transaction>,
    onTransactionClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TransactionColumn(
            title = "지출",
            count = expenseTransactions.size,
            transactions = expenseTransactions,
            emptyText = "지출 내역이 없습니다.",
            accentColor = Color(0xFFBE123C),
            backgroundColor = Color(0xFFFFF1F2),
            onTransactionClick = onTransactionClick,
            modifier = Modifier.weight(1f)
        )
        TransactionColumn(
            title = "수입",
            count = incomeTransactions.size,
            transactions = incomeTransactions,
            emptyText = "수입 내역이 없습니다.",
            accentColor = Color(0xFF047857),
            backgroundColor = Color(0xFFECFDF5),
            onTransactionClick = onTransactionClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TransactionColumn(
    title: String,
    count: Int,
    transactions: List<Transaction>,
    emptyText: String,
    accentColor: Color,
    backgroundColor: Color,
    onTransactionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
            Text(
                text = "${count}건",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
        }

        if (transactions.isEmpty()) {
            EmptyTransactionBox(text = emptyText)
        } else {
            transactions.forEach { transaction ->
                HomeTransactionCard(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction.id) }
                )
            }
        }
    }
}

@Composable
private fun HomeTransactionCard(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val color = if (transaction.type == TransactionType.INCOME) {
        Color(0xFF047857)
    } else {
        Color(0xFFBE123C)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = DateUtils.formatDayLabel(transaction.date),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF71717A)
            )
            Text(
                text = transaction.title,
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF18181B)
            )
            Text(
                text = MoneyFormatter.formatWon(transaction.amount),
                modifier = Modifier.padding(top = 6.dp),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.ExtraBold,
                color = color,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EmptyTransactionBox(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFA1A1AA)
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun HomeScreenPreview() {
    val transactions = listOf(
        Transaction(
            id = "preview-expense",
            type = TransactionType.EXPENSE,
            title = "마트 장보기",
            amount = 126_000L,
            category = "식비",
            date = "2026-06-02",
            memo = null,
            receiptImagePath = null,
            createdAt = 0L,
            updatedAt = null
        ),
        Transaction(
            id = "preview-income",
            type = TransactionType.INCOME,
            title = "월급",
            amount = 2_800_000L,
            category = "월급",
            date = "2026-06-25",
            memo = null,
            receiptImagePath = null,
            createdAt = 0L,
            updatedAt = null
        )
    )

    MaterialTheme {
        Surface {
            HomeScreen(
                uiState = HomeUiState(
                    selectedMonthKey = "2026-06",
                    monthLabel = "2026년 6월",
                    incomeTransactions = transactions.filter { it.type == TransactionType.INCOME },
                    expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE },
                    summary = MonthlySummary(
                        income = 2_800_000L,
                        expense = 126_000L,
                        balance = 2_674_000L
                    )
                ),
                onPreviousMonth = {},
                onNextMonth = {},
                onAddExpense = {},
                onAddIncome = {},
                onHome = {},
                onShowHistory = {},
                onOpenSettings = {},
                onMonthSelected = {},
                onExportMonthlyPdf = {},
                onDismissPdfPremiumDialog = {},
                onPdfShareIntentHandled = {},
                onPdfOpenFailed = {},
                onTransactionClick = {}
            )
        }
    }
}
