package com.sihwani.simpleledger.data.pdf

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionType
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class MonthlyLedgerPdfGenerator {
    fun generate(
        reportData: MonthlyLedgerReportData,
        outputFile: File
    ) {
        outputFile.parentFile?.mkdirs()

        val document = PdfDocument()
        try {
            val pageState = PageState()
            var page = startMonthlyPage(
                document = document,
                reportData = reportData,
                pageNumber = pageState.nextPageNumber(),
                continuation = false
            )

            val transactions = reportData.transactions.sortedWith(transactionSort())
            if (transactions.isEmpty()) {
                page = ensureMonthlyRowSpace(document, reportData, page, pageState)
                drawEmptyRow(page.canvas, page.y)
                page.y += ROW_HEIGHT
            } else {
                transactions.forEach { transaction ->
                    page = ensureMonthlyRowSpace(document, reportData, page, pageState)
                    drawTransactionRow(page.canvas, transaction, page.y)
                    page.y += ROW_HEIGHT
                }
            }

            finishPage(document, page)
            FileOutputStream(outputFile).use { output -> document.writeTo(output) }
        } finally {
            document.close()
        }
    }

    fun generate(
        reportData: YearlyLedgerReportData,
        outputFile: File
    ) {
        outputFile.parentFile?.mkdirs()

        val document = PdfDocument()
        try {
            val pageState = PageState()
            var page = startYearlyPage(
                document = document,
                reportData = reportData,
                pageNumber = pageState.nextPageNumber(),
                continuation = false
            )

            if (reportData.sections.isEmpty()) {
                page = ensureYearlySpace(document, reportData, page, pageState, ROW_HEIGHT)
                drawEmptyRow(page.canvas, page.y)
                page.y += ROW_HEIGHT
            } else {
                reportData.sections.forEach { section ->
                    page = ensureYearlySpace(
                        document = document,
                        reportData = reportData,
                        activePage = page,
                        pageState = pageState,
                        neededHeight = MONTH_SECTION_HEADER_HEIGHT + ROW_HEIGHT
                    )
                    page.y = drawMonthSectionHeader(page.canvas, section, page.y, continuation = false)
                    drawTableHeader(page.canvas, page.y)
                    page.y += ROW_HEIGHT

                    val transactions = section.transactions.sortedWith(transactionSort())
                    if (transactions.isEmpty()) {
                        page = ensureYearlyMonthRowSpace(document, reportData, section, page, pageState)
                        drawEmptyRow(page.canvas, page.y)
                        page.y += ROW_HEIGHT
                    } else {
                        transactions.forEach { transaction ->
                            page = ensureYearlyMonthRowSpace(document, reportData, section, page, pageState)
                            drawTransactionRow(page.canvas, transaction, page.y)
                            page.y += ROW_HEIGHT
                        }
                    }

                    page.y += 10
                }
            }

            finishPage(document, page)
            FileOutputStream(outputFile).use { output -> document.writeTo(output) }
        } finally {
            document.close()
        }
    }

    private fun startMonthlyPage(
        document: PdfDocument,
        reportData: MonthlyLedgerReportData,
        pageNumber: Int,
        continuation: Boolean
    ): ActivePage {
        val page = document.startPage(pageInfo(pageNumber))
        val monthTitle = "${formatMonthTitle(reportData.monthKey)} 가계부"
        val title = if (continuation) "$monthTitle (계속)" else monthTitle
        var y = drawDocumentTitle(page.canvas, title)

        if (!continuation) {
            y = drawSummary(
                canvas = page.canvas,
                y = y,
                income = reportData.summary.income,
                expense = reportData.summary.expense,
                balance = reportData.summary.balance,
                generatedDateIso = reportData.generatedDateIso
            )
        } else {
            y += 12
        }

        drawTableHeader(page.canvas, y)
        y += ROW_HEIGHT

        return ActivePage(page = page, canvas = page.canvas, pageNumber = pageNumber, y = y)
    }

    private fun startYearlyPage(
        document: PdfDocument,
        reportData: YearlyLedgerReportData,
        pageNumber: Int,
        continuation: Boolean
    ): ActivePage {
        val page = document.startPage(pageInfo(pageNumber))
        val title = if (continuation) {
            "${reportData.year}년 가계부 (계속)"
        } else {
            "${reportData.year}년 가계부"
        }
        var y = drawDocumentTitle(page.canvas, title)

        if (!continuation) {
            y = drawSummary(
                canvas = page.canvas,
                y = y,
                income = reportData.incomeTotal,
                expense = reportData.expenseTotal,
                balance = reportData.balance,
                generatedDateIso = reportData.generatedDateIso
            )
        } else {
            y += 12
        }

        return ActivePage(page = page, canvas = page.canvas, pageNumber = pageNumber, y = y)
    }

    private fun drawDocumentTitle(
        canvas: Canvas,
        title: String
    ): Int {
        var y = TOP_MARGIN
        canvas.drawText(title, LEFT_MARGIN.toFloat(), y.toFloat(), titlePaint)
        y += 32
        return y
    }

    private fun drawSummary(
        canvas: Canvas,
        y: Int,
        income: Long,
        expense: Long,
        balance: Long,
        generatedDateIso: String
    ): Int {
        var nextY = y
        canvas.drawText("총 수입: ${formatWon(income)}", LEFT_MARGIN.toFloat(), nextY.toFloat(), summaryPaint)
        nextY += 20
        canvas.drawText("총 지출: ${formatWon(expense)}", LEFT_MARGIN.toFloat(), nextY.toFloat(), summaryPaint)
        nextY += 20
        canvas.drawText("잔액: ${formatWon(balance)}", LEFT_MARGIN.toFloat(), nextY.toFloat(), summaryPaint)
        nextY += 22
        canvas.drawText("생성일 $generatedDateIso", LEFT_MARGIN.toFloat(), nextY.toFloat(), smallPaint)
        nextY += 30
        return nextY
    }

    private fun drawMonthSectionHeader(
        canvas: Canvas,
        section: YearlyLedgerMonthSection,
        y: Int,
        continuation: Boolean
    ): Int {
        val title = if (continuation) "${section.monthLabel} (계속)" else section.monthLabel
        var nextY = y
        canvas.drawText(title, LEFT_MARGIN.toFloat(), nextY.toFloat(), sectionTitlePaint)
        nextY += 22
        canvas.drawText(
            "총 수입 ${formatWon(section.incomeTotal)} · 총 지출 ${formatWon(section.expenseTotal)} · 잔액 ${formatWon(section.balance)}",
            LEFT_MARGIN.toFloat(),
            nextY.toFloat(),
            smallPaint
        )
        nextY += 26
        return nextY
    }

    private fun ensureMonthlyRowSpace(
        document: PdfDocument,
        reportData: MonthlyLedgerReportData,
        activePage: ActivePage,
        pageState: PageState
    ): ActivePage {
        if (activePage.y + ROW_HEIGHT <= PAGE_HEIGHT - BOTTOM_MARGIN) {
            return activePage
        }

        finishPage(document, activePage)
        return startMonthlyPage(
            document = document,
            reportData = reportData,
            pageNumber = pageState.nextPageNumber(),
            continuation = true
        )
    }

    private fun ensureYearlySpace(
        document: PdfDocument,
        reportData: YearlyLedgerReportData,
        activePage: ActivePage,
        pageState: PageState,
        neededHeight: Int
    ): ActivePage {
        if (activePage.y + neededHeight <= PAGE_HEIGHT - BOTTOM_MARGIN) {
            return activePage
        }

        finishPage(document, activePage)
        return startYearlyPage(
            document = document,
            reportData = reportData,
            pageNumber = pageState.nextPageNumber(),
            continuation = true
        )
    }

    private fun ensureYearlyMonthRowSpace(
        document: PdfDocument,
        reportData: YearlyLedgerReportData,
        section: YearlyLedgerMonthSection,
        activePage: ActivePage,
        pageState: PageState
    ): ActivePage {
        if (activePage.y + ROW_HEIGHT <= PAGE_HEIGHT - BOTTOM_MARGIN) {
            return activePage
        }

        finishPage(document, activePage)
        val nextPage = startYearlyPage(
            document = document,
            reportData = reportData,
            pageNumber = pageState.nextPageNumber(),
            continuation = true
        )
        nextPage.y = drawMonthSectionHeader(nextPage.canvas, section, nextPage.y, continuation = true)
        drawTableHeader(nextPage.canvas, nextPage.y)
        nextPage.y += ROW_HEIGHT
        return nextPage
    }

    private fun finishPage(
        document: PdfDocument,
        activePage: ActivePage
    ) {
        activePage.canvas.drawText(
            "${activePage.pageNumber}",
            (PAGE_WIDTH / 2).toFloat(),
            (PAGE_HEIGHT - 24).toFloat(),
            footerPaint
        )
        document.finishPage(activePage.page)
    }

    private fun drawTableHeader(
        canvas: Canvas,
        y: Int
    ) {
        canvas.drawRect(
            LEFT_MARGIN.toFloat(),
            (y - 16).toFloat(),
            (PAGE_WIDTH - RIGHT_MARGIN).toFloat(),
            (y + 8).toFloat(),
            headerBackgroundPaint
        )
        canvas.drawText("날짜", X_DATE.toFloat(), y.toFloat(), headerPaint)
        canvas.drawText("내용", X_TITLE.toFloat(), y.toFloat(), headerPaint)
        canvas.drawText("카테고리", X_CATEGORY.toFloat(), y.toFloat(), headerPaint)
        canvas.drawText("수입", X_INCOME_RIGHT.toFloat(), y.toFloat(), headerRightPaint)
        canvas.drawText("지출", X_EXPENSE_RIGHT.toFloat(), y.toFloat(), headerRightPaint)
        drawHorizontalLine(canvas, y + 10)
    }

    private fun drawTransactionRow(
        canvas: Canvas,
        transaction: Transaction,
        y: Int
    ) {
        val incomeText = if (transaction.type == TransactionType.INCOME) {
            formatWon(transaction.amount)
        } else {
            ""
        }
        val expenseText = if (transaction.type == TransactionType.EXPENSE) {
            formatWon(transaction.amount)
        } else {
            ""
        }

        canvas.drawText(formatMonthDay(transaction.date), X_DATE.toFloat(), y.toFloat(), bodyPaint)
        canvas.drawText(
            ellipsize(transaction.title, bodyPaint, TITLE_WIDTH),
            X_TITLE.toFloat(),
            y.toFloat(),
            bodyPaint
        )
        canvas.drawText(
            ellipsize(transaction.category, bodyPaint, CATEGORY_WIDTH),
            X_CATEGORY.toFloat(),
            y.toFloat(),
            bodyPaint
        )
        canvas.drawText(
            ellipsize(incomeText, bodyRightPaint, AMOUNT_WIDTH),
            X_INCOME_RIGHT.toFloat(),
            y.toFloat(),
            bodyRightPaint
        )
        canvas.drawText(
            ellipsize(expenseText, bodyRightPaint, AMOUNT_WIDTH),
            X_EXPENSE_RIGHT.toFloat(),
            y.toFloat(),
            bodyRightPaint
        )
        drawHorizontalLine(canvas, y + 10)
    }

    private fun drawEmptyRow(
        canvas: Canvas,
        y: Int
    ) {
        canvas.drawText("거래 내역 없음", X_DATE.toFloat(), y.toFloat(), bodyPaint)
        drawHorizontalLine(canvas, y + 10)
    }

    private fun drawHorizontalLine(
        canvas: Canvas,
        y: Int
    ) {
        canvas.drawLine(
            LEFT_MARGIN.toFloat(),
            y.toFloat(),
            (PAGE_WIDTH - RIGHT_MARGIN).toFloat(),
            y.toFloat(),
            linePaint
        )
    }

    private fun ellipsize(
        text: String,
        paint: Paint,
        maxWidth: Float
    ): String {
        if (text.isBlank() || paint.measureText(text) <= maxWidth) {
            return text
        }

        val suffix = "..."
        var end = text.length
        while (end > 0 && paint.measureText(text.take(end) + suffix) > maxWidth) {
            end -= 1
        }
        return if (end <= 0) suffix else text.take(end) + suffix
    }

    private fun formatMonthTitle(monthKey: String): String {
        val month = YearMonth.parse(monthKey, monthFormatter)
        return "${month.year}년 ${month.monthValue}월"
    }

    private fun formatMonthDay(dateIso: String): String {
        return runCatching {
            val date = LocalDate.parse(dateIso, isoDateFormatter)
            "%02d/%02d".format(date.monthValue, date.dayOfMonth)
        }.getOrElse {
            dateIso.takeLast(5)
        }
    }

    private fun formatWon(amount: Long): String {
        return "${numberFormatter.format(amount)}원"
    }

    private fun transactionSort(): Comparator<Transaction> {
        return compareByDescending<Transaction> { transaction -> transaction.date }
            .thenByDescending { transaction -> transaction.createdAt }
    }

    private fun pageInfo(pageNumber: Int): PdfDocument.PageInfo {
        return PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
    }

    private data class ActivePage(
        val page: PdfDocument.Page,
        val canvas: Canvas,
        val pageNumber: Int,
        var y: Int
    )

    private class PageState {
        private var currentPageNumber = 0

        fun nextPageNumber(): Int {
            currentPageNumber += 1
            return currentPageNumber
        }
    }

    private companion object {
        const val PAGE_WIDTH = 595
        const val PAGE_HEIGHT = 842
        const val LEFT_MARGIN = 40
        const val RIGHT_MARGIN = 40
        const val TOP_MARGIN = 54
        const val BOTTOM_MARGIN = 54
        const val ROW_HEIGHT = 28
        const val MONTH_SECTION_HEADER_HEIGHT = 48

        const val X_DATE = 42
        const val X_TITLE = 92
        const val X_CATEGORY = 272
        const val X_INCOME_RIGHT = 438
        const val X_EXPENSE_RIGHT = 552
        const val TITLE_WIDTH = 164f
        const val CATEGORY_WIDTH = 72f
        const val AMOUNT_WIDTH = 94f

        val numberFormatter: NumberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
        val isoDateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        val monthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM")
        val baseTypeface: Typeface = Typeface.create("sans", Typeface.NORMAL)

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(24, 24, 27)
            textSize = 22f
            typeface = Typeface.create(baseTypeface, Typeface.BOLD)
        }
        val sectionTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(24, 24, 27)
            textSize = 15f
            typeface = Typeface.create(baseTypeface, Typeface.BOLD)
        }
        val summaryPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(24, 24, 27)
            textSize = 12f
            typeface = baseTypeface
        }
        val smallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(113, 113, 122)
            textSize = 9f
            typeface = baseTypeface
        }
        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(24, 24, 27)
            textSize = 10f
            typeface = Typeface.create(baseTypeface, Typeface.BOLD)
        }
        val headerRightPaint = Paint(headerPaint).apply {
            textAlign = Paint.Align.RIGHT
        }
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(24, 24, 27)
            textSize = 10f
            typeface = baseTypeface
        }
        val bodyRightPaint = Paint(bodyPaint).apply {
            textAlign = Paint.Align.RIGHT
        }
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(113, 113, 122)
            textSize = 9f
            textAlign = Paint.Align.CENTER
            typeface = baseTypeface
        }
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(228, 228, 231)
            strokeWidth = 1f
        }
        val headerBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(244, 244, 245)
            style = Paint.Style.FILL
        }
    }
}
