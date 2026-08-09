package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object TelegramShareUtil {

    /**
     * Shares the PDF file to Telegram or opens system chooser with Telegram pre-selected.
     */
    fun sharePdfToTelegram(context: Context, pdfUri: Uri, reportDateStr: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            putExtra(Intent.EXTRA_SUBJECT, "Отчет по остаткам бумаги ($reportDateStr)")
            putExtra(
                Intent.EXTRA_TEXT,
                "Добрый день!\nНаправляю актуальный отчет по остаткам бумаги в типографии на $reportDateStr.\nФайл PDF во вложении."
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Try direct Telegram intent
        val telegramPackages = listOf("org.telegram.messenger", "org.telegram.messenger.web", "org.thunderdog.challegram")
        var launchedDirectly = false

        for (pkg in telegramPackages) {
            try {
                val directIntent = Intent(shareIntent).apply {
                    setPackage(pkg)
                }
                if (directIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(directIntent)
                    launchedDirectly = true
                    break
                }
            } catch (e: Exception) {
                // Try next
            }
        }

        // Fallback to system intent chooser (shows Telegram and other installed apps)
        if (!launchedDirectly) {
            try {
                val chooserIntent = Intent.createChooser(shareIntent, "Отправить отчет в Telegram / Поделиться").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooserIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Не удалось открыть приложение для отправки", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Opens PDF with an external viewer app (e.g. Drive PDF Viewer, Adobe, Chrome).
     */
    fun openPdfFile(context: Context, pdfUri: Uri) {
        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(pdfUri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(viewIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Нет приложения для просмотра PDF файлов", Toast.LENGTH_SHORT).show()
        }
    }
}
