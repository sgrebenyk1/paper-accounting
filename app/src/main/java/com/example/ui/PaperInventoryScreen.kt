package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.PaperItem
import com.example.ui.components.AddEditPaperDialog
import com.example.ui.components.CalculatorDialog
import com.example.ui.components.DashboardHeader
import com.example.ui.components.PaperItemCard
import com.example.ui.components.PaperSearchAndFilter
import com.example.ui.components.PdfExportDialog
import com.example.ui.theme.M3FooterBg
import com.example.ui.theme.M3OnPurpleContainer
import com.example.ui.theme.M3PurpleContainer
import com.example.ui.theme.M3PurplePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaperInventoryScreen(
    viewModel: PaperViewModel
) {
    val context = LocalContext.current
    val papers by viewModel.filteredPapers.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedPaperType by viewModel.selectedPaperType.collectAsStateWithLifecycle()
    val showLowStockOnly by viewModel.showLowStockOnly.collectAsStateWithLifecycle()
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()

    val totalTypes by viewModel.totalPaperTypes.collectAsStateWithLifecycle()
    val totalSheets by viewModel.totalSheetsCount.collectAsStateWithLifecycle()
    val lowStockCount by viewModel.lowStockCount.collectAsStateWithLifecycle()
    val totalMeters by viewModel.totalThicknessMeters.collectAsStateWithLifecycle()

    val isAddEditOpen by viewModel.isAddEditOpen.collectAsStateWithLifecycle()
    val editingPaperItem by viewModel.editingPaperItem.collectAsStateWithLifecycle()

    val isCalculatorOpen by viewModel.isCalculatorOpen.collectAsStateWithLifecycle()
    val isPdfExportOpen by viewModel.isPdfExportOpen.collectAsStateWithLifecycle()
    val generatedPdfOutput by viewModel.generatedPdfOutput.collectAsStateWithLifecycle()

    val toastMsg by viewModel.toastMessage.collectAsStateWithLifecycle()

    LaunchedEffect(toastMsg) {
        toastMsg?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bottom_action_bar"),
                color = M3FooterBg,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 10.dp,
                            bottom = 10.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // PDF Export Button
                        OutlinedButton(
                            onClick = { viewModel.generatePdfReport(context) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("bottom_pdf_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, M3PurplePrimary),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = M3PurplePrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "PDF",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PDF", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        // Send to Client / Telegram Button
                        Button(
                            onClick = { viewModel.generateAndShareToTelegram(context) },
                            modifier = Modifier
                                .weight(2f)
                                .height(48.dp)
                                .testTag("bottom_send_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = M3PurplePrimary,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Отправить клиенту",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Отправить клиенту", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openAddDialog() },
                icon = { Icon(Icons.Default.Add, contentDescription = "Добавить бумагу") },
                text = { Text("Добавить бумагу", fontWeight = FontWeight.Bold) },
                containerColor = M3PurpleContainer,
                contentColor = M3OnPurpleContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .testTag("add_paper_fab")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Dashboard Header
            item {
                DashboardHeader(
                    totalTypes = totalTypes,
                    totalSheets = totalSheets,
                    lowStockCount = lowStockCount,
                    totalMeters = totalMeters,
                    onOpenCalculator = { viewModel.isCalculatorOpen.value = true },
                    onGeneratePdf = { viewModel.generatePdfReport(context) }
                )
            }

            // 2. Search & Filters
            item {
                PaperSearchAndFilter(
                    searchQuery = searchQuery,
                    onSearchChange = { viewModel.onSearchQueryChange(it) },
                    selectedCategory = selectedPaperType,
                    onCategorySelect = { viewModel.onPaperTypeSelect(it) },
                    showLowStockOnly = showLowStockOnly,
                    onToggleLowStockOnly = { viewModel.toggleLowStockOnly() },
                    sortBy = sortBy,
                    onSortSelect = { viewModel.setSortBy(it) }
                )
            }

            // 3. Stock Items List or Empty State
            if (papers.isEmpty()) {
                item {
                    EmptyStockView(
                        isFiltered = searchQuery.isNotEmpty() || selectedPaperType != "Все" || showLowStockOnly,
                        onResetFilters = {
                            viewModel.onSearchQueryChange("")
                            viewModel.onPaperTypeSelect("Все")
                            if (showLowStockOnly) viewModel.toggleLowStockOnly()
                        }
                    )
                }
            } else {
                items(
                    items = papers,
                    key = { it.id }
                ) { paperItem ->
                    PaperItemCard(
                        item = paperItem,
                        onEdit = { viewModel.openEditDialog(paperItem) },
                        onDelete = { viewModel.deletePaperItem(paperItem.id) },
                        onAdjustSheets = { delta -> viewModel.adjustSheetCount(paperItem, delta) }
                    )
                }
            }
        }
    }

    // Dialogs
    if (isAddEditOpen) {
        AddEditPaperDialog(
            item = editingPaperItem,
            onDismiss = { viewModel.dismissAddEditDialog() },
            onSave = { viewModel.savePaperItem(it) }
        )
    }

    if (isCalculatorOpen) {
        CalculatorDialog(
            onDismiss = { viewModel.isCalculatorOpen.value = false }
        )
    }

    if (isPdfExportOpen) {
        PdfExportDialog(
            report = generatedPdfOutput,
            onDismiss = { viewModel.isPdfExportOpen.value = false },
            onShareTelegram = { viewModel.sharePdfToTelegram(context) },
            onOpenPdf = { viewModel.openPdfFile(context) }
        )
    }
}

@Composable
private fun EmptyStockView(
    isFiltered: Boolean,
    onResetFilters: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .testTag("empty_stock_view"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Inventory,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isFiltered) "Позиции не найдены" else "Склад бумаги пуст",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isFiltered)
                    "Попробуйте изменить параметры поиска или сбросить фильтры"
                else
                    "Нажмите кнопку «Добавить бумагу» внизу для внесения первых остатков",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B)),
                textAlign = TextAlign.Center
            )
        }
    }
}
