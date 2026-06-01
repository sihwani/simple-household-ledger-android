package com.sihwani.simpleledger.ui.detail;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0006\u0010\u0010\u001a\u00020\u0011J\u0006\u0010\u0012\u001a\u00020\u0011J\b\u0010\u0013\u001a\u00020\u0011H\u0002J\u0006\u0010\u0014\u001a\u00020\u0011R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\u0015"}, d2 = {"Lcom/sihwani/simpleledger/ui/detail/TransactionDetailViewModel;", "Landroidx/lifecycle/ViewModel;", "transactionRepository", "Lcom/sihwani/simpleledger/data/repository/TransactionRepository;", "receiptImageStorage", "Lcom/sihwani/simpleledger/data/storage/ReceiptImageStorage;", "transactionId", "", "(Lcom/sihwani/simpleledger/data/repository/TransactionRepository;Lcom/sihwani/simpleledger/data/storage/ReceiptImageStorage;Ljava/lang/String;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/sihwani/simpleledger/ui/detail/TransactionDetailUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "deleteTransaction", "", "dismissDeleteDialog", "observeTransaction", "requestDelete", "app_debug"})
public final class TransactionDetailViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.sihwani.simpleledger.data.repository.TransactionRepository transactionRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.sihwani.simpleledger.data.storage.ReceiptImageStorage receiptImageStorage = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String transactionId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.sihwani.simpleledger.ui.detail.TransactionDetailUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.sihwani.simpleledger.ui.detail.TransactionDetailUiState> uiState = null;
    
    public TransactionDetailViewModel(@org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.data.repository.TransactionRepository transactionRepository, @org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.data.storage.ReceiptImageStorage receiptImageStorage, @org.jetbrains.annotations.NotNull()
    java.lang.String transactionId) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.sihwani.simpleledger.ui.detail.TransactionDetailUiState> getUiState() {
        return null;
    }
    
    private final void observeTransaction() {
    }
    
    public final void requestDelete() {
    }
    
    public final void dismissDeleteDialog() {
    }
    
    public final void deleteTransaction() {
    }
}