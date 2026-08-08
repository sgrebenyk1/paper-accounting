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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.SlateNavy
import com.example.util.PaperCalculator
import java.util.Locale

@Composable
fun CalculatorDialog(
    onDismiss: () -> Unit
) {
    var densityGsmStr by remember { mutableStateOf("130") }
    var paperType by remember { mutableStateOf("Мелованная глянцевая") }
    var inputCmStr by remember { mutableStateOf("10.0") }
    var inputSheetsStr by remember { mutableStateOf("1000") }

    val densities = listOf("80", "115", "130", "150", "170", "200", "250", "300", "350")
    val types = listOf("Мелованная глянцевая", "Мелованная матовая", "Офсетная", "Картон")

    val currentDensity = densityGsmStr.toIntOrNull() ?: 130
    val estimatedCaliper = PaperCalculator.estimateCaliperMicrons(currentDensity, paperType)

    fun onCmChanged(cmVal: String) {
        inputCmStr = cmVal
        val cm = cmVal.toDoubleOrNull() ?: 0.0
        val sheets = PaperCalculator.calculateSheetsFromCm(cm, currentDensity, paperType)
        inputSheetsStr = "$sheets"
    }

    fun onSheetsChanged(sheetsVal: String) {
        inputSheetsStr = sheetsVal
        val sheets = sheetsVal.toIntOrNull() ?: 0
        val cm = PaperCalculator.calculateCmFromSheets(sheets, currentDensity, paperType)
        inputCmStr = "$cm"
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("calculator_dialog_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Калькулятор стопы бумаги",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = SlateNavy,
                            fontSize = 18.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Density Selector
                Text("Плотность (г/м²):", style = MaterialTheme.typography.labelMedium, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    densities.forEach { gsm ->
                        FilterChip(
                            selected = densityGsmStr == gsm,
                            onClick = {
                                densityGsmStr = gsm
                                onCmChanged(inputCmStr)
                            },
                            label = { Text("$gsm г/м²", fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Paper Type Selector
                Text("Тип бумаги:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    types.forEach { type ->
                        FilterChip(
                            selected = paperType == type,
                            onClick = {
                                paperType = type
                                onCmChanged(inputCmStr)
                            },
                            label = { Text(type, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Inputs (CM <-> SHEETS)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    OutlinedTextField(
                        value = inputCmStr,
                        onValueChange = { onCmChanged(it) },
                        label = { Text("Высота стопы линейкой (см)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Конвертер",
                            tint = CyanPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = inputSheetsStr,
                        onValueChange = { onSheetsChanged(it) },
                        label = { Text("Количество листов (шт)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Calculation Info Card
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE0F2FE)
                ) {
                    Text(
                        text = "Расчетная толщина 1 листа: %.1f мкм. Формула: Стопа (см) = Листов × Толщину / 10,000".format(
                            Locale("ru"), estimatedCaliper
                        ),
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF0369A1),
                            fontSize = 11.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                    ) {
                        Text("Готово", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
