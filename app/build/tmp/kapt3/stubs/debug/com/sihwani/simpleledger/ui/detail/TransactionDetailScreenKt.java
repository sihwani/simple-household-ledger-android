package com.sihwani.simpleledger.ui.detail;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000F\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u001a\u0016\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a\u0010\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0006H\u0003\u001a\u0018\u0010\u0007\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\u0006H\u0003\u001a\u001e\u0010\n\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\u00062\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a\u001e\u0010\r\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\u00062\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a@\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u0015H\u0003\u001a`\u0010\u0016\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u00182\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\b\b\u0002\u0010\u001b\u001a\u00020\u001cH\u0007\u001a\b\u0010\u001d\u001a\u00020\u0001H\u0003\u001a\u001a\u0010\u001e\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\u00062\b\b\u0002\u0010\u001b\u001a\u00020\u001cH\u0003\u001a\u0010\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\u0006H\u0002\u001a\u0010\u0010\"\u001a\u00020\u00062\u0006\u0010#\u001a\u00020$H\u0002\u00a8\u0006%"}, d2 = {"DetailHeader", "", "onBack", "Lkotlin/Function0;", "DetailNotice", "text", "", "DetailRow", "label", "value", "ReceiptImageCard", "receiptImagePath", "onClick", "ReceiptImageViewerDialog", "onDismiss", "TransactionDetailContent", "transaction", "Lcom/sihwani/simpleledger/domain/model/Transaction;", "onEdit", "onDeleteClick", "onReceiptClick", "Lkotlin/Function1;", "TransactionDetailScreen", "uiState", "Lcom/sihwani/simpleledger/ui/detail/TransactionDetailUiState;", "onDismissDelete", "onConfirmDelete", "modifier", "Landroidx/compose/ui/Modifier;", "TransactionDetailScreenPreview", "ZoomableReceiptImage", "receiptImageModel", "", "source", "typeLabel", "type", "Lcom/sihwani/simpleledger/domain/model/TransactionType;", "app_debug"})
public final class TransactionDetailScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void TransactionDetailScreen(@org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.ui.detail.TransactionDetailUiState uiState, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onEdit, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDeleteClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismissDelete, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onConfirmDelete, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void DetailHeader(kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TransactionDetailContent(com.sihwani.simpleledger.domain.model.Transaction transaction, kotlin.jvm.functions.Function0<kotlin.Unit> onEdit, kotlin.jvm.functions.Function0<kotlin.Unit> onDeleteClick, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onReceiptClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ReceiptImageCard(java.lang.String receiptImagePath, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ReceiptImageViewerDialog(java.lang.String receiptImagePath, kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ZoomableReceiptImage(java.lang.String receiptImagePath, androidx.compose.ui.Modifier modifier) {
    }
    
    private static final java.lang.Object receiptImageModel(java.lang.String source) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    private static final void DetailRow(java.lang.String label, java.lang.String value) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void DetailNotice(java.lang.String text) {
    }
    
    private static final java.lang.String typeLabel(com.sihwani.simpleledger.domain.model.TransactionType type) {
        return null;
    }
    
    @androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 390)
    @androidx.compose.runtime.Composable()
    private static final void TransactionDetailScreenPreview() {
    }
}