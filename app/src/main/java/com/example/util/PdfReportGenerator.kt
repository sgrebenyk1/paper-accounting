package com.example.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.PaperItem
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

    data class ReportOutput(
        val file: File,
        val uri: Uri,
        val totalItems: Int,
        val totalSheets: Int,
        val formattedDate: String
    )

    fun generateReport(context: Context, items: List<PaperItem>): ReportOutput? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 portrait in points (72 dpi)
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }
        val textPaint = Paint().apply { isAntiAlias = true }

        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru"))
        val currentDateStr = dateFormat.format(Date())

        var y = 40f
        val startX = 30f
        val endX = 565f
        val contentWidth = endX - startX

        // 1. Header Banner
        paint.color = Color.parseColor("#0F172A") // Deep navy
        canvas.drawRect(startX, y, endX, y + 60f, paint)

        textPaint.color = Color.WHITE
        textPaint.textSize = 16f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("ОТЧЕТ ПО ОСТАТКАМ БУМАГИ В ТИПОГРАФИИ", startX + 15f, y + 26f, textPaint)

        textPaint.textSize = 10f
        textPaint.typeface = Typeface.DEFAULT
        textPaint.color = Color.parseColor("#94A3B8")
        canvas.drawText("Сформировано: $currentDateStr", startX + 15f, y + 46f, textPaint)

        y += 75f

        // 2. Summary Metrics Bar
        val totalSheets = items.sumOf { it.sheetsCount }
        val lowStockCount = items.count { it.sheetsCount <= it.minThresholdSheets }
        val totalMeters = items.sumOf { it.thicknessCm } / 100.0

        paint.color = Color.parseColor("#F1F5F9")
        canvas.drawRoundRect(startX, y, endX, y + 45f, 8f, 8f, paint)

        textPaint.textSize = 10f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.color = Color.parseColor("#334155")

        val metricText = "Позиций: ${items.size}   |   Всего листов: %,d шт.   |   Мало на складе: $lowStockCount   |   Высота стоп: %.2f м".format(
            Locale("ru"), totalSheets, totalMeters
        )
        canvas.drawText(metricText, startX + 15f, y + 27f, textPaint)

        y += 60f

        // 3. Table Column Layout
        val colNoWidth = 25f
        val colNameWidth = 165f
        val colDensityWidth = 55f
        val colFormatWidth = 65f
        val colCmWidth = 60f
        val colSheetsWidth = 75f
        val colStatusWidth = 90f

        val col1X = startX
        val col2X = col1X + colNoWidth
        val col3X = col2X + colNameWidth
        val col4X = col3X + colDensityWidth
        val col5X = col4X + colFormatWidth
        val col6X = col5X + colCmWidth
        val col7X = col6X + colSheetsWidth

        // Draw Table Header
        paint.color = Color.parseColor("#0284C7") // Cyan accent
        canvas.drawRect(startX, y, endX, y + 24f, paint)

        textPaint.color = Color.WHITE
        textPaint.textSize = 9f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        canvas.drawText("№", col1X + 4f, y + 16f, textPaint)
        canvas.drawText("Наименование", col2X + 4f, y + 16f, textPaint)
        canvas.drawText("Плотн.", col3X + 4f, y + 16f, textPaint)
        canvas.drawText("Формат", col4X + 4f, y + 16f, textPaint)
        canvas.drawText("Остаток (см)", col5X + 4f, y + 16f, textPaint)
        canvas.drawText("Остаток (лист)", col6X + 4f, y + 16f, textPaint)
        canvas.drawText("Статус", col7X + 4f, y + 16f, textPaint)

        y += 24f

        // Draw Table Rows
        var rowNum = 1
        for (item in items) {
            // Check page height limit (842 - 60 = 782)
            if (y > 770f) {
                pdfDocument.finishPage(page)
                val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, pdfDocument.pages.size + 1).create()
                page = pdfDocument.startPage(newPageInfo)
                canvas = page.canvas
                y = 40f
            }

            // Zebra striping
            if (rowNum % 2 == 0) {
                paint.color = Color.parseColor("#F8FAFC")
                canvas.drawRect(startX, y, endX, y + 22f, paint)
            }

            // Grid bottom line
            paint.color = Color.parseColor("#E2E8F0")
            paint.strokeWidth = 0.5f
            canvas.drawLine(startX, y + 22f, endX, y + 22f, paint)

            textPaint.textSize = 9f
            textPaint.typeface = Typeface.DEFAULT
            textPaint.color = Color.parseColor("#0F172A")

            val truncatedName = if (item.name.length > 24) item.name.substring(0, 22) + "…" else item.name

            canvas.drawText("$rowNum", col1X + 4f, y + 15f, textPaint)
            canvas.drawText(truncatedName, col2X + 4f, y + 15f, textPaint)
            canvas.drawText("${item.densityGsm}г/м²", col3X + 4f, y + 15f, textPaint)
            canvas.drawText(item.format, col4X + 4f, y + 15f, textPaint)
            canvas.drawText("%.1f см".format(Locale("ru"), item.thicknessCm), col5X + 4f, y + 15f, textPaint)

            // Highlight sheet count
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("%,d".format(Locale("ru"), item.sheetsCount), col6X + 4f, y + 15f, textPaint)

            // Status indicator text
            val isLow = item.sheetsCount <= item.minThresholdSheets
            if (isLow) {
                textPaint.color = Color.parseColor("#DC2626") // Red warning
                canvas.drawText("Мало (≤${item.minThresholdSheets})", col7X + 4f, y + 15f, textPaint)
            } else {
                textPaint.color = Color.parseColor("#16A34A") // Green ok
                canvas.drawText("В наличии", col7X + 4f, y + 15f, textPaint)
            }

            y += 22f
            rowNum++
        }

        // 4. Footer & Signature Line
        y += 30f
        if (y > 750f) {
            pdfDocument.finishPage(page)
            val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, pdfDocument.pages.size + 1).create()
            page = pdfDocument.startPage(newPageInfo)
            canvas = page.canvas
            y = 40f
        }

        paint.color = Color.parseColor("#CBD5E1")
        paint.strokeWidth = 1f
        canvas.drawLine(startX, y, endX, y, paint)

        y += 20f
        textPaint.textSize = 9f
        textPaint.typeface = Typeface.DEFAULT
        textPaint.color = Color.parseColor("#64748B")
        canvas.drawText("Отчет сгенерирован автоматически из приложения «Учет бумаги»", startX, y, textPaint)

        y += 20f
        canvas.drawText("Ответственный за склад / Мастер цеха: _______________________ (Подпись)", startX, y, textPaint)

        pdfDocument.finishPage(page)

        // Save PDF to cache dir / reports
        val reportsDir = File(context.cacheDir, "reports")
        if (!reportsDir.exists()) {
            reportsDir.mkdirs()
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val pdfFile = File(reportsDir, "Paper_Inventory_Report_$timeStamp.pdf")

        return try {
            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)
            outputStream.close()
            pdfDocument.close()

            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, pdfFile)

            ReportOutput(
                file = pdfFile,
                uri = uri,
                totalItems = items.size,
                totalSheets = totalSheets,
                formattedDate = currentDateStr
            )
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }
}
