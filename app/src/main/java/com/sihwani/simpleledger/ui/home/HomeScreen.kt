package com.sihwani.simpleledger.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sihwani.simpleledger.domain.layout.ScreenLayoutPreference
import com.sihwani.simpleledger.domain.model.MonthlySummary
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionStatus
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.ui.adaptive.AdaptiveLayoutDefaults
import com.sihwani.simpleledger.ui.adaptive.AdaptiveLayoutMode
import com.sihwani.simpleledger.ui.adaptive.resolveAdaptiveLayoutMode
import com.sihwani.simpleledger.ui.ads.TopBannerAd
import com.sihwani.simpleledger.util.DateUtils
import com.sihwani.simpleledger.util.MoneyFormatter

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    screenLayoutPreference: ScreenLayoutPreference,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddExpense: () -> Unit,
    onAddIncome: () -> Unit,
    onHome: () -> Unit,
    onShowHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAccounts: () -> Unit,
    onMonthSelected: (String) -> Unit,
    onTransactionClick: (String) -> Unit,
    isPremium: Boolean,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF6F7F9),
        bottomBar = {
            Surface(color = Color(0xFFF6F7F9)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    HomeActionBar(
                        onAddExpense = onAddExpense,
                        onHome = onHome,
                        onAddIncome = onAddIncome,
                        modifier = Modifier
                            .widthIn(max = AdaptiveLayoutDefaults.BottomBarMaxWidth)
                            .fillMaxWidth()
                    )
                }
            }
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F7F9))
                .padding(innerPadding)
        ) {
            val adaptiveLayoutMode = resolveAdaptiveLayoutMode(
                preference = screenLayoutPreference,
                availableWidth = maxWidth
            )

            if (adaptiveLayoutMode == AdaptiveLayoutMode.WIDE) {
                WideHomeContent(
                    uiState = uiState,
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth,
                    onShowHistory = onShowHistory,
                    onOpenSettings = onOpenSettings,
                    onOpenAccounts = onOpenAccounts,
                    onMonthSelected = onMonthSelected,
                    onTransactionClick = onTransactionClick,
                    isPremium = isPremium
                )
            } else {
                CompactHomeContent(
                    uiState = uiState,
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth,
                    onShowHistory = onShowHistory,
                    onOpenSettings = onOpenSettings,
                    onOpenAccounts = onOpenAccounts,
                    onMonthSelected = onMonthSelected,
                    onTransactionClick = onTransactionClick,
                    isPremium = isPremium
                )
            }
        }
    }
}

@Composable
private fun CompactHomeContent(
    uiState: HomeUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onShowHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAccounts: () -> Unit,
    onMonthSelected: (String) -> Unit,
    onTransactionClick: (String) -> Unit,
    isPremium: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = AdaptiveLayoutDefaults.CompactContentMaxWidth)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HomeHeader(onOpenSettings = onOpenSettings)
            TopBannerAd(isPremium = isPremium)
            MonthSelector(
                selectedMonthKey = uiState.selectedMonthKey,
                monthLabel = uiState.monthLabel,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                onMonthSelected = onMonthSelected
            )
            SummaryCard(summary = uiState.summary)
            AccountSummaryCard(
                accountSummary = uiState.accountSummary,
                onOpenAccounts = onOpenAccounts
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
}

@Composable
private fun WideHomeContent(
    uiState: HomeUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onShowHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAccounts: () -> Unit,
    onMonthSelected: (String) -> Unit,
    onTransactionClick: (String) -> Unit,
    isPremium: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = AdaptiveLayoutDefaults.WideContentMaxWidth)
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HomeHeader(onOpenSettings = onOpenSettings)
            TopBannerAd(
                isPremium = isPremium,
                modifier = Modifier
                    .widthIn(max = AdaptiveLayoutDefaults.CompactContentMaxWidth)
                    .fillMaxWidth()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MonthSelector(
                        selectedMonthKey = uiState.selectedMonthKey,
                        monthLabel = uiState.monthLabel,
                        onPreviousMonth = onPreviousMonth,
                        onNextMonth = onNextMonth,
                        onMonthSelected = onMonthSelected
                    )
                    SummaryCard(summary = uiState.summary)
                    AccountSummaryCard(
                        accountSummary = uiState.accountSummary,
                        onOpenAccounts = onOpenAccounts
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HistoryButton(onShowHistory = onShowHistory)
                    TransactionColumns(
                        expenseTransactions = uiState.expenseTransactions,
                        incomeTransactions = uiState.incomeTransactions,
                        onTransactionClick = onTransactionClick
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
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
                text = "이번 달 정산",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4D4D8)
            )
            Text(
                text = MoneyFormatter.formatSignedWon(summary.balance),
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
            Text(
                text = monthlySettlementDescription(summary.balance),
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4D4D8)
            )
        }
    }
}

