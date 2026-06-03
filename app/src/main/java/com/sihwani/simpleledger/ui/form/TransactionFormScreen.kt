package com.sihwani.simpleledger.ui.form

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.runtime.LaunchedEffect
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
import coil.compose.AsyncImage
import com.sihwani.simpleledger.domain.layout.ScreenLayoutPreference
import com.sihwani.simpleledger.domain.model.Account
import com.sihwani.simpleledger.domain.model.RecurringRepeatType
import com.sihwani.simpleledger.domain.model.TransactionStatus
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.domain.premium.PremiumPolicy
import com.sihwani.simpleledger.ui.adaptive.AdaptiveLayoutDefaults
import com.sihwani.simpleledger.ui.adaptive.AdaptiveLayoutMode
import com.sihwani.simpleledger.ui.adaptive.resolveAdaptiveLayoutMode
import com.sihwani.simpleledger.util.AccountFormatter
import com.sihwani.simpleledger.util.DateUtils
import java.io.File

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormScreen(
    uiState: TransactionFormUiState,
    screenLayoutPreference: ScreenLayoutPreference,
    onAmountChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onTransactionStatusChange: (TransactionStatus) -> Unit,
    onUseRecurringRuleChange: (Boolean) -> Unit,
    onRecurringRepeatTypeChange: (RecurringRepeatType) -> Unit,
    onRecurringEndDateChange: (String) -> Unit,
    onShowRecurringPremiumInfo: () -> Unit,
    onDismissRecurringPremiumInfo: () -> Unit,
    onMemoChange: (String) -> Unit,
    onAccountChange: (String?) -> Unit,
    onReceiptImageSelected: (String) -> Unit,
    onReceiptImageRemove: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onSaved: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showRecurringEndDatePicker by rememberSaveable { mutableStateOf(false) }
    val receiptImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onReceiptImageSelected(it.toString()) }
    }

    LaunchedEffect(uiState.saveCompleted) {
        if (uiState.saveCompleted) {
            onSaved(uiState.savedTransactionId)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7F9))
    ) {
        val adaptiveLayoutMode = resolveAdaptiveLayoutMode(
            preference = screenLayoutPreference,
            availableWidth = maxWidth
        )
        val onPickReceiptImage = {
            receiptImagePicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        if (adaptiveLayoutMode == AdaptiveLayoutMode.WIDE) {
            WideTransactionFormContent(
                uiState = uiState,
                onAmountChange = onAmountChange,
                onTitleChange = onTitleChange,
                onCategoryChange = onCategoryChange,
                onDateChange = onDateChange,
                onTransactionStatusChange = onTransactionStatusChange,
                onUseRecurringRuleChange = onUseRecurringRuleChange,
                onRecurringRepeatTypeChange = onRecurringRepeatTypeChange,
                onRecurringEndDateChange = onRecurringEndDateChange,
                onShowRecurringPremiumInfo = onShowRecurringPremiumInfo,
                onMemoChange = onMemoChange,
                onAccountChange = onAccountChange,
                onReceiptImageRemove = onReceiptImageRemove,
                onSave = onSave,
                onBack = onBack,
                onPickDate = { showDatePicker = true },
                onPickRecurringEndDate = { showRecurringEndDatePicker = true },
                onPickReceiptImage = onPickReceiptImage
            )
        } else {
            CompactTransactionFormContent(
                uiState = uiState,
                onAmountChange = onAmountChange,
                onTitleChange = onTitleChange,
                onCategoryChange = onCategoryChange,
                onDateChange = onDateChange,
                onTransactionStatusChange = onTransactionStatusChange,
                onUseRecurringRuleChange = onUseRecurringRuleChange,
                onRecurringRepeatTypeChange = onRecurringRepeatTypeChange,
                onRecurringEndDateChange = onRecurringEndDateChange,
                onShowRecurringPremiumInfo = onShowRecurringPremiumInfo,
                onMemoChange = onMemoChange,
                onAccountChange = onAccountChange,
                onReceiptImageRemove = onReceiptImageRemove,
                onSave = onSave,
                onBack = onBack,
                onPickDate = { showDatePicker = true },
                onPickRecurringEndDate = { showRecurringEndDatePicker = true },
                onPickReceiptImage = onPickReceiptImage
            )
        }
    }

    if (showDatePicker) {
        LedgerDatePickerDialog(
            initialDate = uiState.date,
            onDismiss = { showDatePicker = false },
            onConfirm = { dateIso ->
                onDateChange(dateIso)
                showDatePicker = false
            }
        )
    }

    if (showRecurringEndDatePicker) {
        LedgerDatePickerDialog(
            initialDate = uiState.recurringEndDate.ifBlank { uiState.date },
            onDismiss = { showRecurringEndDatePicker = false },
            onConfirm = { dateIso ->
                onRecurringEndDateChange(dateIso)
                showRecurringEndDatePicker = false
            }
        )
    }

    if (uiState.showRecurringPremiumDialog) {
        AlertDialog(
            onDismissRequest = onDismissRecurringPremiumInfo,
            title = { Text(text = "프리미엄 안내") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "반복 거래는 프리미엄 기능입니다.")
                    Text(text = "무료 사용자는 활성 반복 거래 ${PremiumPolicy.FreeRecurringRuleLimit}개까지 체험할 수 있습니다.")
                    Text(text = "프리미엄을 구매하면 여러 반복 거래를 등록하고 예정 거래를 자동으로 만들 수 있습니다.")
                    Text(
                        text = "예상 가격: 1,500원 1회 구매",
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "※ 결제 기능은 출시 준비 단계에서 연결될 예정입니다.")
                }
            },
            confirmButton = {
                TextButton(onClick = onDismissRecurringPremiumInfo) {
                    Text(text = "확인")
                }
            }
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun CompactTransactionFormContent(
    uiState: TransactionFormUiState,
    onAmountChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onTransactionStatusChange: (TransactionStatus) -> Unit,
    onUseRecurringRuleChange: (Boolean) -> Unit,
    onRecurringRepeatTypeChange: (RecurringRepeatType) -> Unit,
    onRecurringEndDateChange: (String) -> Unit,
    onShowRecurringPremiumInfo: () -> Unit,
    onMemoChange: (String) -> Unit,
    onAccountChange: (String?) -> Unit,
    onReceiptImageRemove: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onPickDate: () -> Unit,
    onPickRecurringEndDate: () -> Unit,
    onPickReceiptImage: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        FormHeader(
            title = uiState.screenTitle,
            onBack = onBack
        )

        when {
            uiState.isLoading -> FormInfoMessage(text = "내역을 불러오는 중입니다.")
            uiState.notFound -> FormInfoMessage(text = "내역을 찾을 수 없습니다.")
            else -> {
                PrimaryFormFields(
                    uiState = uiState,
                    dateBeforeCategory = false,
                    onAmountChange = onAmountChange,
                    onTitleChange = onTitleChange,
                    onCategoryChange = onCategoryChange,
                    onDateChange = onDateChange,
                    onAccountChange = onAccountChange,
                    onTransactionStatusChange = onTransactionStatusChange,
                    onPickDate = onPickDate
                )
                SecondaryFormFields(
                    uiState = uiState,
                    onUseRecurringRuleChange = onUseRecurringRuleChange,
                    onRecurringRepeatTypeChange = onRecurringRepeatTypeChange,
                    onRecurringEndDateChange = onRecurringEndDateChange,
                    onShowRecurringPremiumInfo = onShowRecurringPremiumInfo,
                    onMemoChange = onMemoChange,
                    onReceiptImageRemove = onReceiptImageRemove,
                    onSave = onSave,
                    onPickRecurringEndDate = onPickRecurringEndDate,
                    onPickReceiptImage = onPickReceiptImage
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun WideTransactionFormContent(
    uiState: TransactionFormUiState,
    onAmountChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onTransactionStatusChange: (TransactionStatus) -> Unit,
    onUseRecurringRuleChange: (Boolean) -> Unit,
    onRecurringRepeatTypeChange: (RecurringRepeatType) -> Unit,
    onRecurringEndDateChange: (String) -> Unit,
    onShowRecurringPremiumInfo: () -> Unit,
    onMemoChange: (String) -> Unit,
    onAccountChange: (String?) -> Unit,
    onReceiptImageRemove: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onPickDate: () -> Unit,
    onPickRecurringEndDate: () -> Unit,
    onPickReceiptImage: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = AdaptiveLayoutDefaults.WideContentMaxWidth)
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            FormHeader(
                title = uiState.screenTitle,
                onBack = onBack
            )

            when {
                uiState.isLoading -> FormInfoMessage(text = "내역을 불러오는 중입니다.")
                uiState.notFound -> FormInfoMessage(text = "내역을 찾을 수 없습니다.")
                else -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            PrimaryFormFields(
                                uiState = uiState,
                                dateBeforeCategory = true,
                                onAmountChange = onAmountChange,
                                onTitleChange = onTitleChange,
                                onCategoryChange = onCategoryChange,
                                onDateChange = onDateChange,
                                onAccountChange = onAccountChange,
                                onTransactionStatusChange = onTransactionStatusChange,
                                onPickDate = onPickDate
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            SecondaryFormFields(
                                uiState = uiState,
                                onUseRecurringRuleChange = onUseRecurringRuleChange,
                                onRecurringRepeatTypeChange = onRecurringRepeatTypeChange,
                                onRecurringEndDateChange = onRecurringEndDateChange,
                                onShowRecurringPremiumInfo = onShowRecurringPremiumInfo,
                                onMemoChange = onMemoChange,
                                onReceiptImageRemove = onReceiptImageRemove,
                                onSave = onSave,
                                onPickRecurringEndDate = onPickRecurringEndDate,
                                onPickReceiptImage = onPickReceiptImage
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun PrimaryFormFields(
    uiState: TransactionFormUiState,
    dateBeforeCategory: Boolean,
    onAmountChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onAccountChange: (String?) -> Unit,
    onTransactionStatusChange: (TransactionStatus) -> Unit,
    onPickDate: () -> Unit
) {
    AmountInputField(
        amountText = uiState.amountText,
        onAmountChange = onAmountChange
    )
    TitleInputField(
        title = uiState.title,
        titleLabel = uiState.titleLabel,
        onTitleChange = onTitleChange
    )
    if (dateBeforeCategory) {
        DateInputField(
            date = uiState.date,
            onDateChange = onDateChange,
            onPickDate = onPickDate
        )
    }
    CategorySelectionSection(
        categories = uiState.categories,
        selectedCategory = uiState.category,
        onCategoryChange = onCategoryChange
    )
    AccountSelectionSection(
        accounts = uiState.accountOptions,
        selectedAccountId = uiState.selectedAccountId,
        onAccountChange = onAccountChange
    )
    if (!dateBeforeCategory) {
        DateInputField(
            date = uiState.date,
            onDateChange = onDateChange,
            onPickDate = onPickDate
        )
    }
    TransactionStatusSection(
        uiState = uiState,
        onTransactionStatusChange = onTransactionStatusChange
    )
}

@Composable
private fun SecondaryFormFields(
    uiState: TransactionFormUiState,
    onUseRecurringRuleChange: (Boolean) -> Unit,
    onRecurringRepeatTypeChange: (RecurringRepeatType) -> Unit,
    onRecurringEndDateChange: (String) -> Unit,
    onShowRecurringPremiumInfo: () -> Unit,
    onMemoChange: (String) -> Unit,
    onReceiptImageRemove: () -> Unit,
    onSave: () -> Unit,
    onPickRecurringEndDate: () -> Unit,
    onPickReceiptImage: () -> Unit
) {
    RecurringRuleSection(
        uiState = uiState,
        onUseRecurringRuleChange = onUseRecurringRuleChange,
        onRepeatTypeChange = onRecurringRepeatTypeChange,
        onEndDateChange = onRecurringEndDateChange,
        onPickEndDate = onPickRecurringEndDate,
        onShowPremiumInfo = onShowRecurringPremiumInfo
    )

    MemoInputField(
        memo = uiState.memo,
        onMemoChange = onMemoChange
    )

    if (uiState.showReceiptSection) {
        ReceiptImageSection(
            previewSource = uiState.receiptPreviewSource,
            onPickImage = onPickReceiptImage,
            onRemoveImage = onReceiptImageRemove
        )
    }

    uiState.errorMessage?.let { message ->
        FormErrorMessage(message = message)
    }

    Spacer(modifier = Modifier.height(4.dp))
    SaveButton(
        isSaving = uiState.isSaving,
        onSave = onSave
    )
}

@Composable
private fun AmountInputField(
    amountText: String,
    onAmountChange: (String) -> Unit
) {
    OutlinedTextField(
        value = amountText,
        onValueChange = onAmountChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "금액") },
        trailingIcon = { Text(text = "원") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun TitleInputField(
    title: String,
    titleLabel: String,
    onTitleChange: (String) -> Unit
) {
    OutlinedTextField(
        value = title,
        onValueChange = onTitleChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = titleLabel) },
        singleLine = true,
        maxLines = 1,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun CategorySelectionSection(
    categories: List<String>,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "카테고리",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF18181B)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
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
    }
}

@Composable
private fun DateInputField(
    date: String,
    onDateChange: (String) -> Unit,
    onPickDate: () -> Unit
) {
    OutlinedTextField(
        value = date,
        onValueChange = onDateChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "날짜") },
        supportingText = { Text(text = "YYYY-MM-DD") },
        trailingIcon = {
            TextButton(onClick = onPickDate) {
                Text(text = "선택")
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun MemoInputField(
    memo: String,
    onMemoChange: (String) -> Unit
) {
    OutlinedTextField(
        value = memo,
        onValueChange = onMemoChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "메모") },
        minLines = 4,
        maxLines = 6,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun FormInfoMessage(
    text: String
) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 12.dp, vertical = 16.dp),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF71717A)
    )
}

@Composable
private fun FormErrorMessage(
    message: String
) {
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

@Composable
private fun SaveButton(
    isSaving: Boolean,
    onSave: () -> Unit
) {
    Button(
        onClick = onSave,
        enabled = !isSaving,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18181B))
    ) {
        Text(
            text = if (isSaving) "저장 중" else "저장",
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun AccountSelectionSection(
    accounts: List<Account>,
    selectedAccountId: String?,
    onAccountChange: (String?) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "계좌/지갑",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF18181B)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedAccountId == null,
                onClick = { onAccountChange(null) },
                label = {
                    Text(
                        text = "선택 안 함",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
            accounts.forEach { account ->
                FilterChip(
                    selected = selectedAccountId == account.id,
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
        if (accounts.isEmpty()) {
            Text(
                text = "설정에서 계좌/지갑을 추가하면 거래와 연결할 수 있습니다.",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF71717A)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun LedgerDatePickerDialog(
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

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun TransactionStatusSection(
    uiState: TransactionFormUiState,
    onTransactionStatusChange: (TransactionStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "예정 거래",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF18181B)
            )
            Text(
                text = if (uiState.useRecurringRule) {
                    "반복 거래는 날짜에 따라 예정 또는 실제 반영 거래로 자동 생성됩니다."
                } else {
                    "한 번만 예정된 수입/지출입니다. 지정한 날짜가 되면 실제 거래로 반영됩니다."
                },
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF71717A)
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.transactionStatus == TransactionStatus.POSTED,
                    enabled = !uiState.useRecurringRule,
                    onClick = { onTransactionStatusChange(TransactionStatus.POSTED) },
                    label = { Text(text = "실제 반영") }
                )
                FilterChip(
                    selected = uiState.transactionStatus == TransactionStatus.SCHEDULED,
                    enabled = !uiState.useRecurringRule,
                    onClick = { onTransactionStatusChange(TransactionStatus.SCHEDULED) },
                    label = { Text(text = "예정") }
                )
            }
            uiState.datePolicyNotice?.let { message ->
                Text(
                    text = message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF4F4F5),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF52525B)
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun RecurringRuleSection(
    uiState: TransactionFormUiState,
    onUseRecurringRuleChange: (Boolean) -> Unit,
    onRepeatTypeChange: (RecurringRepeatType) -> Unit,
    onEndDateChange: (String) -> Unit,
    onPickEndDate: () -> Unit,
    onShowPremiumInfo: () -> Unit
) {
    if (!uiState.showRecurringSection) {
        return
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "반복 거래",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF18181B)
                )
                Text(
                    text = if (uiState.isPremium) "프리미엄" else "무료 체험 1개",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF047857),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "매월, 매분기, 매년 자동으로 예정 거래를 만듭니다.",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF71717A)
            )

            if (uiState.isRecurringRuleLocked) {
                Text(
                    text = "무료 체험 한도를 모두 사용했습니다. 일반 예정 거래는 계속 무료로 사용할 수 있습니다.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF4F4F5),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF71717A)
                )
                OutlinedButton(
                    onClick = onShowPremiumInfo,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "프리미엄 보기",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                return@Column
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = !uiState.useRecurringRule,
                    onClick = { onUseRecurringRuleChange(false) },
                    label = { Text(text = "OFF") }
                )
                FilterChip(
                    selected = uiState.useRecurringRule,
                    onClick = { onUseRecurringRuleChange(true) },
                    label = { Text(text = "반복 거래로 등록") }
                )
            }

            if (uiState.useRecurringRule) {
                Text(
                    text = "반복 주기",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF18181B)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RecurringRepeatType.entries.forEach { repeatType ->
                        FilterChip(
                            selected = uiState.recurringRepeatType == repeatType,
                            onClick = { onRepeatTypeChange(repeatType) },
                            label = { Text(text = repeatTypeLabel(repeatType)) }
                        )
                    }
                }
                Text(
                    text = "작성한 날짜를 기준으로 반복됩니다. 종료일을 선택하지 않으면 계속 반복되며, 앱은 12개월 앞까지만 예정 거래를 만듭니다.",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF71717A)
                )
                OutlinedTextField(
                    value = uiState.recurringEndDate.ifBlank { "종료일 없음" },
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "종료일") },
                    readOnly = true,
                    trailingIcon = {
                        TextButton(onClick = onPickEndDate) {
                            Text(text = "선택")
                        }
                    },
                    singleLine = true
                )
                if (uiState.recurringEndDate.isNotBlank()) {
                    OutlinedButton(
                        onClick = { onEndDateChange("") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "종료일 없음으로 변경")
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptImageSection(
    previewSource: String?,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "영수증 사진",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF18181B)
            )

            previewSource?.let { source ->
                AsyncImage(
                    model = receiptImageModel(source),
                    contentDescription = "영수증 사진 미리보기",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            } ?: Text(
                text = "첨부된 영수증이 없습니다.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF71717A)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onPickImage,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = if (previewSource == null) "사진 선택" else "사진 변경")
                }

                if (previewSource != null) {
                    OutlinedButton(
                        onClick = onRemoveImage,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                    ) {
                        Text(text = "사진 삭제")
                    }
                }
            }
        }
    }
}

private fun repeatTypeLabel(repeatType: RecurringRepeatType): String {
    return when (repeatType) {
        RecurringRepeatType.MONTHLY -> "매월"
        RecurringRepeatType.QUARTERLY -> "매분기"
        RecurringRepeatType.YEARLY -> "매년"
    }
}

private fun receiptImageModel(source: String): Any {
    return when {
        source.startsWith("content://") -> Uri.parse(source)
        else -> File(source)
    }
}

@Composable
private fun FormHeader(
    title: String,
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
            text = title,
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF18181B)
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun TransactionFormScreenPreview() {
    MaterialTheme {
        Surface {
            TransactionFormScreen(
                uiState = TransactionFormUiState(type = TransactionType.EXPENSE),
                screenLayoutPreference = ScreenLayoutPreference.AUTO,
                onAmountChange = {},
                onTitleChange = {},
                onCategoryChange = {},
                onDateChange = {},
                onTransactionStatusChange = {},
                onUseRecurringRuleChange = {},
                onRecurringRepeatTypeChange = {},
                onRecurringEndDateChange = {},
                onShowRecurringPremiumInfo = {},
                onDismissRecurringPremiumInfo = {},
                onMemoChange = {},
                onAccountChange = {},
                onReceiptImageSelected = {},
                onReceiptImageRemove = {},
                onSave = {},
                onBack = {},
                onSaved = {}
            )
        }
    }
}
