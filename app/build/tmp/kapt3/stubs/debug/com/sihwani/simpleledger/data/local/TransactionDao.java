package com.sihwani.simpleledger.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\f\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\n0\u000eH\'J\u0018\u0010\u000f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\u000e2\u0006\u0010\u0006\u001a\u00020\u0007H\'J\u001c\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\n0\u000e2\u0006\u0010\u0011\u001a\u00020\u0007H\'J\u0016\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u0014J\u001c\u0010\u0015\u001a\u00020\u00032\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u00a7@\u00a2\u0006\u0002\u0010\u0017\u00a8\u0006\u0018"}, d2 = {"Lcom/sihwani/simpleledger/data/local/TransactionDao;", "", "deleteAll", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteById", "id", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAll", "", "Lcom/sihwani/simpleledger/data/local/TransactionEntity;", "getById", "observeAll", "Lkotlinx/coroutines/flow/Flow;", "observeById", "observeByMonth", "monthPrefix", "upsert", "transaction", "(Lcom/sihwani/simpleledger/data/local/TransactionEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "upsertAll", "transactions", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface TransactionDao {
    
    @androidx.room.Query(value = "\n        SELECT * FROM transactions\n        ORDER BY date DESC, createdAt DESC\n        ")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.sihwani.simpleledger.data.local.TransactionEntity>> observeAll();
    
    @androidx.room.Query(value = "\n        SELECT * FROM transactions\n        WHERE date LIKE :monthPrefix || \'%\'\n        ORDER BY date DESC, createdAt DESC\n        ")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.sihwani.simpleledger.data.local.TransactionEntity>> observeByMonth(@org.jetbrains.annotations.NotNull()
    java.lang.String monthPrefix);
    
    @androidx.room.Query(value = "SELECT * FROM transactions WHERE id = :id LIMIT 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.sihwani.simpleledger.data.local.TransactionEntity> observeById(@org.jetbrains.annotations.NotNull()
    java.lang.String id);
    
    @androidx.room.Query(value = "SELECT * FROM transactions WHERE id = :id LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.sihwani.simpleledger.data.local.TransactionEntity> $completion);
    
    @androidx.room.Query(value = "\n        SELECT * FROM transactions\n        ORDER BY date DESC, createdAt DESC\n        ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.sihwani.simpleledger.data.local.TransactionEntity>> $completion);
    
    @androidx.room.Upsert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object upsert(@org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.data.local.TransactionEntity transaction, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object upsertAll(@org.jetbrains.annotations.NotNull()
    java.util.List<com.sihwani.simpleledger.data.local.TransactionEntity> transactions, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM transactions WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM transactions")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}