private fun monthlySettlementDescription(amount: Long): String {
    return when {
        amount > 0L -> "이번 달은 ${MoneyFormatter.formatWon(amount)} 남았습니다."
        amount < 0L -> "이번 달은 ${MoneyFormatter.formatWon(-amount)} 초과 지출했습니다."
        else -> "이번 달 수입과 지출이 같습니다."
    }
}

@Composable
private fun AccountSummaryCard(
    accountSummary: HomeAccountSummaryUiState,
    onOpenAccounts: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "계좌/지갑",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF18181B)
            )

            if (accountSummary.hasActiveAccounts) {
                Text(
                    text = "총 계산 잔액 ${MoneyFormatter.formatWon(accountSummary.totalCalculatedBalance)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF18181B),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${accountSummary.scheduledMonthLabel} 예정 수입 +${MoneyFormatter.formatWon(accountSummary.scheduledIncome)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF047857),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${accountSummary.scheduledMonthLabel} 예정 지출 -${MoneyFormatter.formatWon(accountSummary.scheduledExpense)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBE123C),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${accountSummary.scheduledMonthLabel} 말 예상 잔액 ${MoneyFormatter.formatWon(accountSummary.expectedMonthEndBalance)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF18181B),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    accountSummary.previewAccounts.forEach { account ->
                        AccountPreviewRow(account = account)
                    }
                    if (accountSummary.hiddenAccountCount > 0) {
                        Text(
                            text = "외 ${accountSummary.hiddenAccountCount}개 계좌",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF71717A)
                        )
                    }
                }

                OutlinedButton(
                    onClick = onOpenAccounts,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "전체 계좌 보기",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            } else {
                Text(
                    text = "계좌를 등록하면 거래와 연결해 계산 잔액을 확인할 수 있습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF71717A)
                )

                Button(
                    onClick = onOpenAccounts,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18181B))
                ) {
                    Text(
                        text = "계좌 등록하기",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountPreviewRow(
    account: HomeAccountBalanceItem
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = account.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3F3F46),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = MoneyFormatter.formatWon(account.calculatedBalance),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.End,
            color = Color(0xFF18181B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
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
    val amountText = if (transaction.transactionStatus == TransactionStatus.SCHEDULED) {
        "${MoneyFormatter.formatWon(transaction.amount)} · 예정"
    } else {
        MoneyFormatter.formatWon(transaction.amount)
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
                text = amountText,
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
                    accountSummary = HomeAccountSummaryUiState(
                        totalCalculatedBalance = 3_250_000L,
                        previewAccounts = listOf(
                            HomeAccountBalanceItem(
                                id = "account-1",
                                name = "생활통장",
                                calculatedBalance = 1_150_000L,
                                expectedMonthEndBalance = 1_030_000L
                            ),
                            HomeAccountBalanceItem(
                                id = "account-2",
                                name = "월급통장",
                                calculatedBalance = 2_000_000L,
                                expectedMonthEndBalance = 2_000_000L
                            )
                        ),
                        scheduledIncome = 0L,
                        scheduledExpense = 120_000L,
                        expectedMonthEndBalance = 3_130_000L,
                        scheduledMonthLabel = "2026년 6월",
                        hiddenAccountCount = 1,
                        hasActiveAccounts = true
                    ),
                    summary = MonthlySummary(
                        income = 2_800_000L,
                        expense = 126_000L,
                        balance = 2_674_000L
                    )
                ),
                screenLayoutPreference = ScreenLayoutPreference.AUTO,
                onPreviousMonth = {},
                onNextMonth = {},
                onAddExpense = {},
                onAddIncome = {},
                onHome = {},
                onShowHistory = {},
                onOpenSettings = {},
                onOpenAccounts = {},
                onMonthSelected = {},
                onTransactionClick = {},
                isPremium = false,
            )
        }
    }
}
