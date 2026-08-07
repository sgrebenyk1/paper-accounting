package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paper_items")
data class PaperItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,                  // Наименование бумаги (e.g., "Upm Finesse Gloss")
    val densityGsm: Int,               // Плотность бумаги (г/м², e.g., 130)
    val thicknessCm: Double,           // Остаток в сантиметрах (см, e.g., 12.5)
    val sheetsCount: Int,              // Остаток в листах (e.g., 1250)
    val format: String = "SRA3",       // Формат (SRA3, A3, A4, 70x100)
    val paperType: String = "Мелованная", // Тип (Мелованная глянцевая, Офсетная, etc.)
    val caliperMicrons: Double = 100.0, // Толщина 1 листа в мкм (микронах)
    val minThresholdSheets: Int = 200, // Порог малого остатка
    val location: String = "Стеллаж А", // Локация на складе
    val notes: String = "",            // Заметки / Поставщик
    val updatedAt: Long = System.currentTimeMillis()
)
