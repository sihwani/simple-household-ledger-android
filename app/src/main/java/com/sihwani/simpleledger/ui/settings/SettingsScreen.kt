package com.sihwani.simpleledger.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sihwani.simpleledger.domain.premium.PremiumPolicy
import com.sihwani.simpleledger.ui.history.DataManagementSection
import com.sihwani.simpleledger.ui.history.DataManagementUiState

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    dataManagementUiState: DataManagementUiState,
    versionName: String,
    versionCode: Int,
    packageName: String,
    showDebugPremiumToggle: Boolean,
    onBack: () -> Unit,
    onOpenAccounts: () -> Unit,
    onDebugPremiumChange: (Boolean) -> Unit,
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsHeader(onBack = onBack)
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
        AccountManagementEntry(onOpenAccounts = onOpenAccounts)
        ReceiptSection(receiptImageCount = uiState.receiptImageCount)
        AppSettingsSection()
        PremiumSection(
            isPremium = uiState.isPremium,
            showDebugPremiumToggle = showDebugPremiumToggle,
            onDebugPremiumChange = onDebugPremiumChange
        )
        AppInfoSection(
            versionName = versionName,
            versionCode = versionCode,
            packageName = packageName
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SettingsHeader(
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
            text = "설정",
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
private fun PremiumSection(
    isPremium: Boolean,
    showDebugPremiumToggle: Boolean,
    onDebugPremiumChange: (Boolean) -> Unit
) {
    var showDetails by rememberSaveable { mutableStateOf(false) }

    SettingsCard(title = "프리미엄") {
        InfoRow(
            label = "현재 상태",
            value = if (isPremium) "프리미엄" else "무료"
        )
        Text(
            text = if (isPremium) {
                "광고 없이 모든 프리미엄 기능을 사용할 수 있습니다."
            } else {
                "광고 제거, PDF 무제한, 계좌/지갑 관리 기능을 사용할 수 있습니다."
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF71717A)
        )
        OutlinedButton(
            onClick = { showDetails = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "자세히 보기",
                fontWeight = FontWeight.ExtraBold
            )
        }

        if (showDebugPremiumToggle) {
            OutlinedButton(
                onClick = { onDebugPremiumChange(!isPremium) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isPremium) "개발용: 무료 상태로 전환" else "개발용: 프리미엄 상태로 전환",
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    if (showDetails) {
        PremiumDetailsDialog(
            onDismiss = { showDetails = false }
        )
    }
}

@Composable
private fun PremiumDetailsDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "프리미엄") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "한 번만 구매하면 더 깔끔하고 편리하게 사용할 수 있습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF71717A)
                )
                Text(
                    text = "예상 가격: 1,500원 1회 구매",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF047857)
                )

                SectionLabel(text = "현재 제공")
                PremiumPolicy.CurrentPremiumFeatures.forEach { feature ->
                    BenefitText(text = feature)
                }

                SectionLabel(text = "무료 제공")
                BenefitText(text = "PDF 내보내기 ${PremiumPolicy.FreePdfTrialLimit}회 체험")
                BenefitText(text = "계좌/지갑 ${PremiumPolicy.FreeAccountLimit}개 체험")

                SectionLabel(text = "향후 제공 예정")
                PremiumPolicy.PlannedPremiumFeatures.forEach { feature ->
                    BenefitText(text = feature)
                }

                Text(
                    text = "※ 결제 기능은 출시 준비 단계에서 연결될 예정입니다.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF4F4F5),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF71717A)
                )

                Button(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18181B))
                ) {
                    Text(
                        text = "구매 기능 준비 중",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "확인")
            }
        }
    )
}

@Composable
private fun AccountManagementEntry(
    onOpenAccounts: () -> Unit
) {
    SettingsCard(title = "계좌/지갑 관리") {
        Text(
            text = "직접 등록한 계좌/지갑과 거래 내역을 기준으로 계산 잔액을 확인합니다.",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF71717A)
        )
        OutlinedButton(
            onClick = onOpenAccounts,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "계좌/지갑 관리",
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun ReceiptSection(
    receiptImageCount: Int
) {
    SettingsCard(title = "영수증") {
        InfoRow(label = "현재 첨부된 사진", value = "${receiptImageCount}장")
        InfoRow(label = "파일 용량 제한", value = "1장당 최대 8MB")
        Text(
            text = "영수증 사진은 지출 내역의 보조 기록으로 첨부할 수 있습니다.",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF18181B)
        )
        Text(
            text = "사진 파일은 앱 내부 저장소에 보관됩니다.",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF71717A)
        )
        Text(
            text = "백업 파일에는 영수증 사진이 포함되지 않습니다.",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF71717A)
        )
    }
}

@Composable
private fun AppSettingsSection() {
    SettingsCard(title = "앱 설정") {
        InfoRow(label = "테마", value = "라이트 모드 고정")
        Text(
            text = "다크모드는 추후 지원 예정입니다.",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF71717A)
        )
    }
}

@Composable
private fun AppInfoSection(
    versionName: String,
    versionCode: Int,
    packageName: String
) {
    SettingsCard(title = "앱 정보") {
        InfoRow(label = "앱 이름", value = "한눈 가계부")
        InfoRow(label = "versionName", value = versionName)
        InfoRow(label = "versionCode", value = versionCode.toString())
        InfoRow(label = "package", value = packageName)
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
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
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF18181B)
            )
            content()
        }
    }
}

@Composable
private fun SectionLabel(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF18181B)
    )
}

@Composable
private fun BenefitText(
    text: String
) {
    Text(
        text = "• $text",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF18181B)
    )
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.9f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF71717A),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value,
            modifier = Modifier.weight(1.2f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF18181B),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun SettingsScreenPreview() {
    MaterialTheme {
        Surface {
            SettingsScreen(
                uiState = SettingsUiState(receiptImageCount = 3),
                dataManagementUiState = DataManagementUiState(),
                versionName = "0.1.0",
                versionCode = 1,
                packageName = "com.sihwani.simpleledger",
                showDebugPremiumToggle = true,
                onBack = {},
                onOpenAccounts = {},
                onDebugPremiumChange = {},
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
