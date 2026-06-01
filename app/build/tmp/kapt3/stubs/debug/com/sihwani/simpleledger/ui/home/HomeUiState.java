package com.sihwani.simpleledger.ui.home;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BC\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u00c6\u0003J\u000f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\nH\u00c6\u0003JG\u0010\u0019\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\b\b\u0002\u0010\t\u001a\u00020\nH\u00c6\u0001J\u0013\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001d\u001a\u00020\u001eH\u00d6\u0001J\t\u0010\u001f\u001a\u00020\u0003H\u00d6\u0001R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013\u00a8\u0006 "}, d2 = {"Lcom/sihwani/simpleledger/ui/home/HomeUiState;", "", "selectedMonthKey", "", "monthLabel", "incomeTransactions", "", "Lcom/sihwani/simpleledger/domain/model/Transaction;", "expenseTransactions", "summary", "Lcom/sihwani/simpleledger/domain/model/MonthlySummary;", "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Lcom/sihwani/simpleledger/domain/model/MonthlySummary;)V", "getExpenseTransactions", "()Ljava/util/List;", "getIncomeTransactions", "getMonthLabel", "()Ljava/lang/String;", "getSelectedMonthKey", "getSummary", "()Lcom/sihwani/simpleledger/domain/model/MonthlySummary;", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
public final class HomeUiState {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String selectedMonthKey = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String monthLabel = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.sihwani.simpleledger.domain.model.Transaction> incomeTransactions = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.sihwani.simpleledger.domain.model.Transaction> expenseTransactions = null;
    @org.jetbrains.annotations.NotNull()
    private final com.sihwani.simpleledger.domain.model.MonthlySummary summary = null;
    
    public HomeUiState(@org.jetbrains.annotations.NotNull()
    java.lang.String selectedMonthKey, @org.jetbrains.annotations.NotNull()
    java.lang.String monthLabel, @org.jetbrains.annotations.NotNull()
    java.util.List<com.sihwani.simpleledger.domain.model.Transaction> incomeTransactions, @org.jetbrains.annotations.NotNull()
    java.util.List<com.sihwani.simpleledger.domain.model.Transaction> expenseTransactions, @org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.domain.model.MonthlySummary summary) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSelectedMonthKey() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getMonthLabel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.sihwani.simpleledger.domain.model.Transaction> getIncomeTransactions() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.sihwani.simpleledger.domain.model.Transaction> getExpenseTransactions() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.sihwani.simpleledger.domain.model.MonthlySummary getSummary() {
        return null;
    }
    
    public HomeUiState() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.sihwani.simpleledger.domain.model.Transaction> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.sihwani.simpleledger.domain.model.Transaction> component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.sihwani.simpleledger.domain.model.MonthlySummary component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.sihwani.simpleledger.ui.home.HomeUiState copy(@org.jetbrains.annotations.NotNull()
    java.lang.String selectedMonthKey, @org.jetbrains.annotations.NotNull()
    java.lang.String monthLabel, @org.jetbrains.annotations.NotNull()
    java.util.List<com.sihwani.simpleledger.domain.model.Transaction> incomeTransactions, @org.jetbrains.annotations.NotNull()
    java.util.List<com.sihwani.simpleledger.domain.model.Transaction> expenseTransactions, @org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.domain.model.MonthlySummary summary) {
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