package com.sihwani.simpleledger.ui.recurring

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sihwani.simpleledger.domain.layout.ScreenLayoutPreference
import com.sihwani.simpleledger.domain.model.Account
import com.sihwani.simpleledger.domain.model.RecurringRepeatType
import com.sihwani.simpleledger.domain.model.RecurringTransaction
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.domain.premium.PremiumPolicy
import com.sihwani.simpleledger.ui.adaptive.AdaptiveLayoutDefaults
import com.sihwani.simpleledger.ui.adaptive.AdaptiveLayoutMode
import com.sihwani.simpleledger.ui.adaptive.resolveAdaptiveLayoutMode
import com.sihwani.simpleledger.util.AccountFormatter
import com.sihwani.simpleledger.util.DateUtils
import com.sihwani.simpleledger.util.MoneyFormatter

@Composable
fun RecurringTransactionScreen(
    uiState: RecurringTransactionUiState,
    screenLayoutPreference: ScreenLayoutPreference,
    onBack: () -> Unit,
    onAddRule: () -> Unit,
    onEditRule: (String) -> Unit,
    onRequestDeactivate: (String) -> Unit,
    onDismissDeactivate: () -> Unit,
    onConfirmDeactivate: () -> Unit,
    onToggleInactiveRules: () -> Unit,
    onRequestReactivate: (String) -> Unit,
    onDismissReactivate: () -> Unit,
    onConfirmReactivate: () -> Unit,
    onRequestDelete: (String) -> Unit,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissPremiumDialog: () -> Unit,
    onDismissForm: () -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onAmountChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onAccountChange: (String?) -> Unit,
    onRepeatTypeChange: (RecurringRepeatType) -> Unit,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onMemoChange: (String) -> Unit,
    onSaveRule: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7F9))
    ) {
        val adaptiveLayoutMode = resolveAdaptiveLayoutMode(
            preference = screenLayoutPreference,
            availableWidth = maxWidth
        )

        if (adaptiveLayoutMode == AdaptiveLayoutMode.WIDE) {
            WideRecurringTransactionContent(
                uiState = uiState,
                onBack = onBack,
                onAddRule = onAddRule,
                onEditRule = onEditRule,
                onRequestDeactivate = onRequestDeactivate,
                onToggleInactiveRules = onToggleInactiveRules,
                onRequestReactivate = onRequestReactivate,
                onRequestDelete = onRequestDelete
            )
        } else {
            CompactRecurringTransactionContent(
                uiState = uiState,
                onBack = onBack,
                onAddRule = onAddRule,
                onEditRule = onEditRule,
                onRequestDeactivate = onRequestDeactivate,
                onToggleInactiveRules = onToggleInactiveRules,
                onRequestReactivate = onRequestReactivate,
                onRequestDelete = onRequestDelete
            )
        }
    }

    uiState.form?.let { form ->
        RecurringRuleFormDialog(
            form = form,
            accounts = uiState.accounts,
            onDismiss = onDismissForm,
            onTypeChange = onTypeChange,
            onAmountChange = onAmountChange,
            onTitleChange = onTitleChange,
            onCategoryChange = onCategoryChange,
            onAccountChange = onAccountChange,
            onRepeatTypeChange = onRepeatTypeChange,
            onStartDateChange = onStartDateChange,
            onEndDateChange = onEndDateChange,
            onMemoChange = onMemoChange,
            onSave = onSaveRule
        )
    }

    uiState.ruleIdPendingDeactivate?.let {
        ConfirmRecurringDialog(
            title = "반복 거래 비활성화",
            message = "이 반복 거래를 비활성화할까요?\n\n이미 생성된 거래는 유지되며, 이후 새 예정 거래 생성만 멈춥니다.",
            confirmText = "비활성화",
            danger = true,
            onDismiss = onDismissDeactivate,
            onConfirm = onConfirmDeactivate
        )
    }

    uiState.ruleIdPendingReactivate?.let {
        ConfirmRecurringDialog(
            title = "반복 거래 다시 사용",
            message = "이 반복 거래를 다시 사용하시겠습니까?\n\n오늘 기준 12개월 앞까지 예정 거래가 다시 생성됩니다.",
            confirmText = "다시 사용",
            danger = false,
            onDismiss = onDismissReactivate,
            onConfirm = onConfirmReactivate
        )
    }

    uiState.ruleIdPendingDelete?.let {
        ConfirmRecurringDialog(
            title = "반복 거래 삭제",
            message = "반복 거래 원본을 삭제할까요?\n\n이미 생성된 거래는 유지되며, 이 원본에서 새 거래는 더 이상 생성되지 않습니다.",
            confirmText = "삭제",
            danger = true,
            onDismiss = onDismissDelete,
            onConfirm = onConfirmDelete
        )
    }

    if (uiState.showPremiumDialog) {
        AlertDialog(
            onDismissRequest = onDismissPremiumDialog,
            title = { Text(text = "프리미엄 안내") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "반복 거래는 프리미엄 기능입니다.")
                    Text(text = "무료 사용자는 활성 반복 거래 ${PremiumPolicy.FreeRecurringRuleLimit}개까지 체험할 수 있습니다.")
                    Text(text = "프리미엄을 구매하면 여러 반복 거래를 등록하고 예정 거래를 미리 확인할 수 있습니다.")
                    Text(
                        text = "예상 가격: 1,500원 1회 구매",
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "※ 결제 기능은 출시 준비 단계에서 연결될 예정입니다.")
                }
            },
            confirmButton = {
                TextButton(onClick = onDismissPremiumDialog) {
                    Text(text = "확인")
                }
            }
        )
    }
}

