package com.sihwani.simpleledger.ui.form;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u000f\b\u0007\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\u0002\u0010\nJ\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\tH\u0002J\u000e\u0010\u0017\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\tJ\u000e\u0010\u0019\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\tJ\u000e\u0010\u001a\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\tJ\u000e\u0010\u001b\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\tJ\u0006\u0010\u001c\u001a\u00020\u0015J\u000e\u0010\u001d\u001a\u00020\u00152\u0006\u0010\u001e\u001a\u00020\tJ\u000e\u0010\u001f\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\tJ \u0010 \u001a\u0004\u0018\u00010\t2\u0006\u0010!\u001a\u00020\r2\u0006\u0010\b\u001a\u00020\tH\u0082@\u00a2\u0006\u0002\u0010\"J\u0006\u0010#\u001a\u00020\u0015R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\r0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013\u00a8\u0006$"}, d2 = {"Lcom/sihwani/simpleledger/ui/form/TransactionFormViewModel;", "Landroidx/lifecycle/ViewModel;", "transactionRepository", "Lcom/sihwani/simpleledger/data/repository/TransactionRepository;", "receiptImageStorage", "Lcom/sihwani/simpleledger/data/storage/ReceiptImageStorage;", "type", "Lcom/sihwani/simpleledger/domain/model/TransactionType;", "transactionId", "", "(Lcom/sihwani/simpleledger/data/repository/TransactionRepository;Lcom/sihwani/simpleledger/data/storage/ReceiptImageStorage;Lcom/sihwani/simpleledger/domain/model/TransactionType;Ljava/lang/String;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/sihwani/simpleledger/ui/form/TransactionFormUiState;", "originalTransaction", "Lcom/sihwani/simpleledger/domain/model/Transaction;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "loadTransaction", "", "id", "onAmountChange", "value", "onCategoryChange", "onDateChange", "onMemoChange", "onReceiptImageRemove", "onReceiptImageSelected", "uriString", "onTitleChange", "resolveReceiptImagePath", "state", "(Lcom/sihwani/simpleledger/ui/form/TransactionFormUiState;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "save", "app_debug"})
public final class TransactionFormViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.sihwani.simpleledger.data.repository.TransactionRepository transactionRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.sihwani.simpleledger.data.storage.ReceiptImageStorage receiptImageStorage = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String transactionId = null;
    @org.jetbrains.annotations.Nullable()
    private com.sihwani.simpleledger.domain.model.Transaction originalTransaction;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.sihwani.simpleledger.ui.form.TransactionFormUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.sihwani.simpleledger.ui.form.TransactionFormUiState> uiState = null;
    
    public TransactionFormViewModel(@org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.data.repository.TransactionRepository transactionRepository, @org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.data.storage.ReceiptImageStorage receiptImageStorage, @org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.domain.model.TransactionType type, @org.jetbrains.annotations.Nullable()
    java.lang.String transactionId) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.sihwani.simpleledger.ui.form.TransactionFormUiState> getUiState() {
        return null;
    }
    
    private final void loadTransaction(java.lang.String id) {
    }
    
    public final void onAmountChange(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void onTitleChange(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void onCategoryChange(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void onDateChange(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void onMemoChange(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void onReceiptImageSelected(@org.jetbrains.annotations.NotNull()
    java.lang.String uriString) {
    }
    
    public final void onReceiptImageRemove() {
    }
    
    public final void save() {
    }
    
    private final java.lang.Object resolveReceiptImagePath(com.sihwani.simpleledger.ui.form.TransactionFormUiState state, java.lang.String transactionId, kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
}