package com.sihwani.simpleledger.ui.history;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\n\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\b\u0010\u0013\u001a\u00020\u0014H\u0002J\u0006\u0010\u0015\u001a\u00020\u0014J\u0006\u0010\u0016\u001a\u00020\u0014J\u001c\u0010\u0017\u001a\u00020\u00142\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u0082@\u00a2\u0006\u0002\u0010\u0019J\u0006\u0010\u001a\u001a\u00020\u0014J\u0006\u0010\u001b\u001a\u00020\u0014J\u0006\u0010\u001c\u001a\u00020\u0014J\u000e\u0010\u001d\u001a\u00020\u00142\u0006\u0010\u001e\u001a\u00020\u001fJ\u000e\u0010 \u001a\u00020\u00142\u0006\u0010\u001e\u001a\u00020\u001fJ\u0006\u0010!\u001a\u00020\u0014J\u0006\u0010\"\u001a\u00020\u0014J\u0006\u0010#\u001a\u00020\u0014J\u0010\u0010$\u001a\u00020\u00142\u0006\u0010%\u001a\u00020\u001fH\u0002R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012\u00a8\u0006&"}, d2 = {"Lcom/sihwani/simpleledger/ui/history/DataManagementViewModel;", "Landroidx/lifecycle/ViewModel;", "transactionRepository", "Lcom/sihwani/simpleledger/data/repository/TransactionRepository;", "backupFileManager", "Lcom/sihwani/simpleledger/data/backup/BackupFileManager;", "receiptImageStorage", "Lcom/sihwani/simpleledger/data/storage/ReceiptImageStorage;", "(Lcom/sihwani/simpleledger/data/repository/TransactionRepository;Lcom/sihwani/simpleledger/data/backup/BackupFileManager;Lcom/sihwani/simpleledger/data/storage/ReceiptImageStorage;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/sihwani/simpleledger/ui/history/DataManagementUiState;", "pendingImportTransactions", "", "Lcom/sihwani/simpleledger/domain/model/Transaction;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "clearPendingImport", "", "confirmDeleteAll", "confirmReplaceImport", "deleteReceiptImages", "transactions", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "dismissDeleteAllConfirmDialog", "dismissImportModeDialog", "dismissReplaceConfirmDialog", "exportBackup", "uriString", "", "importBackup", "mergePendingImport", "requestDeleteAll", "requestReplaceImport", "showMessage", "message", "app_debug"})
public final class DataManagementViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.sihwani.simpleledger.data.repository.TransactionRepository transactionRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.sihwani.simpleledger.data.backup.BackupFileManager backupFileManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.sihwani.simpleledger.data.storage.ReceiptImageStorage receiptImageStorage = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.sihwani.simpleledger.ui.history.DataManagementUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.sihwani.simpleledger.ui.history.DataManagementUiState> uiState = null;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.sihwani.simpleledger.domain.model.Transaction> pendingImportTransactions;
    
    public DataManagementViewModel(@org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.data.repository.TransactionRepository transactionRepository, @org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.data.backup.BackupFileManager backupFileManager, @org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.data.storage.ReceiptImageStorage receiptImageStorage) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.sihwani.simpleledger.ui.history.DataManagementUiState> getUiState() {
        return null;
    }
    
    public final void exportBackup(@org.jetbrains.annotations.NotNull()
    java.lang.String uriString) {
    }
    
    public final void importBackup(@org.jetbrains.annotations.NotNull()
    java.lang.String uriString) {
    }
    
    public final void mergePendingImport() {
    }
    
    public final void requestReplaceImport() {
    }
    
    public final void confirmReplaceImport() {
    }
    
    public final void requestDeleteAll() {
    }
    
    public final void confirmDeleteAll() {
    }
    
    public final void dismissImportModeDialog() {
    }
    
    public final void dismissReplaceConfirmDialog() {
    }
    
    public final void dismissDeleteAllConfirmDialog() {
    }
    
    private final java.lang.Object deleteReceiptImages(java.util.List<com.sihwani.simpleledger.domain.model.Transaction> transactions, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final void clearPendingImport() {
    }
    
    private final void showMessage(java.lang.String message) {
    }
}