@Composable
private fun CompactRecurringTransactionContent(
    uiState: RecurringTransactionUiState,
    onBack: () -> Unit,
    onAddRule: () -> Unit,
    onEditRule: (String) -> Unit,
    onRequestDeactivate: (String) -> Unit,
    onToggleInactiveRules: () -> Unit,
    onRequestReactivate: (String) -> Unit,
    onRequestDelete: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RecurringHeader(onBack = onBack)
        RecurringNoticeCard()

        uiState.message?.let { message ->
            MessageCard(message = message)
        }

        RecurringAddButton(onAddRule = onAddRule)
        RecurringLimitNotice(isPremium = uiState.isPremium)
        ActiveRecurringRulesSection(
            activeRules = uiState.activeRules,
            onEditRule = onEditRule,
            onRequestDeactivate = onRequestDeactivate,
            onRequestDelete = onRequestDelete
        )
        InactiveRecurringRulesSection(
            inactiveRules = uiState.inactiveRules,
            showInactiveRules = uiState.showInactiveRules,
            onToggleInactiveRules = onToggleInactiveRules,
            onEditRule = onEditRule,
            onRequestReactivate = onRequestReactivate,
            onRequestDelete = onRequestDelete
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun WideRecurringTransactionContent(
    uiState: RecurringTransactionUiState,
    onBack: () -> Unit,
    onAddRule: () -> Unit,
    onEditRule: (String) -> Unit,
    onRequestDeactivate: (String) -> Unit,
    onToggleInactiveRules: () -> Unit,
    onRequestReactivate: (String) -> Unit,
    onRequestDelete: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = AdaptiveLayoutDefaults.WideContentMaxWidth)
                .fillMaxSize(),
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
                RecurringHeader(onBack = onBack)
                RecurringOverviewCard(uiState = uiState)

                uiState.message?.let { message ->
                    MessageCard(message = message)
                }

                RecurringAddButton(onAddRule = onAddRule)
                RecurringLimitNotice(isPremium = uiState.isPremium)
                RecurringNoticeCard()
                RecurringDifferenceCard()
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
                ActiveRecurringRulesSection(
                    activeRules = uiState.activeRules,
                    onEditRule = onEditRule,
                    onRequestDeactivate = onRequestDeactivate,
                    onRequestDelete = onRequestDelete
                )
                InactiveRecurringRulesSection(
                    inactiveRules = uiState.inactiveRules,
                    showInactiveRules = uiState.showInactiveRules,
                    onToggleInactiveRules = onToggleInactiveRules,
                    onEditRule = onEditRule,
                    onRequestReactivate = onRequestReactivate,
                    onRequestDelete = onRequestDelete
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun RecurringOverviewCard(
    uiState: RecurringTransactionUiState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "활성 반복 거래",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4D4D8)
            )
            Text(
                text = "${uiState.activeRules.size}개",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "비활성 반복 거래 ${uiState.inactiveRules.size}개",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4D4D8)
            )
            Text(
                text = if (uiState.isPremium) {
                    "프리미엄 상태에서는 여러 반복 거래를 등록할 수 있습니다."
                } else {
                    "무료 상태에서는 활성 반복 거래 ${PremiumPolicy.FreeRecurringRuleLimit}개까지 체험할 수 있습니다."
                },
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4D4D8)
            )
        }
    }
}

