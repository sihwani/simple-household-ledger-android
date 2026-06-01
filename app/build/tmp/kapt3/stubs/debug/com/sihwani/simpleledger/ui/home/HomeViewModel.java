package com.sihwani.simpleledger.ui.home;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0011\u001a\u00020\u0012J\u0006\u0010\u0013\u001a\u00020\u0012J\u000e\u0010\u0014\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u000bR\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u0016"}, d2 = {"Lcom/sihwani/simpleledger/ui/home/HomeViewModel;", "Landroidx/lifecycle/ViewModel;", "transactionRepository", "Lcom/sihwani/simpleledger/data/repository/TransactionRepository;", "(Lcom/sihwani/simpleledger/data/repository/TransactionRepository;)V", "monthlyTransactions", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/sihwani/simpleledger/domain/model/Transaction;", "selectedMonthKey", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/sihwani/simpleledger/ui/home/HomeUiState;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "moveNextMonth", "", "movePreviousMonth", "moveToMonth", "monthKey", "app_debug"})
@kotlin.OptIn(markerClass = {kotlinx.coroutines.ExperimentalCoroutinesApi.class})
public final class HomeViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.sihwani.simpleledger.data.repository.TransactionRepository transactionRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> selectedMonthKey = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.sihwani.simpleledger.domain.model.Transaction>> monthlyTransactions = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.sihwani.simpleledger.ui.home.HomeUiState> uiState = null;
    
    public HomeViewModel(@org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.data.repository.TransactionRepository transactionRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.sihwani.simpleledger.ui.home.HomeUiState> getUiState() {
        return null;
    }
    
    public final void movePreviousMonth() {
    }
    
    public final void moveNextMonth() {
    }
    
    public final void moveToMonth(@org.jetbrains.annotations.NotNull()
    java.lang.String monthKey) {
    }
}