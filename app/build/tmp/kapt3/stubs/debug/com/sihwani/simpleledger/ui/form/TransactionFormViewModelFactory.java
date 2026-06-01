package com.sihwani.simpleledger.ui.form;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\u0002\u0010\nJ%\u0010\u000b\u001a\u0002H\f\"\b\b\u0000\u0010\f*\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u0002H\f0\u000fH\u0016\u00a2\u0006\u0002\u0010\u0010R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/sihwani/simpleledger/ui/form/TransactionFormViewModelFactory;", "Landroidx/lifecycle/ViewModelProvider$Factory;", "transactionRepository", "Lcom/sihwani/simpleledger/data/repository/TransactionRepository;", "receiptImageStorage", "Lcom/sihwani/simpleledger/data/storage/ReceiptImageStorage;", "type", "Lcom/sihwani/simpleledger/domain/model/TransactionType;", "transactionId", "", "(Lcom/sihwani/simpleledger/data/repository/TransactionRepository;Lcom/sihwani/simpleledger/data/storage/ReceiptImageStorage;Lcom/sihwani/simpleledger/domain/model/TransactionType;Ljava/lang/String;)V", "create", "T", "Landroidx/lifecycle/ViewModel;", "modelClass", "Ljava/lang/Class;", "(Ljava/lang/Class;)Landroidx/lifecycle/ViewModel;", "app_debug"})
public final class TransactionFormViewModelFactory implements androidx.lifecycle.ViewModelProvider.Factory {
    @org.jetbrains.annotations.NotNull()
    private final com.sihwani.simpleledger.data.repository.TransactionRepository transactionRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.sihwani.simpleledger.data.storage.ReceiptImageStorage receiptImageStorage = null;
    @org.jetbrains.annotations.NotNull()
    private final com.sihwani.simpleledger.domain.model.TransactionType type = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String transactionId = null;
    
    public TransactionFormViewModelFactory(@org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.data.repository.TransactionRepository transactionRepository, @org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.data.storage.ReceiptImageStorage receiptImageStorage, @org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.domain.model.TransactionType type, @org.jetbrains.annotations.Nullable()
    java.lang.String transactionId) {
        super();
    }
    
    @java.lang.Override()
    @kotlin.Suppress(names = {"UNCHECKED_CAST"})
    @org.jetbrains.annotations.NotNull()
    public <T extends androidx.lifecycle.ViewModel>T create(@org.jetbrains.annotations.NotNull()
    java.lang.Class<T> modelClass) {
        return null;
    }
}