package com.sihwani.simpleledger;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0014R\u001b\u0010\u0003\u001a\u00020\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u001b\u0010\t\u001a\u00020\n8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\b\u001a\u0004\b\u000b\u0010\fR\u001b\u0010\u000e\u001a\u00020\u000f8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0012\u0010\b\u001a\u0004\b\u0010\u0010\u0011\u00a8\u0006\u0017"}, d2 = {"Lcom/sihwani/simpleledger/MainActivity;", "Landroidx/activity/ComponentActivity;", "()V", "backupFileManager", "Lcom/sihwani/simpleledger/data/backup/BackupFileManager;", "getBackupFileManager", "()Lcom/sihwani/simpleledger/data/backup/BackupFileManager;", "backupFileManager$delegate", "Lkotlin/Lazy;", "receiptImageStorage", "Lcom/sihwani/simpleledger/data/storage/ReceiptImageStorage;", "getReceiptImageStorage", "()Lcom/sihwani/simpleledger/data/storage/ReceiptImageStorage;", "receiptImageStorage$delegate", "transactionRepository", "Lcom/sihwani/simpleledger/data/repository/TransactionRepository;", "getTransactionRepository", "()Lcom/sihwani/simpleledger/data/repository/TransactionRepository;", "transactionRepository$delegate", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "app_debug"})
public final class MainActivity extends androidx.activity.ComponentActivity {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy transactionRepository$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy receiptImageStorage$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy backupFileManager$delegate = null;
    
    public MainActivity() {
        super(0);
    }
    
    private final com.sihwani.simpleledger.data.repository.TransactionRepository getTransactionRepository() {
        return null;
    }
    
    private final com.sihwani.simpleledger.data.storage.ReceiptImageStorage getReceiptImageStorage() {
        return null;
    }
    
    private final com.sihwani.simpleledger.data.backup.BackupFileManager getBackupFileManager() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
}