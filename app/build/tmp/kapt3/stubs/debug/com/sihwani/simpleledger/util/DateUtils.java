package com.sihwani.simpleledger.util;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0007\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\b\u001a\u00020\tJ\u000e\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\tJ\u000e\u0010\f\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\tJ\u000e\u0010\r\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\tJ\u000e\u0010\u000e\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\tJ\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\tJ\u000e\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u000f\u001a\u00020\tJ\u0015\u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u000b\u001a\u00020\t\u00a2\u0006\u0002\u0010\u0015J\u000e\u0010\u000f\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\tJ\u0016\u0010\u0016\u001a\u00020\t2\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u0018J\u000e\u0010\u001a\u001a\u00020\t2\u0006\u0010\u001b\u001a\u00020\u0014J\u0016\u0010\u001c\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\t2\u0006\u0010\u001d\u001a\u00020\u0014J\u0006\u0010\u001e\u001a\u00020\tR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001f"}, d2 = {"Lcom/sihwani/simpleledger/util/DateUtils;", "", "()V", "isoDateFormatter", "Ljava/time/format/DateTimeFormatter;", "isoDateRegex", "Lkotlin/text/Regex;", "monthFormatter", "currentMonthKey", "", "formatDayLabel", "dateIso", "formatFullDate", "formatMonthDayLabel", "formatMonthLabel", "monthKey", "isValidIsoDate", "", "isValidMonthKey", "isoToPickerMillisOrNull", "", "(Ljava/lang/String;)Ljava/lang/Long;", "monthKeyOf", "year", "", "month", "pickerMillisToIso", "millis", "shiftMonth", "offset", "todayIso", "app_debug"})
public final class DateUtils {
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex isoDateRegex = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.time.format.DateTimeFormatter isoDateFormatter = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.time.format.DateTimeFormatter monthFormatter = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.sihwani.simpleledger.util.DateUtils INSTANCE = null;
    
    private DateUtils() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String todayIso() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String currentMonthKey() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String monthKey(@org.jetbrains.annotations.NotNull()
    java.lang.String dateIso) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String monthKeyOf(int year, int month) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String shiftMonth(@org.jetbrains.annotations.NotNull()
    java.lang.String monthKey, long offset) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatMonthLabel(@org.jetbrains.annotations.NotNull()
    java.lang.String monthKey) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatDayLabel(@org.jetbrains.annotations.NotNull()
    java.lang.String dateIso) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatMonthDayLabel(@org.jetbrains.annotations.NotNull()
    java.lang.String dateIso) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatFullDate(@org.jetbrains.annotations.NotNull()
    java.lang.String dateIso) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String pickerMillisToIso(long millis) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long isoToPickerMillisOrNull(@org.jetbrains.annotations.NotNull()
    java.lang.String dateIso) {
        return null;
    }
    
    public final boolean isValidMonthKey(@org.jetbrains.annotations.NotNull()
    java.lang.String monthKey) {
        return false;
    }
    
    public final boolean isValidIsoDate(@org.jetbrains.annotations.NotNull()
    java.lang.String dateIso) {
        return false;
    }
}