@Composable
private fun RecurringAddButton(
    onAddRule: () -> Unit
) {
    Button(
        onClick = onAddRule,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18181B))
    ) {
        Text(
            text = "반복 거래 추가",
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun RecurringLimitNotice(
    isPremium: Boolean
) {
    if (!isPremium) {
        Text(
            text = "무료 상태에서는 활성 반복 거래 ${PremiumPolicy.FreeRecurringRuleLimit}개까지 체험할 수 있습니다.",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF71717A)
        )
    }
}

@Composable
private fun RecurringDifferenceCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "예정 거래와 반복 거래",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF18181B)
            )
            Text(
                text = "예정 거래는 한 번만 반영될 내역이고, 반복 거래는 정해진 주기마다 예정 거래를 자동으로 만듭니다.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF71717A)
            )
        }
    }
}

@Composable
private fun ActiveRecurringRulesSection(
    activeRules: List<RecurringRuleItem>,
    onEditRule: (String) -> Unit,
    onRequestDeactivate: (String) -> Unit,
    onRequestDelete: (String) -> Unit
) {
    SectionTitle(text = "활성 반복 거래")

    if (activeRules.isEmpty()) {
        EmptyRecurringCard()
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            activeRules.forEach { item ->
                RecurringRuleCard(
                    item = item,
                    statusText = null,
                    onEdit = { onEditRule(item.rule.id) },
                    onDeactivate = { onRequestDeactivate(item.rule.id) },
                    onReactivate = null,
                    onDelete = { onRequestDelete(item.rule.id) }
                )
            }
        }
    }
}

@Composable
private fun InactiveRecurringRulesSection(
    inactiveRules: List<RecurringRuleItem>,
    showInactiveRules: Boolean,
    onToggleInactiveRules: () -> Unit,
    onEditRule: (String) -> Unit,
    onRequestReactivate: (String) -> Unit,
    onRequestDelete: (String) -> Unit
) {
    if (inactiveRules.isEmpty()) {
        return
    }

    OutlinedButton(
        onClick = onToggleInactiveRules,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = if (showInactiveRules) {
                "비활성 반복 거래 숨기기"
            } else {
                "비활성 반복 거래 ${inactiveRules.size}개 보기"
            },
            fontWeight = FontWeight.ExtraBold
        )
    }

    if (showInactiveRules) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(text = "비활성 반복 거래")
            inactiveRules.forEach { item ->
                RecurringRuleCard(
                    item = item,
                    statusText = "비활성 반복 거래",
                    onEdit = { onEditRule(item.rule.id) },
                    onDeactivate = null,
                    onReactivate = { onRequestReactivate(item.rule.id) },
                    onDelete = { onRequestDelete(item.rule.id) }
                )
            }
        }
    }
}

@Composable
private fun RecurringHeader(
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
            text = "반복 거래 관리",
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF18181B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RecurringNoticeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "반복 거래는 예정 거래를 미리 생성합니다.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF18181B)
            )
            Text(
                text = "예정 거래는 날짜가 도래하기 전까지 계좌 계산 잔액에는 반영되지 않습니다.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF71717A)
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF18181B)
    )
}

@Composable
private fun MessageCard(message: String) {
    Text(
        text = message,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF4F4F5),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF18181B)
    )
}

@Composable
private fun EmptyRecurringCard() {
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
                text = "등록된 반복 거래가 없습니다.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF18181B)
            )
            Text(
                text = "매월 보험료, 매년 회비처럼 반복되는 내역을 등록해 예정 거래로 확인할 수 있습니다.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF71717A)
            )
        }
    }
}

