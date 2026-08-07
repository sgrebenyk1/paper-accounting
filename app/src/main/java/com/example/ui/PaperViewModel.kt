package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.PaperItem
import com.example.data.PaperRepository
import com.example.util.PaperCalculator
import com.example.util.PdfReportGenerator
import com.example.util.TelegramShareUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption(val title: String) {
    NAME_ASC("По названию (А-Я)"),
    SHEETS_ASC("Сначала меньший остаток"),
    SHEETS_DESC("Сначала больший остаток"),
    DENSITY_DESC("По плотности (высокая)"),
    CM_DESC("По высоте стопы (см)")
}

class PaperViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PaperRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = PaperRepository(database.paperDao())
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedPaperType = MutableStateFlow("Все")
    val selectedPaperType: StateFlow<String> = _selectedPaperType

    private val _showLowStockOnly = MutableStateFlow(false)
    val showLowStockOnly: StateFlow<Boolean> = _showLowStockOnly

    private val _sortBy = MutableStateFlow(SortOption.NAME_ASC)
    val sortBy: StateFlow<SortOption> = _sortBy

    val filteredPapers: StateFlow<List<PaperItem>> = combine(
        repository.allPapers,
        _searchQuery,
        _selectedPaperType,
        _showLowStockOnly,
        _sortBy
    ) { allPapers, query, paperType, lowStockOnly, sort ->
        allPapers.filter { item ->
            val matchesSearch = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.format.contains(query, ignoreCase = true) ||
                    item.paperType.contains(query, ignoreCase = true) ||
                    item.location.contains(query, ignoreCase = true) ||
                    "${item.densityGsm}".contains(query)

            val matchesType = when (paperType) {
                "Все" -> true
                "Мелованная" -> item.paperType.contains("мелован", ignoreCase = true)
                "Офсетная" -> item.paperType.contains("офсет", ignoreCase = true)
                "Картон" -> item.paperType.contains("картон", ignoreCase = true)
                "Крафт" -> item.paperType.contains("крафт", ignoreCase = true)
                else -> item.paperType.equals(paperType, ignoreCase = true)
            }

            val matchesLowStock = if (lowStockOnly) item.sheetsCount <= item.minThresholdSheets else true

            matchesSearch && matchesType && matchesLowStock
        }.let { list ->
            when (sort) {
                SortOption.NAME_ASC -> list.sortedBy { it.name.lowercase() }
                SortOption.SHEETS_ASC -> list.sortedBy { it.sheetsCount }
                SortOption.SHEETS_DESC -> list.sortedByDescending { it.sheetsCount }
                SortOption.DENSITY_DESC -> list.sortedByDescending { it.densityGsm }
                SortOption.CM_DESC -> list.sortedByDescending { it.thicknessCm }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Summary Metrics
    val totalPaperTypes: StateFlow<Int> = repository.allPapers.combine(MutableStateFlow(Unit)) { list, _ ->
        list.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalSheetsCount: StateFlow<Int> = repository.allPapers.combine(MutableStateFlow(Unit)) { list, _ ->
        list.sumOf { it.sheetsCount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val lowStockCount: StateFlow<Int> = repository.allPapers.combine(MutableStateFlow(Unit)) { list, _ ->
        list.count { it.sheetsCount <= it.minThresholdSheets }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalThicknessMeters: StateFlow<Double> = repository.allPapers.combine(MutableStateFlow(Unit)) { list, _ ->
        list.sumOf { it.thicknessCm } / 100.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Dialog state
    val isAddEditOpen = MutableStateFlow(false)
    val editingPaperItem = MutableStateFlow<PaperItem?>(null)

    val isCalculatorOpen = MutableStateFlow(false)
    val isPdfExportOpen = MutableStateFlow(false)
    val generatedPdfOutput = MutableStateFlow<PdfReportGenerator.ReportOutput?>(null)

    val toastMessage = MutableStateFlow<String?>(null)

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onPaperTypeSelect(type: String) {
        _selectedPaperType.value = type
    }

    fun toggleLowStockOnly() {
        _showLowStockOnly.value = !_showLowStockOnly.value
    }

    fun setSortBy(sort: SortOption) {
        _sortBy.value = sort
    }

    fun openAddDialog() {
        editingPaperItem.value = null
        isAddEditOpen.value = true
    }

    fun openEditDialog(item: PaperItem) {
        editingPaperItem.value = item
        isAddEditOpen.value = true
    }

    fun dismissAddEditDialog() {
        isAddEditOpen.value = false
        editingPaperItem.value = null
    }

    fun savePaperItem(paperItem: PaperItem) {
        viewModelScope.launch {
            if (paperItem.id == 0L) {
                repository.insert(paperItem)
                toastMessage.value = "Бумага добавлена"
            } else {
                repository.update(paperItem)
                toastMessage.value = "Данные обновлены"
            }
            dismissAddEditDialog()
        }
    }

    fun deletePaperItem(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
            toastMessage.value = "Позиция удалена"
        }
    }

    fun adjustSheetCount(item: PaperItem, deltaSheets: Int) {
        val newSheets = (item.sheetsCount + deltaSheets).coerceAtLeast(0)
        val newThicknessCm = PaperCalculator.calculateCmFromSheets(
            sheetsCount = newSheets,
            densityGsm = item.densityGsm,
            paperType = item.paperType,
            customCaliperMicrons = item.caliperMicrons
        )

        viewModelScope.launch {
            repository.update(item.copy(sheetsCount = newSheets, thicknessCm = newThicknessCm, updatedAt = System.currentTimeMillis()))
        }
    }

    fun generatePdfReport(context: Context) {
        viewModelScope.launch {
            val currentItems = filteredPapers.value
            if (currentItems.isEmpty()) {
                toastMessage.value = "Нет позиций для отчета"
                return@launch
            }

            val report = PdfReportGenerator.generateReport(context, currentItems)
            if (report != null) {
                generatedPdfOutput.value = report
                isPdfExportOpen.value = true
            } else {
                toastMessage.value = "Ошибка при генерации PDF"
            }
        }
    }

    fun generateAndShareToTelegram(context: Context) {
        viewModelScope.launch {
            val currentItems = filteredPapers.value
            if (currentItems.isEmpty()) {
                toastMessage.value = "Нет позиций для отчета"
                return@launch
            }

            val report = PdfReportGenerator.generateReport(context, currentItems)
            if (report != null) {
                generatedPdfOutput.value = report
                TelegramShareUtil.sharePdfToTelegram(context, report.uri, report.formattedDate)
            } else {
                toastMessage.value = "Ошибка при генерации PDF"
            }
        }
    }

    fun sharePdfToTelegram(context: Context) {
        val pdf = generatedPdfOutput.value ?: return
        TelegramShareUtil.sharePdfToTelegram(context, pdf.uri, pdf.formattedDate)
    }

    fun openPdfFile(context: Context) {
        val pdf = generatedPdfOutput.value ?: return
        TelegramShareUtil.openPdfFile(context, pdf.uri)
    }

    fun clearToast() {
        toastMessage.value = null
    }
}
