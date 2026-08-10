package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PaperItem
import com.example.ui.theme.M3Border
import com.example.ui.theme.M3CardSurface
import com.example.ui.theme.M3OnPurpleContainer
import com.example.ui.theme.M3PurpleContainer
import com.example.ui.theme.M3PurplePrimary
import com.example.ui.theme.M3TextPrimary
import com.example.ui.theme.M3TextSecondary
import com.example.ui.theme.WarningRed
import java.util.Locale

@Composable
fun PaperItemCard(
    item: PaperItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAdjustSheets: (delta: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLowStock = item.sheetsCount <= item.minThresholdSheets
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("paper_item_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = M3CardSurface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, M3Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Title, Format Badge, and Actions Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = M3TextPrimary
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Формат: ${item.format}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = M3TextSecondary
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // High-Density Format Badge (e.g., SRA3, A3, A4)
                    TagBadge(
                        text = item.format.uppercase(),
                        color = M3OnPurpleContainer,
                        containerColor = M3PurpleContainer,
                        isBold = true
                    )

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("item_menu_button_${item.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Опции",
                                tint = M3TextSecondary
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Редактировать") },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Удалить", color = WarningRed) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = WarningRed) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details Badges Row: Prominent Paper Type & Density
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prominent Paper Type Badge
                TagBadge(
                    text = "Тип: ${item.paperType}",
                    color = Color(0xFF1D192B),
                    containerColor = Color(0xFFE8DEF8),
                    isBold = true
                )

                // Density Badge
                TagBadge(
                    text = "${item.densityGsm} г/м²",
                    color = M3TextSecondary,
                    containerColor = Color(0xFFECE6F0)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Low Stock Warning Banner if applicable
            if (isLowStock) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = WarningRed.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Предупреждение",
                            tint = WarningRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "МАЛО (≤ ${item.minThresholdSheets} листов)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = WarningRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Core Stock Display Grid (CM & SHEETS)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, M3Border, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Rest in CM
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ОСТАТОК (СМ)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = M3TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "%.1f см".format(Locale("ru"), item.thicknessCm),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = M3TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                        .background(M3Border)
                )

                // Right Column: Rest in SHEETS (листов)
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = "ЛИСТОВ (РАСЧЕТ)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = M3TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "%,d шт".format(Locale("ru"), item.sheetsCount),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = if (isLowStock) WarningRed else M3PurplePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Location & Quick Stock Adjust Buttons (+ / - 100 sheets)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Локация",
                        tint = M3TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.location.ifBlank { "Склад" },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = M3TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }

                // Quick Stock Adjust (+100 / -100 sheets)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    StockAdjustButton(
                        icon = Icons.Default.Remove,
                        label = "-100",
                        onClick = { onAdjustSheets(-100) },
                        tag = "sub_sheets_${item.id}"
                    )

                    StockAdjustButton(
                        icon = Icons.Default.Add,
                        label = "+100",
                        onClick = { onAdjustSheets(100) },
                        tag = "add_sheets_${item.id}"
                    )
                }
            }
        }
    }
}

@Composable
private fun TagBadge(text: String, color: Color, containerColor: Color, isBold: Boolean = false) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = containerColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                color = color,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
private fun StockAdjustButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    tag: String
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .testTag(tag),
        color = Color(0xFFECE6F0)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = M3TextPrimary,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = M3TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
        }
    }
}

