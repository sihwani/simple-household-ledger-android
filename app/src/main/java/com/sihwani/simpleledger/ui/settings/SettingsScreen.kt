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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.sihwani.simpleledger.ui.history.DataManagementSection
import com.sihwani.simpleledger.ui.history.DataManagementUiState

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    dataManagementUiState: DataManagementUiState,
    versionName: String,
    versionCode: Int,
    packageName: String,
    onBack: () -> Unit,
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
        PremiumSection()
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
        ReceiptSection(receiptImageCount = uiState.receiptImageCount)
        AppSettingsSection()
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
private fun PremiumSection() {
    SettingsCard(title = "프리미엄") {
        Text(
            text = "프리미엄 안내",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF18181B)
        )
        Text(
            text = "예상 가격: 1,500원 1회 구매",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF047857)
        )
        Text(
            text = "준비 중",
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFF4F4F5),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF71717A)
        )
        BenefitText(text = "광고 제거")
        BenefitText(text = "데이터 내보내기/가져오기")
        BenefitText(text = "영수증 사진 첨부 제한 완화")
        BenefitText(text = "향후 추가 프리미엄 기능 포함")
    }
}

@Composable
private fun ReceiptSection(
    receiptImageCount: Int
) {
    SettingsCard(title = "영수증") {
        InfoRow(label = "현재 첨부된 사진", value = "${receiptImageCount}장")
        InfoRow(label = "무료 제한", value = "최대 20장")
        InfoRow(label = "프리미엄 제한", value = "최대 1,000장")
        Text(
            text = "현재 MVP에서는 제한 안내만 표시하며, 실제 제한 적용은 추후 프리미엄 기능과 함께 정리할 예정입니다.",
            style = MaterialTheme.typography.bodySmall,
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
                onBack = {},
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
