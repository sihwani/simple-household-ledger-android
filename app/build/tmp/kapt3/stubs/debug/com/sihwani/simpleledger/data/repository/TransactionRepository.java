package com.sihwani.simpleledger.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\b\u0007\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u000e\u0010\f\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\rJ\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fH\u0086@\u00a2\u0006\u0002\u0010\rJ\u0018\u0010\u0011\u001a\u0004\u0018\u00010\u00102\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u0012\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000f0\u0013J\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\u00132\u0006\u0010\u0016\u001a\u00020\nJ\u0016\u0010\u0017\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00100\u00132\u0006\u0010\t\u001a\u00020\nJ\u001a\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000f0\u00132\u0006\u0010\u0016\u001a\u00020\nJ\u001c\u0010\u0019\u001a\u00020\b2\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fH\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u0016\u0010\u001c\u001a\u00020\b2\u0006\u0010\u001d\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010\u001eJ\u001c\u0010\u001f\u001a\u00020\b2\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fH\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u0012\u0010 \u001a\u00020\u0015*\b\u0012\u0004\u0012\u00020\u00100\u000fH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006!"}, d2 = {"Lcom/sihwani/simpleledger/data/repository/TransactionRepository;", "", "transactionDao", "Lcom/sihwani/simpleledger/data/local/TransactionDao;", "ioDispatcher", "Lkotlinx/coroutines/CoroutineDispatcher;", "(Lcom/sihwani/simpleledger/data/local/TransactionDao;Lkotlinx/coroutines/CoroutineDispatcher;)V", "delete", "", "id", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAll", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllTransactions", "", "Lcom/sihwani/simpleledger/domain/model/Transaction;", "getTransaction", "observeAllTransactions", "Lkotlinx/coroutines/flow/Flow;", "observeMonthlySummary", "Lcom/sihwani/simpleledger/domain/model/MonthlySummary;", "monthKey", "observeTransaction", "observeTransactionsByMonth", "replaceAll", "transactions", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "upsert", "transaction", "(Lcom/sihwani/simpleledger/domain/model/Transaction;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "upsertAll", "toMonthlySummary", "app_debug"})
public final class TransactionRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.sihwani.simpleledger.data.local.TransactionDao transactionDao = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineDispatcher ioDispatcher = null;
    
    public TransactionRepository(@org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.data.local.TransactionDao transactionDao, @org.jetbrains.annotations.NotNull()
    kotlinx.coroutines.CoroutineDispatcher ioDispatcher) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.sihwani.simpleledger.domain.model.Transaction>> observeAllTransactions() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.sihwani.simpleledger.domain.model.Transaction>> observeTransactionsByMonth(@org.jetbrains.annotations.NotNull()
    java.lang.String monthKey) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.sihwani.simpleledger.domain.model.Transaction> observeTransaction(@org.jetbrains.annotations.NotNull()
    java.lang.String id) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.sihwani.simpleledger.domain.model.MonthlySummary> observeMonthlySummary(@org.jetbrains.annotations.NotNull()
    java.lang.String monthKey) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getTransaction(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.sihwani.simpleledger.domain.model.Transaction> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAllTransactions(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.sihwani.simpleledger.domain.model.Transaction>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object upsert(@org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.domain.model.Transaction transaction, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object upsertAll(@org.jetbrains.annotations.NotNull()
    java.util.List<com.sihwani.simpleledger.domain.model.Transaction> transactions, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object replaceAll(@org.jetbrains.annotations.NotNull()
    java.util.List<com.sihwani.simpleledger.domain.model.Transaction> transactions, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final com.sihwani.simpleledger.domain.model.MonthlySummary toMonthlySummary(java.util.List<com.sihwani.simpleledger.domain.model.Transaction> $this$toMonthlySummary) {
        return null;
    }
}