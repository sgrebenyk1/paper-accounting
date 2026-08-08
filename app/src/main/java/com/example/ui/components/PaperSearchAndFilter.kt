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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SortOption
import com.example.ui.theme.M3Border
import com.example.ui.theme.M3PurpleContainer
import com.example.ui.theme.M3PurplePrimary
import com.example.ui.theme.M3TextPrimary
import com.example.ui.theme.M3TextSecondary
import com.example.ui.theme.WarningRed

@Composable
fun PaperSearchAndFilter(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    showLowStockOnly: Boolean,
    onToggleLowStockOnly: () -> Unit,
    sortBy: SortOption,
    onSortSelect: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("Все", "Мелованная", "Офсетная", "Картон", "Крафт", "Дизайнерская")
    var showSortMenu by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Search & Sort Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .weight(1f)
                    .testTag("paper_search_input"),
                placeholder = { Text("Поиск по названию, формату, типу...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = M3TextSecondary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = M3TextSecondary)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = M3PurplePrimary,
                    unfocusedBorderColor = M3Border,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFECE6F0).copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { showSortMenu = true },
                modifier = Modifier.testTag("sort_menu_button")
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Сортировка",
                    tint = M3PurplePrimary
                )
            }

            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                SortOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option.title,
                                fontWeight = if (sortBy == option) FontWeight.Bold else FontWeight.Normal,
                                color = if (sortBy == option) M3PurplePrimary else M3TextPrimary
                            )
                        },
                        onClick = {
                            onSortSelect(option)
                            showSortMenu = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

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

