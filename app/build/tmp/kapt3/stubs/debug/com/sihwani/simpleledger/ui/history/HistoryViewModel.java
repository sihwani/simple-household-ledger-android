package com.sihwani.simpleledger.ui.history;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\n\u001a\u0012\u0012\u0004\u0012\u00020\f0\u000bj\b\u0012\u0004\u0012\u00020\f`\rH\u0002J\u0018\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f*\b\u0012\u0004\u0012\u00020\f0\u000fH\u0002R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t\u00a8\u0006\u0011"}, d2 = {"Lcom/sihwani/simpleledger/ui/history/HistoryViewModel;", "Landroidx/lifecycle/ViewModel;", "transactionRepository", "Lcom/sihwani/simpleledger/data/repository/TransactionRepository;", "(Lcom/sihwani/simpleledger/data/repository/TransactionRepository;)V", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/sihwani/simpleledger/ui/history/HistoryUiState;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "transactionSort", "Ljava/util/Comparator;", "Lcom/sihwani/simpleledger/domain/model/Transaction;", "Lkotlin/Comparator;", "toMonthlySections", "", "Lcom/sihwani/simpleledger/ui/history/MonthlyHistorySection;", "app_debug"})
public final class HistoryViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.sihwani.simpleledger.ui.history.HistoryUiState> uiState = null;
    
    public HistoryViewModel(@org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.data.repository.TransactionRepository transactionRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.sihwani.simpleledger.ui.history.HistoryUiState> getUiState() {
        return null;
    }
    
    private final java.util.List<com.sihwani.simpleledger.ui.history.MonthlyHistorySection> toMonthlySections(java.util.List<com.sihwani.simpleledger.domain.model.Transaction> $this$toMonthlySections) {
        return null;
    }
    
    private final java.util.Comparator<com.sihwani.simpleledger.domain.model.Transaction> transactionSort() {
        return null;
    }
}