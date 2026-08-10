package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.M3PurplePrimary
import com.example.ui.theme.M3TextPrimary
import com.example.ui.theme.M3TextSecondary
import com.example.ui.theme.WarningRed

@Composable
fun PaperSearchAndFilter(
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    showLowStockOnly: Boolean,
    onToggleLowStockOnly: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("Все", "Мелованная", "Офсетная", "Картон", "Крафт", "Дизайнерская", "Самоклейка", "Этикеточная")

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Label for Category / Paper Type
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "ТИП БУМАГИ:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = M3TextSecondary,
                    letterSpacing = 0.5.sp
                )
            )
            if (selectedCategory != "Все") {
                Text(
                    text = "Выбрано: $selectedCategory",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = M3PurplePrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Horizontal Category Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Low Stock Filter Chip
            FilterChip(
                selected = showLowStockOnly,
                onClick = onToggleLowStockOnly,
                label = { Text("Мало на складе", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (showLowStockOnly) Color.White else WarningRed
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = WarningRed,
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("low_stock_chip")
            )

            categories.forEach { category ->
                val isSelected = selectedCategory == category && !showLowStockOnly
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelect(category) },
                    label = { Text(category, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = M3PurplePrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFF7F2FA),
                        labelColor = M3TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("category_chip_$category")
                )
            }
        }
    }
}


