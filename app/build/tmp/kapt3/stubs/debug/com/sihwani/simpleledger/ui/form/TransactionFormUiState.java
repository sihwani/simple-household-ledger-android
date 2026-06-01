package com.sihwani.simpleledger.ui.form;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b6\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u00bb\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0005\u0012\b\b\u0002\u0010\b\u001a\u00020\u0005\u0012\b\b\u0002\u0010\t\u001a\u00020\u0005\u0012\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u000f\u0012\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u000f\u0012\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0017J\t\u00101\u001a\u00020\u0003H\u00c6\u0003J\t\u00102\u001a\u00020\u000fH\u00c6\u0003J\t\u00103\u001a\u00020\u000fH\u00c6\u0003J\t\u00104\u001a\u00020\u000fH\u00c6\u0003J\t\u00105\u001a\u00020\u000fH\u00c6\u0003J\t\u00106\u001a\u00020\u000fH\u00c6\u0003J\u000b\u00107\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u00108\u001a\u00020\u000fH\u00c6\u0003J\u000b\u00109\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010:\u001a\u00020\u0005H\u00c6\u0003J\t\u0010;\u001a\u00020\u0005H\u00c6\u0003J\t\u0010<\u001a\u00020\u0005H\u00c6\u0003J\t\u0010=\u001a\u00020\u0005H\u00c6\u0003J\t\u0010>\u001a\u00020\u0005H\u00c6\u0003J\u000f\u0010?\u001a\b\u0012\u0004\u0012\u00020\u00050\u000bH\u00c6\u0003J\u000b\u0010@\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010A\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u00c1\u0001\u0010B\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\u00052\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u000f2\b\b\u0002\u0010\u0011\u001a\u00020\u000f2\b\b\u0002\u0010\u0012\u001a\u00020\u000f2\b\b\u0002\u0010\u0013\u001a\u00020\u000f2\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0015\u001a\u00020\u000f2\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001J\u0013\u0010C\u001a\u00020\u000f2\b\u0010D\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010E\u001a\u00020FH\u00d6\u0001J\t\u0010G\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0019R\u0011\u0010\b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0019R\u0013\u0010\u0014\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0019R\u0011\u0010\u0010\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u001fR\u0011\u0010\u0011\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u001fR\u0011\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u001fR\u0011\u0010\u0012\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u001fR\u0011\u0010\t\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u0019R\u0011\u0010\u0013\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001fR\u0013\u0010\f\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0019R\u0013\u0010#\u001a\u0004\u0018\u00010\u00058F\u00a2\u0006\u0006\u001a\u0004\b$\u0010\u0019R\u0011\u0010\u0015\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u001fR\u0013\u0010\u0016\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u0019R\u0011\u0010\'\u001a\u00020\u00058F\u00a2\u0006\u0006\u001a\u0004\b(\u0010\u0019R\u0013\u0010\r\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\u0019R\u0011\u0010*\u001a\u00020\u000f8F\u00a2\u0006\u0006\u001a\u0004\b+\u0010\u001fR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010\u0019R\u0011\u0010-\u001a\u00020\u00058F\u00a2\u0006\u0006\u001a\u0004\b.\u0010\u0019R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u00100\u00a8\u0006H"}, d2 = {"Lcom/sihwani/simpleledger/ui/form/TransactionFormUiState;", "", "type", "Lcom/sihwani/simpleledger/domain/model/TransactionType;", "amountText", "", "title", "category", "date", "memo", "categories", "", "receiptImagePath", "selectedReceiptImageUri", "isReceiptMarkedForDeletion", "", "isEditMode", "isLoading", "isSaving", "notFound", "errorMessage", "saveCompleted", "savedTransactionId", "(Lcom/sihwani/simpleledger/domain/model/TransactionType;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;ZZZZZLjava/lang/String;ZLjava/lang/String;)V", "getAmountText", "()Ljava/lang/String;", "getCategories", "()Ljava/util/List;", "getCategory", "getDate", "getErrorMessage", "()Z", "getMemo", "getNotFound", "getReceiptImagePath", "receiptPreviewSource", "getReceiptPreviewSource", "getSaveCompleted", "getSavedTransactionId", "screenTitle", "getScreenTitle", "getSelectedReceiptImageUri", "showReceiptSection", "getShowReceiptSection", "getTitle", "titleLabel", "getTitleLabel", "getType", "()Lcom/sihwani/simpleledger/domain/model/TransactionType;", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class TransactionFormUiState {
    @org.jetbrains.annotations.NotNull()
    private final com.sihwani.simpleledger.domain.model.TransactionType type = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String amountText = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String title = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String category = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String date = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String memo = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> categories = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String receiptImagePath = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String selectedReceiptImageUri = null;
    private final boolean isReceiptMarkedForDeletion = false;
    private final boolean isEditMode = false;
    private final boolean isLoading = false;
    private final boolean isSaving = false;
    private final boolean notFound = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String errorMessage = null;
    private final boolean saveCompleted = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String savedTransactionId = null;
    
    public TransactionFormUiState(@org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.domain.model.TransactionType type, @org.jetbrains.annotations.NotNull()
    java.lang.String amountText, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String category, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    java.lang.String memo, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> categories, @org.jetbrains.annotations.Nullable()
    java.lang.String receiptImagePath, @org.jetbrains.annotations.Nullable()
    java.lang.String selectedReceiptImageUri, boolean isReceiptMarkedForDeletion, boolean isEditMode, boolean isLoading, boolean isSaving, boolean notFound, @org.jetbrains.annotations.Nullable()
    java.lang.String errorMessage, boolean saveCompleted, @org.jetbrains.annotations.Nullable()
    java.lang.String savedTransactionId) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.sihwani.simpleledger.domain.model.TransactionType getType() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAmountText() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCategory() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDate() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getMemo() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getCategories() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getReceiptImagePath() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSelectedReceiptImageUri() {
        return null;
    }
    
    public final boolean isReceiptMarkedForDeletion() {
        return false;
    }
    
    public final boolean isEditMode() {
        return false;
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    public final boolean isSaving() {
        return false;
    }
    
    public final boolean getNotFound() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getErrorMessage() {
        return null;
    }
    
    public final boolean getSaveCompleted() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSavedTransactionId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getScreenTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTitleLabel() {
        return null;
    }
    
    public final boolean getShowReceiptSection() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getReceiptPreviewSource() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.sihwani.simpleledger.domain.model.TransactionType component1() {
        return null;
    }
    
    public final boolean component10() {
        return false;
    }
    
    public final boolean component11() {
        return false;
    }
    
    public final boolean component12() {
        return false;
    }
    
    public final boolean component13() {
        return false;
    }
    
    public final boolean component14() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component15() {
        return null;
    }
    
    public final boolean component16() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component17() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.sihwani.simpleledger.ui.form.TransactionFormUiState copy(@org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.domain.model.TransactionType type, @org.jetbrains.annotations.NotNull()
    java.lang.String amountText, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String category, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    java.lang.String memo, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> categories, @org.jetbrains.annotations.Nullable()
    java.lang.String receiptImagePath, @org.jetbrains.annotations.Nullable()
    java.lang.String selectedReceiptImageUri, boolean isReceiptMarkedForDeletion, boolean isEditMode, boolean isLoading, boolean isSaving, boolean notFound, @org.jetbrains.annotations.Nullable()
    java.lang.String errorMessage, boolean saveCompleted, @org.jetbrains.annotations.Nullable()
    java.lang.String savedTransactionId) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}