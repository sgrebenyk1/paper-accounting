package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.PaperItem
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.SlateNavy
import com.example.util.PaperCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPaperDialog(
    item: PaperItem?,
    onDismiss: () -> Unit,
    onSave: (PaperItem) -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var densityGsmStr by remember { mutableStateOf(item?.densityGsm?.toString() ?: "130") }
    var paperType by remember { mutableStateOf(item?.paperType ?: "Мелованная глянцевая") }
    var format by remember { mutableStateOf(item?.format ?: "SRA3 (32x45)") }
    var thicknessCmStr by remember { mutableStateOf(item?.thicknessCm?.toString() ?: "10.0") }
    var sheetsCountStr by remember { mutableStateOf(item?.sheetsCount?.toString() ?: "1000") }
    var minThresholdStr by remember { mutableStateOf(item?.minThresholdSheets?.toString() ?: "200") }
    var location by remember { mutableStateOf(item?.location ?: "Стеллаж А-1") }
    var notes by remember { mutableStateOf(item?.notes ?: "") }

    var isErrorName by remember { mutableStateOf(false) }

    val presetDensities = listOf("80", "115", "130", "150", "170", "200", "250", "300", "350")
    val paperTypes = listOf("Мелованная глянцевая", "Мелованная матовая", "Офсетная", "Картон", "Крафт", "Самоклейка")
    val formats = listOf("SRA3 (32x45)", "A3 (29.7x42)", "A4 (21x29.7)", "70x100 см", "62x94 см")

    // Helper for live calculation
    fun updateSheetsFromCm(cmInput: String) {
        thicknessCmStr = cmInput
        val cm = cmInput.toDoubleOrNull() ?: 0.0
        val density = densityGsmStr.toIntOrNull() ?: 130
        val sheets = PaperCalculator.calculateSheetsFromCm(cm, density, paperType)
        sheetsCountStr = "$sheets"
    }

    fun updateCmFromSheets(sheetsInput: String) {
        sheetsCountStr = sheetsInput
        val sheets = sheetsInput.toIntOrNull() ?: 0
        val density = densityGsmStr.toIntOrNull() ?: 130
        val cm = PaperCalculator.calculateCmFromSheets(sheets, density, paperType)
        thicknessCmStr = "$cm"
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("add_edit_dialog_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (item == null) "Добавить бумагу" else "Редактировать бумагу",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = SlateNavy,
                        fontSize = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        isErrorName = false
                    },
                    label = { Text("Наименование бумаги *") },
                    placeholder = { Text("например, Upm Finesse Gloss") },
                    isError = isErrorName,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_paper_name"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Paper Type Selection
                Text(text = "Тип бумаги", style = MaterialTheme.typography.labelMedium, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    paperTypes.forEach { type ->
                        FilterChip(
                            selected = paperType == type,
                            onClick = {
                                paperType = type
                                // Recalculate sheets for new paper type
                                updateSheetsFromCm(thicknessCmStr)
                            },
                            label = { Text(type, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Density GSM
                Text(text = "Плотность (г/м²)", style = MaterialTheme.typography.labelMedium, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetDensities.forEach { preset ->
                        FilterChip(
                            selected = densityGsmStr == preset,
                            onClick = {
                                densityGsmStr = preset
                                updateSheetsFromCm(thicknessCmStr)
                            },
                            label = { Text("$preset г/м²", fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = densityGsmStr,
                    onValueChange = {
                        densityGsmStr = it
                        updateSheetsFromCm(thicknessCmStr)
                    },
                    label = { Text("Точная плотность (г/м²)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_density"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Format
                Text(text = "Формат листа", style = MaterialTheme.typography.labelMedium, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    formats.forEach { fmt ->
                        FilterChip(
                            selected = format == fmt,
                            onClick = { format = fmt },
                            label = { Text(fmt, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 5. LIVE STACK CONVERTER (CM <-> SHEETS)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = CyanPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Авторасчет остатка стопы",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SlateNavy,
                                    fontSize = 13.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = thicknessCmStr,
                                onValueChange = { updateSheetsFromCm(it) },
                                label = { Text("Остаток (см)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_thickness_cm"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                            )

                            OutlinedTextField(
                                value = sheetsCountStr,
                                onValueChange = { updateCmFromSheets(it) },
                                label = { Text("Остаток (листов)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_sheets_count"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 6. Min Threshold & Location
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = minThresholdStr,
                        onValueChange = { minThresholdStr = it },
                        label = { Text("Порог «Мало» (шт)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_min_threshold"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Стеллаж / Локация") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_location"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 7. Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Заметки / Поставщик") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_notes"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена", color = Color(0xFF64748B))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                isErrorName = true
                                return@Button
                            }

                            val density = densityGsmStr.toIntOrNull() ?: 130
                            val thicknessCm = thicknessCmStr.toDoubleOrNull() ?: 0.0
                            val sheetsCount = sheetsCountStr.toIntOrNull() ?: 0
                            val minThreshold = minThresholdStr.toIntOrNull() ?: 200

                            val newItem = PaperItem(
                                id = item?.id ?: 0L,
                                name = name.trim(),
                                densityGsm = density,
                                thicknessCm = thicknessCm,
                                sheetsCount = sheetsCount,
                                format = format,
                                paperType = paperType,
                                caliperMicrons = PaperCalculator.estimateCaliperMicrons(density, paperType),
                                minThresholdSheets = minThreshold,
                                location = location.trim(),
                                notes = notes.trim(),
                                updatedAt = System.currentTimeMillis()
                            )

                            onSave(newItem)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        modifier = Modifier.testTag("save_paper_button")
                    ) {
                        Text("Сохранить", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
