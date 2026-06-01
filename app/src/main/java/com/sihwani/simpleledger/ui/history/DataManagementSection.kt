package com.sihwani.simpleledger.ui.history

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sihwani.simpleledger.data.backup.BackupJson

@Composable
fun DataManagementSection(
    uiState: DataManagementUiState,
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
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { onExportBackup(it.toString()) }
    }
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { onImportBackup(it.toString()) }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "데이터 관리",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF18181B)
            )
            Text(
                text = "백업 파일에는 거래 내역만 포함되며, 영수증 사진은 포함되지 않습니다.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF71717A)
            )

            uiState.message?.let { message ->
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

            Button(
                onClick = {
                    exportLauncher.launch(BackupJson.createFileName())
                },
                enabled = !uiState.isBusy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18181B))
            ) {
                Text(
                    text = if (uiState.isBusy) "처리 중" else "데이터 내보내기",
                    fontWeight = FontWeight.ExtraBold
                )
            }

            OutlinedButton(
                onClick = {
                    importLauncher.launch(
                        arrayOf(
                            "application/json",
                            "text/*",
                            "application/octet-stream"
                        )
                    )
                },
                enabled = !uiState.isBusy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "데이터 가져오기",
                    fontWeight = FontWeight.ExtraBold
                )
            }

            OutlinedButton(
                onClick = onRequestDeleteAll,
                enabled = !uiState.isBusy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
            ) {
                Text(
                    text = "전체 데이터 삭제",
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }

    if (uiState.showImportModeDialog) {
        AlertDialog(
            onDismissRequest = onDismissImportModeDialog,
            title = { Text(text = "데이터 가져오기") },
            text = {
                Text(
                    text = "백업 파일에서 거래 ${uiState.pendingImportCount}건을 찾았습니다. 가져온 백업을 어떻게 반영할까요?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onMergeImport,
                    enabled = !uiState.isBusy
                ) {
                    Text(text = "병합")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = onRequestReplaceImport,
                        enabled = !uiState.isBusy
                    ) {
                        Text(text = "교체")
                    }
                    TextButton(
                        onClick = onDismissImportModeDialog,
                        enabled = !uiState.isBusy
                    ) {
                        Text(text = "취소")
                    }
                }
            }
        )
    }

    if (uiState.showReplaceConfirmDialog) {
        AlertDialog(
            onDismissRequest = onDismissReplaceConfirmDialog,
            title = { Text(text = "백업 데이터로 교체") },
            text = {
                Text(text = "기존 거래 내역을 모두 지우고 백업 데이터로 교체하시겠습니까? 이 작업은 되돌릴 수 없습니다.")
            },
            confirmButton = {
                Button(
                    onClick = onConfirmReplaceImport,
                    enabled = !uiState.isBusy,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text(text = "교체")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissReplaceConfirmDialog,
                    enabled = !uiState.isBusy
                ) {
                    Text(text = "취소")
                }
            }
        )
    }

    if (uiState.showDeleteAllConfirmDialog) {
        AlertDialog(
            onDismissRequest = onDismissDeleteAllConfirmDialog,
            title = { Text(text = "전체 데이터 삭제") },
            text = {
                Text(text = "정말 모든 거래 내역을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.")
            },
            confirmButton = {
                Button(
                    onClick = onConfirmDeleteAll,
                    enabled = !uiState.isBusy,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text(text = "삭제")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissDeleteAllConfirmDialog,
                    enabled = !uiState.isBusy
                ) {
                    Text(text = "취소")
                }
            }
        )
    }
}
