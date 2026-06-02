package com.sihwani.simpleledger.ui.account

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sihwani.simpleledger.domain.model.Account
import com.sihwani.simpleledger.domain.premium.PremiumPolicy
import com.sihwani.simpleledger.util.AccountFormatter
import com.sihwani.simpleledger.util.DateUtils
import com.sihwani.simpleledger.util.MoneyFormatter

@Composable
fun AccountManagementScreen(
    uiState: AccountManagementUiState,
    onBack: () -> Unit,
    onAddAccount: () -> Unit,
    onEditAccount: (String) -> Unit,
    onRequestDeactivate: (String) -> Unit,
    onDismissDeactivate: () -> Unit,
    onConfirmDeactivate: () -> Unit,
    onToggleInactiveAccounts: () -> Unit,
    onRequestReactivate: (String) -> Unit,
    onDismissReactivate: () -> Unit,
    onConfirmReactivate: () -> Unit,
    onRequestDelete: (String) -> Unit,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissPremiumDialog: () -> Unit,
    onDismissForm: () -> Unit,
    onNameChange: (String) -> Unit,
    onBankNameChange: (String) -> Unit,
    onIdentifierChange: (String) -> Unit,
    onBaseBalanceChange: (String) -> Unit,
    onBaseDateChange: (String) -> Unit,
    onMemoChange: (String) -> Unit,
    onSaveAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7F9))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AccountHeader(onBack = onBack)
        AccountNoticeCard()

        uiState.message?.let { message ->
            MessageCard(message = message)
        }

        Button(
            onClick = onAddAccount,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18181B))
        ) {
            Text(
                text = "계좌/지갑 추가",
                fontWeight = FontWeight.ExtraBold
            )
        }

        if (!uiState.isPremium) {
            Text(
                text = "무료 상태에서는 활성 계좌/지갑 ${PremiumPolicy.FreeAccountLimit}개까지 체험할 수 있습니다.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF71717A)
            )
        }

        Text(
            text = "활성 계좌",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF18181B)
        )

        if (uiState.accounts.isEmpty()) {
            EmptyAccountCard()
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                uiState.accounts.forEach { item ->
                    AccountCard(
                        item = item,
                        statusText = null,
                        onEdit = { onEditAccount(item.account.id) },
                        onDeactivate = { onRequestDeactivate(item.account.id) },
                        onReactivate = null,
                        onDelete = { onRequestDelete(item.account.id) }
                    )
                }
            }
        }

        if (uiState.inactiveAccounts.isNotEmpty()) {
            OutlinedButton(
                onClick = onToggleInactiveAccounts,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (uiState.showInactiveAccounts) {
                        "비활성 계좌 숨기기"
                    } else {
                        "비활성 계좌 ${uiState.inactiveAccounts.size}개 보기"
                    },
                    fontWeight = FontWeight.ExtraBold
                )
            }

            if (uiState.showInactiveAccounts) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "비활성 계좌",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF71717A)
                    )
                    uiState.inactiveAccounts.forEach { item ->
                        AccountCard(
                            item = item,
                            statusText = "비활성 계좌",
                            onEdit = { onEditAccount(item.account.id) },
                            onDeactivate = null,
                            onReactivate = { onRequestReactivate(item.account.id) },
                            onDelete = { onRequestDelete(item.account.id) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    uiState.form?.let { form ->
        AccountFormDialog(
            form = form,
            onDismiss = onDismissForm,
            onNameChange = onNameChange,
            onBankNameChange = onBankNameChange,
            onIdentifierChange = onIdentifierChange,
            onBaseBalanceChange = onBaseBalanceChange,
            onBaseDateChange = onBaseDateChange,
            onMemoChange = onMemoChange,
            onSave = onSaveAccount
        )
    }

    uiState.accountIdPendingDeactivate?.let {
        ConfirmActionDialog(
            title = "계좌/지갑 비활성화",
            message = "이 계좌/지갑을 비활성화할까요?\n\n비활성화하면 새 거래 작성 시 선택 목록에는 표시되지 않습니다.\n이미 연결된 과거 거래에서는 계속 표시됩니다.",
            confirmText = "비활성화",
            danger = true,
            onDismiss = onDismissDeactivate,
            onConfirm = onConfirmDeactivate
        )
    }

    uiState.accountIdPendingReactivate?.let {
        ConfirmActionDialog(
            title = "계좌/지갑 다시 사용",
            message = "이 계좌/지갑을 다시 사용하시겠습니까?\n\n다시 사용하면 새 거래 작성 시 선택 목록에 표시됩니다.",
            confirmText = "다시 사용",
            danger = false,
            onDismiss = onDismissReactivate,
            onConfirm = onConfirmReactivate
        )
    }

    uiState.deleteDialog?.let { deleteDialog ->
        AccountDeleteDialog(
            deleteDialog = deleteDialog,
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
                    Text(text = "계좌/지갑 관리는 프리미엄 기능입니다.")
                    Text(text = "무료 사용자는 계좌/지갑 ${PremiumPolicy.FreeAccountLimit}개까지 체험할 수 있습니다.")
                    Text(text = "프리미엄을 구매하면 여러 계좌/지갑을 등록하고 거래와 연결해 계산 잔액을 확인할 수 있습니다.")
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
private fun AccountHeader(
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
            text = "계좌/지갑 관리",
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
private fun AccountNoticeCard() {
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
                text = "이 기능은 실제 은행 계좌와 연결되지 않습니다.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF18181B)
            )
            Text(
                text = "사용자가 직접 입력한 계좌/지갑 정보와 거래 내역을 기준으로 잔액을 계산합니다.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF71717A)
            )
            Text(
                text = "거래를 빠뜨리거나 실제 계좌와 다르게 입력하면 계산 잔액과 실제 잔액이 다를 수 있습니다.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF71717A)
            )
        }
    }
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
private fun EmptyAccountCard() {
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
                text = "등록된 계좌/지갑이 없습니다.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF18181B)
            )
            Text(
                text = "계좌/지갑을 추가하면 거래와 연결해 계산 잔액을 확인할 수 있습니다.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF71717A)
            )
        }
    }
}

@Composable
private fun AccountCard(
    item: AccountBalanceItem,
    statusText: String?,
    onEdit: () -> Unit,
    onDeactivate: (() -> Unit)?,
    onReactivate: (() -> Unit)?,
    onDelete: () -> Unit
) {
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
                text = AccountFormatter.displayName(item.account),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF18181B)
            )
            Text(
                text = "계산 잔액 ${MoneyFormatter.formatWon(item.calculatedBalance)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold,
                color = if (item.calculatedBalance >= 0L) Color(0xFF047857) else Color(0xFFBE123C),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "기준 ${item.account.baseDate} · 기준 잔액 ${MoneyFormatter.formatWon(item.account.baseBalance)}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF71717A)
            )
            item.account.memo?.takeIf { it.isNotBlank() }?.let { memo ->
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
                    text = "계좌 삭제",
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountFormDialog(
    form: AccountFormUiState,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    onBankNameChange: (String) -> Unit,
    onIdentifierChange: (String) -> Unit,
    onBaseBalanceChange: (String) -> Unit,
    onBaseDateChange: (String) -> Unit,
    onMemoChange: (String) -> Unit,
    onSave: () -> Unit
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {
            if (!form.isSaving) {
                onDismiss()
            }
        },
        title = { Text(text = if (form.isEditMode) "계좌/지갑 수정" else "계좌/지갑 추가") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = form.name,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "계좌/지갑 이름") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = form.bankName,
                    onValueChange = onBankNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "은행명") },
                    placeholder = { Text(text = "예: 우리은행, 현금") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = form.identifier,
                    onValueChange = onIdentifierChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "식별번호 또는 끝자리 4자리") },
                    supportingText = { Text(text = "계좌번호 전체는 입력하지 마세요.") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = form.baseBalanceText,
                    onValueChange = onBaseBalanceChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "기준 잔액") },
                    trailingIcon = { Text(text = "원") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = form.baseDate,
                    onValueChange = onBaseDateChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "기준 날짜") },
                    supportingText = { Text(text = "YYYY-MM-DD") },
                    trailingIcon = {
                        TextButton(onClick = { showDatePicker = true }) {
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

    if (showDatePicker) {
        AccountDatePickerDialog(
            initialDate = form.baseDate,
            onDismiss = { showDatePicker = false },
            onConfirm = { dateIso ->
                onBaseDateChange(dateIso)
                showDatePicker = false
            }
        )
    }
}

@Composable
private fun ConfirmActionDialog(
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

@Composable
private fun AccountDeleteDialog(
    deleteDialog: AccountDeleteDialogUiState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val message = if (deleteDialog.linkedTransactionCount == 0) {
        "이 계좌/지갑과 연결된 거래가 없습니다.\n삭제하면 계좌 목록에서 완전히 제거됩니다."
    } else {
        "이 계좌/지갑과 연결된 거래가 ${deleteDialog.linkedTransactionCount}건 있습니다.\n\n삭제해도 기존 거래 내역은 유지됩니다.\n단, 이 계좌/지갑은 더 이상 계좌 목록에서 관리할 수 없으며,\n기존 거래에는 삭제 당시의 계좌/지갑 정보만 표시됩니다.\n\n계속 삭제할까요?"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "계좌/지갑 삭제") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = deleteDialog.accountLabel,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(text = message)
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
            ) {
                Text(text = "삭제")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "취소")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountDatePickerDialog(
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

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun AccountManagementScreenPreview() {
    val account = Account(
        id = "account",
        name = "생활통장",
        bankName = "우리은행",
        identifier = "0369",
        baseBalance = 1_200_000L,
        baseDate = "2026-06-01",
        memo = "생활비 지출 계좌",
        isActive = true,
        createdAt = 0L,
        updatedAt = null
    )

    MaterialTheme {
        Surface {
            AccountManagementScreen(
                uiState = AccountManagementUiState(
                    accounts = listOf(
                        AccountBalanceItem(
                            account = account,
                            calculatedBalance = 1_150_000L
                        )
                    )
                ),
                onBack = {},
                onAddAccount = {},
                onEditAccount = {},
                onRequestDeactivate = {},
                onDismissDeactivate = {},
                onConfirmDeactivate = {},
                onToggleInactiveAccounts = {},
                onRequestReactivate = {},
                onDismissReactivate = {},
                onConfirmReactivate = {},
                onRequestDelete = {},
                onDismissDelete = {},
                onConfirmDelete = {},
                onDismissPremiumDialog = {},
                onDismissForm = {},
                onNameChange = {},
                onBankNameChange = {},
                onIdentifierChange = {},
                onBaseBalanceChange = {},
                onBaseDateChange = {},
                onMemoChange = {},
                onSaveAccount = {}
            )
        }
    }
}
