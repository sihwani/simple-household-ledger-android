package com.sihwani.simpleledger.ui.home;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000^\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0003\u001a\u0016\u0010\u0004\u001a\u00020\u00012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0003\u001a<\u0010\u0007\u001a\u00020\u00012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0003\u001a\b\u0010\r\u001a\u00020\u0001H\u0003\u001a\u0096\u0001\u0010\u000e\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u00102\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u00142\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u00142\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0007\u001a\b\u0010\u0016\u001a\u00020\u0001H\u0003\u001a\u001e\u0010\u0017\u001a\u00020\u00012\u0006\u0010\u0018\u001a\u00020\u00192\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0003\u001a0\u0010\u001b\u001a\u00020\u00012\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0003\u001a2\u0010 \u001a\u00020\u00012\u0006\u0010!\u001a\u00020\u00032\f\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\u0012\u0010#\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u0014H\u0003\u001aH\u0010$\u001a\u00020\u00012\u0006\u0010!\u001a\u00020\u00032\u0006\u0010%\u001a\u00020\u00032\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u0014H\u0003\u001a\u0010\u0010&\u001a\u00020\u00012\u0006\u0010\'\u001a\u00020(H\u0003\u001af\u0010)\u001a\u00020\u00012\u0006\u0010*\u001a\u00020\u00032\u0006\u0010+\u001a\u00020\u001d2\f\u0010,\u001a\b\u0012\u0004\u0012\u00020\u00190-2\u0006\u0010.\u001a\u00020\u00032\u0006\u0010/\u001a\u0002002\u0006\u00101\u001a\u0002002\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u00142\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0003\u00f8\u0001\u0000\u00a2\u0006\u0004\b2\u00103\u001a8\u00104\u001a\u00020\u00012\f\u00105\u001a\b\u0012\u0004\u0012\u00020\u00190-2\f\u00106\u001a\b\u0012\u0004\u0012\u00020\u00190-2\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u0014H\u0003\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u00067"}, d2 = {"EmptyTransactionBox", "", "text", "", "HistoryButton", "onShowHistory", "Lkotlin/Function0;", "HomeActionBar", "onAddExpense", "onHome", "onAddIncome", "modifier", "Landroidx/compose/ui/Modifier;", "HomeHeader", "HomeScreen", "uiState", "Lcom/sihwani/simpleledger/ui/home/HomeUiState;", "onPreviousMonth", "onNextMonth", "onMonthSelected", "Lkotlin/Function1;", "onTransactionClick", "HomeScreenPreview", "HomeTransactionCard", "transaction", "Lcom/sihwani/simpleledger/domain/model/Transaction;", "onClick", "MonthPickerButton", "month", "", "selected", "", "MonthPickerDialog", "selectedMonthKey", "onDismiss", "onConfirm", "MonthSelector", "monthLabel", "SummaryCard", "summary", "Lcom/sihwani/simpleledger/domain/model/MonthlySummary;", "TransactionColumn", "title", "count", "transactions", "", "emptyText", "accentColor", "Landroidx/compose/ui/graphics/Color;", "backgroundColor", "TransactionColumn-kKq0p4A", "(Ljava/lang/String;ILjava/util/List;Ljava/lang/String;JJLkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;)V", "TransactionColumns", "expenseTransactions", "incomeTransactions", "app_debug"})
public final class HomeScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void HomeScreen(@org.jetbrains.annotations.NotNull()
    com.sihwani.simpleledger.ui.home.HomeUiState uiState, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onPreviousMonth, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNextMonth, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAddExpense, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAddIncome, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onHome, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onShowHistory, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onMonthSelected, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onTransactionClick, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void HomeActionBar(kotlin.jvm.functions.Function0<kotlin.Unit> onAddExpense, kotlin.jvm.functions.Function0<kotlin.Unit> onHome, kotlin.jvm.functions.Function0<kotlin.Unit> onAddIncome, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void HomeHeader() {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void MonthSelector(java.lang.String selectedMonthKey, java.lang.String monthLabel, kotlin.jvm.functions.Function0<kotlin.Unit> onPreviousMonth, kotlin.jvm.functions.Function0<kotlin.Unit> onNextMonth, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onMonthSelected) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void MonthPickerDialog(java.lang.String selectedMonthKey, kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onConfirm) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void MonthPickerButton(int month, boolean selected, kotlin.jvm.functions.Function0<kotlin.Unit> onClick, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SummaryCard(com.sihwani.simpleledger.domain.model.MonthlySummary summary) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void HistoryButton(kotlin.jvm.functions.Function0<kotlin.Unit> onShowHistory) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TransactionColumns(java.util.List<com.sihwani.simpleledger.domain.model.Transaction> expenseTransactions, java.util.List<com.sihwani.simpleledger.domain.model.Transaction> incomeTransactions, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onTransactionClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void HomeTransactionCard(com.sihwani.simpleledger.domain.model.Transaction transaction, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void EmptyTransactionBox(java.lang.String text) {
    }
    
    @androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 390)
    @androidx.compose.runtime.Composable()
    private static final void HomeScreenPreview() {
    }
}