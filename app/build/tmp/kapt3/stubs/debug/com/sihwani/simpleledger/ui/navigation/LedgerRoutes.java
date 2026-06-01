package com.sihwani.simpleledger.ui.navigation;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\n\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u000b\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u0004J\u000e\u0010\r\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/sihwani/simpleledger/ui/navigation/LedgerRoutes;", "", "()V", "ExpenseForm", "", "History", "Home", "IncomeForm", "TransactionDetail", "TransactionEdit", "TransactionIdArg", "transactionDetail", "transactionId", "transactionEdit", "app_debug"})
public final class LedgerRoutes {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String Home = "home";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String IncomeForm = "transaction-form/income";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ExpenseForm = "transaction-form/expense";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String History = "history";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TransactionDetail = "transaction-detail/{transactionId}";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TransactionEdit = "transaction-edit/{transactionId}";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TransactionIdArg = "transactionId";
    @org.jetbrains.annotations.NotNull()
    public static final com.sihwani.simpleledger.ui.navigation.LedgerRoutes INSTANCE = null;
    
    private LedgerRoutes() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String transactionDetail(@org.jetbrains.annotations.NotNull()
    java.lang.String transactionId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String transactionEdit(@org.jetbrains.annotations.NotNull()
    java.lang.String transactionId) {
        return null;
    }
}