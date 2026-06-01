package com.sihwani.simpleledger.ui.detail

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.util.DateUtils
import com.sihwani.simpleledger.util.MoneyFormatter
import java.io.File

@Composable
fun TransactionDetailScreen(
    uiState: TransactionDetailUiState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var enlargedReceiptPath by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7F9))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        DetailHeader(onBack = onBack)

        when {
            uiState.isLoading -> DetailNotice(text = "내역을 불러오는 중입니다.")
            uiState.notFound -> DetailNotice(text = "내역을 찾을 수 없습니다.")
            uiState.transaction != null -> {
                TransactionDetailContent(
                    transaction = uiState.transaction,
                    onEdit = onEdit,
                    onDeleteClick = onDeleteClick,
                    onReceiptClick = { receiptPath -> enlargedReceiptPath = receiptPath }
                )
            }
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

        Spacer(modifier = Modifier.height(18.dp))
    }

    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = onDismissDelete,
            title = { Text(text = "내역 삭제") },
            text = {
                Text(text = "정말 이 내역을 삭제하시겠습니까? 삭제한 내역은 되돌릴 수 없습니다.")
            },
            confirmButton = {
                Button(
                    onClick = onConfirmDelete,
                    enabled = !uiState.isDeleting,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text(text = if (uiState.isDeleting) "삭제 중" else "삭제")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissDelete,
                    enabled = !uiState.isDeleting
                ) {
                    Text(text = "취소")
                }
            }
        )
    }

    enlargedReceiptPath?.let { receiptPath ->
        ReceiptImageViewerDialog(
            receiptImagePath = receiptPath,
            onDismiss = { enlargedReceiptPath = null }
        )
    }
}

@Composable
private fun DetailHeader(
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
            text = "상세 내역",
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
private fun TransactionDetailContent(
    transaction: Transaction,
    onEdit: () -> Unit,
    onDeleteClick: () -> Unit,
    onReceiptClick: (String) -> Unit
) {
    val amountColor = when (transaction.type) {
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
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = typeLabel(transaction.type),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = amountColor
                )
                Text(
                    text = transaction.title,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF18181B),
                    overflow = TextOverflow.Clip
                )
                Text(
                    text = MoneyFormatter.formatWon(transaction.amount),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = amountColor,
                    overflow = TextOverflow.Clip
                )
            }

            DetailRow(label = "날짜", value = DateUtils.formatFullDate(transaction.date))
            DetailRow(label = "카테고리", value = transaction.category)
            DetailRow(label = "메모", value = transaction.memo?.takeIf { it.isNotBlank() } ?: "메모 없음")

            transaction.receiptImagePath?.let { receiptImagePath ->
                ReceiptImageCard(
                    receiptImagePath = receiptImagePath,
                    onClick = { onReceiptClick(receiptImagePath) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18181B))
                ) {
                    Text(
                        text = "수정",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                OutlinedButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                ) {
                    Text(
                        text = "삭제",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ReceiptImageCard(
    receiptImagePath: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "영수증 사진",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF71717A)
        )
        AsyncImage(
            model = receiptImageModel(receiptImagePath),
            contentDescription = "영수증 사진",
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick),
            contentScale = ContentScale.Crop
        )
        Text(
            text = "사진을 눌러 크게 보기",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF71717A)
        )
    }
}

@Composable
private fun ReceiptImageViewerDialog(
    receiptImagePath: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6000000))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            ZoomableReceiptImage(
                receiptImagePath = receiptImagePath,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 64.dp, bottom = 24.dp)
            )
            Text(
                text = "손가락으로 확대/이동할 수 있습니다",
                modifier = Modifier.align(Alignment.TopStart),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(
                    text = "닫기",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ZoomableReceiptImage(
    receiptImagePath: String,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = receiptImageModel(receiptImagePath),
            contentDescription = "확대된 영수증 사진",
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(receiptImagePath) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val nextScale = (scale * zoom).coerceIn(1f, 5f)
                        val rawOffset = if (nextScale == 1f) {
                            Offset.Zero
                        } else {
                            offset + pan
                        }
                        val maxOffsetX = size.width * (nextScale - 1f) / 2f
                        val maxOffsetY = size.height * (nextScale - 1f) / 2f

                        scale = nextScale
                        offset = Offset(
                            x = rawOffset.x.coerceIn(-maxOffsetX, maxOffsetX),
                            y = rawOffset.y.coerceIn(-maxOffsetY, maxOffsetY)
                        )
                    }
                }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                },
            contentScale = ContentScale.Fit
        )
    }
}

private fun receiptImageModel(source: String): Any {
    return when {
        source.startsWith("content://") -> Uri.parse(source)
        else -> File(source)
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF71717A)
        )
        Text(
            text = value,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF18181B),
            overflow = TextOverflow.Clip
        )
    }
}

@Composable
private fun DetailNotice(
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

private fun typeLabel(type: TransactionType): String {
    return when (type) {
        TransactionType.INCOME -> "수입"
        TransactionType.EXPENSE -> "지출"
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun TransactionDetailScreenPreview() {
    MaterialTheme {
        Surface {
            TransactionDetailScreen(
                uiState = TransactionDetailUiState(
                    isLoading = false,
                    transaction = Transaction(
                        id = "preview",
                        type = TransactionType.EXPENSE,
                        title = "이마트 트레이더스에서 생활용품과 식료품을 한 번에 많이 구매한 내역",
                        amount = 12345678901,
                        category = "생활",
                        date = "2026-06-02",
                        memo = "긴 메모도 줄바꿈되면서 상세 화면 안에서 읽을 수 있게 표시합니다.",
                        receiptImagePath = null,
                        createdAt = 0L,
                        updatedAt = null
                    )
                ),
                onBack = {},
                onEdit = {},
                onDeleteClick = {},
                onDismissDelete = {},
                onConfirmDelete = {}
            )
        }
    }
}
