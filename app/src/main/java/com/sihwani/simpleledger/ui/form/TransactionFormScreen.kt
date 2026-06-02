package com.sihwani.simpleledger.ui.form

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sihwani.simpleledger.domain.model.Account
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.util.AccountFormatter
import com.sihwani.simpleledger.util.DateUtils
import java.io.File

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormScreen(
    uiState: TransactionFormUiState,
    onAmountChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7F9))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        FormHeader(
            title = uiState.screenTitle,
            onBack = onBack
        )

        if (uiState.isLoading) {
            Text(
                text = "내역을 불러오는 중입니다.",
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
            return@Column
        }

        if (uiState.notFound) {
            Text(
                text = "내역을 찾을 수 없습니다.",
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
            return@Column
        }

        OutlinedTextField(
            value = uiState.amountText,
            onValueChange = onAmountChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "금액") },
            trailingIcon = { Text(text = "원") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = uiState.titleLabel) },
            singleLine = true,
            maxLines = 1,
            shape = RoundedCornerShape(12.dp)
        )

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
                uiState.categories.forEach { category ->
                    FilterChip(
                        selected = uiState.category == category,
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

        AccountSelectionSection(
            accounts = uiState.accountOptions,
            selectedAccountId = uiState.selectedAccountId,
            onAccountChange = onAccountChange
        )

        OutlinedTextField(
            value = uiState.date,
            onValueChange = onDateChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "날짜") },
            supportingText = { Text(text = "YYYY-MM-DD") },
            trailingIcon = {
                TextButton(onClick = { showDatePicker = true }) {
                    Text(text = "선택")
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = uiState.memo,
            onValueChange = onMemoChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "메모") },
            minLines = 4,
            maxLines = 6,
            shape = RoundedCornerShape(12.dp)
        )

        if (uiState.showReceiptSection) {
            ReceiptImageSection(
                previewSource = uiState.receiptPreviewSource,
                onPickImage = {
                    receiptImagePicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRemoveImage = onReceiptImageRemove
            )
        }

        uiState.errorMessage?.let { message ->
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

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = onSave,
            enabled = !uiState.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18181B))
        ) {
            Text(
                text = if (uiState.isSaving) "저장 중" else "저장",
                fontWeight = FontWeight.ExtraBold
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
                onAmountChange = {},
                onTitleChange = {},
                onCategoryChange = {},
                onDateChange = {},
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
