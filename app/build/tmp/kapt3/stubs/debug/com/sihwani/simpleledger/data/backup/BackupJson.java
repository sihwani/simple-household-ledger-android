package com.sihwani.simpleledger.data.backup;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0007\u001a\u00020\u0004J\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\u0006\u0010\u000b\u001a\u00020\u0004J\u0014\u0010\f\u001a\u00020\u00042\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\n0\tJ\u001b\u0010\u000e\u001a\u0004\u0018\u00010\u000f*\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0004H\u0002\u00a2\u0006\u0002\u0010\u0012J\u0016\u0010\u0013\u001a\u0004\u0018\u00010\u0004*\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0004H\u0002J\u0014\u0010\u0014\u001a\u00020\u000f*\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0004H\u0002J\u0014\u0010\u0015\u001a\u00020\u0004*\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0004H\u0002J\f\u0010\u0016\u001a\u00020\n*\u00020\u0010H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/sihwani/simpleledger/data/backup/BackupJson;", "", "()V", "APP_ID", "", "VERSION", "", "createFileName", "decode", "", "Lcom/sihwani/simpleledger/domain/model/Transaction;", "jsonText", "encode", "transactions", "optionalLong", "", "Lorg/json/JSONObject;", "key", "(Lorg/json/JSONObject;Ljava/lang/String;)Ljava/lang/Long;", "optionalString", "requiredLong", "requiredString", "toTransaction", "app_debug"})
public final class BackupJson {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APP_ID = "hannun-ledger";
    public static final int VERSION = 1;
    @org.jetbrains.annotations.NotNull()
    public static final com.sihwani.simpleledger.data.backup.BackupJson INSTANCE = null;
    
    private BackupJson() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String createFileName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String encode(@org.jetbrains.annotations.NotNull()
    java.util.List<com.sihwani.simpleledger.domain.model.Transaction> transactions) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.sihwani.simpleledger.domain.model.Transaction> decode(@org.jetbrains.annotations.NotNull()
    java.lang.String jsonText) {
        return null;
    }
    
    private final com.sihwani.simpleledger.domain.model.Transaction toTransaction(org.json.JSONObject $this$toTransaction) {
        return null;
    }
    
    private final java.lang.String requiredString(org.json.JSONObject $this$requiredString, java.lang.String key) {
        return null;
    }
    
    private final java.lang.String optionalString(org.json.JSONObject $this$optionalString, java.lang.String key) {
        return null;
    }
    
    private final long requiredLong(org.json.JSONObject $this$requiredLong, java.lang.String key) {
        return 0L;
    }
    
    private final java.lang.Long optionalLong(org.json.JSONObject $this$optionalLong, java.lang.String key) {
        return null;
    }
}