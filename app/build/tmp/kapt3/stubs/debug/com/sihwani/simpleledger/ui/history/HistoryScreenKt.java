package com.sihwani.simpleledger.ui.history;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000L\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u0016\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a\u001a\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\u0006H\u0003\u001a\u00dc\u0001\u0010\b\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u000e2\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u000e2\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u000e2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\b\b\u0002\u0010\u0019\u001a\u00020\u001aH\u0007\u001a\b\u0010\u001b\u001a\u00020\u0001H\u0003\u001a\u001e\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u001e2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a$\u0010 \u001a\u00020\u00012\u0006\u0010!\u001a\u00020\"2\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u000eH\u0003\u001a*\u0010#\u001a\u00020\u00012\u0006\u0010$\u001a\u00020\u00062\u0006\u0010%\u001a\u00020\u00062\u0006\u0010&\u001a\u00020\'H\u0003\u00f8\u0001\u0000\u00a2\u0006\u0004\b(\u0010)\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006*"}, d2 = {"HistoryHeader", "", "onBack", "Lkotlin/Function0;", "HistoryNotice", "title", "", "description", "HistoryScreen", "uiState", "Lcom/sihwani/simpleledger/ui/history/HistoryUiState;", "dataManagementUiState", "Lcom/sihwani/simpleledger/ui/history/DataManagementUiState;", "onTransactionClick", "Lkotlin/Function1;", "onExportBackup", "onImportBackup", "onMergeImport", "onRequestReplaceImport", "onConfirmReplaceImport", "onDismissImportModeDialog", "onDismissReplaceConfirmDialog", "onRequestDeleteAll", "onConfirmDeleteAll", "onDismissDeleteAllConfirmDialog", "modifier", "Landroidx/compose/ui/Modifier;", "HistoryScreenPreview", "HistoryTransactionItem", "transaction", "Lcom/sihwani/simpleledger/domain/model/Transaction;", "onClick", "MonthlyHistorySectionCard", "section", "Lcom/sihwani/simpleledger/ui/history/MonthlyHistorySection;", "MonthlySummaryLine", "label", "amount", "color", "Landroidx/compose/ui/graphics/Color;", "MonthlySummaryLine-mxwnekA", "(Ljava/lang/String;Ljava/lang/String;J)V", "app_debug"})
public final class HistoryScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void HistoryScreen(@org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.ui.history.HistoryUiState uiState, @org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.ui.history.DataManagementUiState dataManagementUiState, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onTransactionClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onExportBackup, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onImportBackup, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onMergeImport, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onRequestReplaceImport, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onConfirmReplaceImport, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismissImportModeDialog, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismissReplaceConfirmDialog, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onRequestDeleteAll, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onConfirmDeleteAll, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismissDeleteAllConfirmDialog, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void HistoryHeader(kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void MonthlyHistorySectionCard(com.sihwani.simpleledger.ui.history.MonthlyHistorySection section, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onTransactionClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void HistoryTransactionItem(com.sihwani.simpleledger.domain.model.Transaction transaction, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void HistoryNotice(java.lang.String title, java.lang.String description) {
    }
    
    @androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 390)
    @androidx.compose.runtime.Composable()
    private static final void HistoryScreenPreview() {
    }
}