@Composable
private fun RecurringRuleCard(
    item: RecurringRuleItem,
    statusText: String?,
    onEdit: () -> Unit,
    onDeactivate: (() -> Unit)?,
    onReactivate: (() -> Unit)?,
    onDelete: () -> Unit
) {
    val amountColor = when (item.rule.type) {
        TransactionType.INCOME -> Color(0xFF047857)
        TransactionType.EXPENSE -> Color(0xFFBE123C)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            statusText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF71717A)
                )
            }
            Text(
                text = item.rule.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF18181B),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${typeLabel(item.rule.type)} · ${repeatTypeLabel(item.rule.repeatType)} · ${MoneyFormatter.formatWon(item.rule.amount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                color = amountColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "계좌/지갑 ${item.accountLabel}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF71717A),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "다음 예정일 ${item.nextOccurrenceLabel}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF18181B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            item.rule.memo?.takeIf { it.isNotBlank() }?.let { memo ->
                Text(
                    text = memo,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF71717A),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "수정")
                }
                onDeactivate?.let {
                    OutlinedButton(
                        onClick = it,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "비활성화")
                    }
                }
                onReactivate?.let {
                    OutlinedButton(
                        onClick = it,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "다시 사용")
                    }
                }
            }
            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
            ) {
                Text(
                    text = "원본 삭제",
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun ConfirmRecurringDialog(
    title: String,
    message: String,
    confirmText: String,
    danger: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = if (danger) {
                    ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                } else {
                    ButtonDefaults.buttonColors()
                }
            ) {
                Text(text = confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "취소")
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun RecurringRuleFormDialog(
    form: RecurringRuleFormUiState,
    accounts: List<Account>,
    onDismiss: () -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onAmountChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onAccountChange: (String?) -> Unit,
    onRepeatTypeChange: (RecurringRepeatType) -> Unit,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onMemoChange: (String) -> Unit,
    onSave: () -> Unit
) {
    var showStartDatePicker by rememberSaveable { mutableStateOf(false) }
    var showEndDatePicker by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {
            if (!form.isSaving) {
                onDismiss()
            }
        },
        title = { Text(text = if (form.isEditMode) "반복 거래 수정" else "반복 거래 추가") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ChipGroupTitle(text = "종류")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TransactionType.entries.forEach { type ->
                        FilterChip(
                            selected = form.type == type,
                            onClick = { onTypeChange(type) },
                            label = { Text(text = typeLabel(type)) }
                        )
                    }
                }

                OutlinedTextField(
                    value = form.amountText,
                    onValueChange = onAmountChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "금액") },
                    trailingIcon = { Text(text = "원") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = form.title,
                    onValueChange = onTitleChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "제목") },
                    singleLine = true
                )

                ChipGroupTitle(text = "카테고리")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    form.categories.forEach { category ->
                        FilterChip(
                            selected = form.category == category,
                            onClick = { onCategoryChange(category) },
                            label = {
                                Text(
                                    text = category,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }

                ChipGroupTitle(text = "계좌/지갑")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = form.accountId == null,
                        onClick = { onAccountChange(null) },
                        label = { Text(text = "선택 안 함") }
                    )
                    accounts.forEach { account ->
                        FilterChip(
                            selected = form.accountId == account.id,
                            onClick = { onAccountChange(account.id) },
                            label = {
                                Text(
                                    text = AccountFormatter.displayName(account),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }

                ChipGroupTitle(text = "반복 주기")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RecurringRepeatType.entries.forEach { repeatType ->
                        FilterChip(
                            selected = form.repeatType == repeatType,
                            onClick = { onRepeatTypeChange(repeatType) },
                            label = { Text(text = repeatTypeLabel(repeatType)) }
                        )
                    }
                }

                Text(
                    text = "시작 날짜의 일자와 월을 기준으로 반복됩니다.",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF71717A)
                )

                OutlinedTextField(
                    value = form.startDate,
                    onValueChange = onStartDateChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "시작 날짜") },
                    supportingText = { Text(text = "YYYY-MM-DD") },
                    trailingIcon = {
                        TextButton(onClick = { showStartDatePicker = true }) {
                            Text(text = "선택")
                        }
                    },
                    singleLine = true
                )
                OutlinedTextField(
                    value = form.endDate,
                    onValueChange = onEndDateChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "종료 날짜") },
                    supportingText = { Text(text = "비워두면 계속 반복 · YYYY-MM-DD") },
                    trailingIcon = {
                        TextButton(onClick = { showEndDatePicker = true }) {
                            Text(text = "선택")
                        }
                    },
                    singleLine = true
                )
                OutlinedTextField(
                    value = form.memo,
                    onValueChange = onMemoChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "메모") },
                    minLines = 3,
                    maxLines = 5
                )

                form.errorMessage?.let { message ->
                    Text(
                        text = message,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFFFF1F2),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBE123C)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = !form.isSaving
            ) {
                Text(text = if (form.isSaving) "저장 중" else "저장")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !form.isSaving
            ) {
                Text(text = "취소")
            }
        }
    )

    if (showStartDatePicker) {
        RecurringDatePickerDialog(
            initialDate = form.startDate,
            onDismiss = { showStartDatePicker = false },
            onConfirm = { dateIso ->
                onStartDateChange(dateIso)
                showStartDatePicker = false
            }
        )
    }

    if (showEndDatePicker) {
        RecurringDatePickerDialog(
            initialDate = form.endDate.ifBlank { form.startDate },
            onDismiss = { showEndDatePicker = false },
            onConfirm = { dateIso ->
                onEndDateChange(dateIso)
                showEndDatePicker = false
            }
        )
    }
}

