package com.example.data

import kotlinx.coroutines.flow.Flow

class PaperRepository(private val paperDao: PaperDao) {

    val allPapers: Flow<List<PaperItem>> = paperDao.getAllPapers()

    suspend fun insert(paper: PaperItem): Long = paperDao.insertPaper(paper)

    suspend fun update(paper: PaperItem) = paperDao.updatePaper(paper)

    suspend fun deleteById(id: Long) = paperDao.deletePaperById(id)

    suspend fun prepopulateIfEmpty() {
        if (paperDao.getCount() == 0) {
            val sampleItems = listOf(
                PaperItem(
                    name = "Мелованная Gloss (Upm Finesse)",
                    densityGsm = 130,
                    thicknessCm = 12.5,
                    sheetsCount = 1250,
                    format = "SRA3 (32x45)",
                    paperType = "Мелованная глянцевая",
                    caliperMicrons = 106.0,
                    minThresholdSheets = 300,
                    location = "Стеллаж А-1",
                    notes = "Партия №402, поставщик ДЕКА"
                ),
                PaperItem(
                    name = "Мелованная Silk (MagneStar)",
                    densityGsm = 170,
                    thicknessCm = 8.0,
                    sheetsCount = 650,
                    format = "SRA3 (32x45)",
                    paperType = "Мелованная матовая",
                    caliperMicrons = 123.0,
                    minThresholdSheets = 200,
                    location = "Стеллаж А-2",
                    notes = "Для буклетов и каталогов"
                ),
                PaperItem(
                    name = "Офсетная Сыктывкар ЭКО",
                    densityGsm = 80,
                    thicknessCm = 25.0,
                    sheetsCount = 2500,
                    format = "A4 (21x29.7)",
                    paperType = "Офсетная",
                    caliperMicrons = 100.0,
                    minThresholdSheets = 500,
                    location = "Палета Б-1",
                    notes = "Для бланков и инструкций"
                ),
                PaperItem(
                    name = "Xerox Colotech+ Extra White",
                    densityGsm = 300,
                    thicknessCm = 4.5,
                    sheetsCount = 160,
                    format = "SRA3 (32x45)",
                    paperType = "Каландрированная",
                    caliperMicrons = 280.0,
                    minThresholdSheets = 200,
                    location = "Стеллаж В-1",
                    notes = "Для визиток и обложек (Мало!)"
                ),
                PaperItem(
                    name = "Крафт упаковочный",
                    densityGsm = 120,
                    thicknessCm = 18.0,
                    sheetsCount = 1200,
                    format = "70x100 cm",
                    paperType = "Крафт",
                    caliperMicrons = 150.0,
                    minThresholdSheets = 300,
                    location = "Стеллаж Г-3",
                    notes = "Упаковка готовой продукции"
                ),
                PaperItem(
                    name = "Картон мелованный C2S",
                    densityGsm = 280,
                    thicknessCm = 3.2,
                    sheetsCount = 80,
                    format = "70x100 cm",
                    paperType = "Картон",
                    caliperMicrons = 390.0,
                    minThresholdSheets = 150,
                    location = "Паллета В-2",
                    notes = "Для коробок и папок (Срочно заказать!)"
                )
            )
            paperDao.insertPapers(sampleItems)
        }
    }
}
