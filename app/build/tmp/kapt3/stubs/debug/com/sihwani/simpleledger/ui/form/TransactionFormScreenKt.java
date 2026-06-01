package com.sihwani.simpleledger.ui.form;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00006\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\u001a\u001e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a2\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u00032\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\nH\u0003\u001a.\u0010\u000b\u001a\u00020\u00012\b\u0010\f\u001a\u0004\u0018\u00010\u00032\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a\u00d2\u0001\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00112\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\n2\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\n2\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\n2\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\n2\u0012\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\n2\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\n2\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0014\u0010\u001a\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u0003\u0012\u0004\u0012\u00020\u00010\n2\b\b\u0002\u0010\u001b\u001a\u00020\u001cH\u0007\u001a\b\u0010\u001d\u001a\u00020\u0001H\u0003\u001a\u0010\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u0003H\u0002\u00a8\u0006!"}, d2 = {"FormHeader", "", "title", "", "onBack", "Lkotlin/Function0;", "LedgerDatePickerDialog", "initialDate", "onDismiss", "onConfirm", "Lkotlin/Function1;", "ReceiptImageSection", "previewSource", "onPickImage", "onRemoveImage", "TransactionFormScreen", "uiState", "Lcom/sihwani/simpleledger/ui/form/TransactionFormUiState;", "onAmountChange", "onTitleChange", "onCategoryChange", "onDateChange", "onMemoChange", "onReceiptImageSelected", "onReceiptImageRemove", "onSave", "onSaved", "modifier", "Landroidx/compose/ui/Modifier;", "TransactionFormScreenPreview", "receiptImageModel", "", "source", "app_debug"})
public final class TransactionFormScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.foundation.layout.ExperimentalLayoutApi.class, androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void TransactionFormScreen(@org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.ui.form.TransactionFormUiState uiState, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onAmountChange, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onTitleChange, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onCategoryChange, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onDateChange, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onMemoChange, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onReceiptImageSelected, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onReceiptImageRemove, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSave, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onSaved, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    private static final void LedgerDatePickerDialog(java.lang.String initialDate, kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onConfirm) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ReceiptImageSection(java.lang.String previewSource, kotlin.jvm.functions.Function0<kotlin.Unit> onPickImage, kotlin.jvm.functions.Function0<kotlin.Unit> onRemoveImage) {
    }
    
    private static final java.lang.Object receiptImageModel(java.lang.String source) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    private static final void FormHeader(java.lang.String title, kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 390)
    @androidx.compose.runtime.Composable()
    private static final void TransactionFormScreenPreview() {
    }
}