@Composable
private fun ChipGroupTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF18181B)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecurringDatePickerDialog(
    initialDate: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val initialSelectedMillis = DateUtils.isoToPickerMillisOrNull(initialDate)
        ?: DateUtils.isoToPickerMillisOrNull(DateUtils.todayIso())
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { selectedMillis ->
                        onConfirm(DateUtils.pickerMillisToIso(selectedMillis))
                    }
                },
                enabled = datePickerState.selectedDateMillis != null
            ) {
                Text(text = "확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "취소")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

private fun typeLabel(type: TransactionType): String {
    return when (type) {
        TransactionType.INCOME -> "수입"
        TransactionType.EXPENSE -> "지출"
    }
}

private fun repeatTypeLabel(repeatType: RecurringRepeatType): String {
    return when (repeatType) {
        RecurringRepeatType.MONTHLY -> "매월"
        RecurringRepeatType.QUARTERLY -> "매분기"
        RecurringRepeatType.YEARLY -> "매년"
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun RecurringTransactionScreenPreview() {
    val account = Account(
        id = "account",
        name = "생활통장",
        bankName = "우리은행",
        identifier = "0369",
        baseBalance = 1_200_000L,
        baseDate = "2026-06-01",
        memo = null,
        isActive = true,
        createdAt = 0L,
        updatedAt = null
    )
    val rule = RecurringTransaction(
        id = "rule",
        title = "보험료",
        type = TransactionType.EXPENSE,
        amount = 120_000L,
        category = "생활",
        accountId = account.id,
        memo = "매월 자동 결제",
        repeatType = RecurringRepeatType.MONTHLY,
        repeatDay = 25,
        repeatMonth = 6,
        startDate = "2026-06-25",
        endDate = null,
        isActive = true,
        createdAt = 0L,
        updatedAt = null
    )

    MaterialTheme {
        Surface {
            RecurringTransactionScreen(
                uiState = RecurringTransactionUiState(
                    activeRules = listOf(
                        RecurringRuleItem(
                            rule = rule,
                            accountLabel = AccountFormatter.displayName(account),
                            nextOccurrenceLabel = "2026년 6월 25일"
                        )
                    ),
                    accounts = listOf(account)
                ),
                screenLayoutPreference = ScreenLayoutPreference.AUTO,
                onBack = {},
                onAddRule = {},
                onEditRule = {},
                onRequestDeactivate = {},
                onDismissDeactivate = {},
                onConfirmDeactivate = {},
                onToggleInactiveRules = {},
                onRequestReactivate = {},
                onDismissReactivate = {},
                onConfirmReactivate = {},
                onRequestDelete = {},
                onDismissDelete = {},
                onConfirmDelete = {},
                onDismissPremiumDialog = {},
                onDismissForm = {},
                onTypeChange = {},
                onAmountChange = {},
                onTitleChange = {},
                onCategoryChange = {},
                onAccountChange = {},
                onRepeatTypeChange = {},
                onStartDateChange = {},
                onEndDateChange = {},
                onMemoChange = {},
                onSaveRule = {}
            )
        }
    }
}
