package com.example.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PaperRepository {

    private val sampleItems = listOf(
        PaperItem(
            id = "1",
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
            id = "2",
            name = "Мелованная Gloss (Upm Finesse)",
            densityGsm = 130,
            thicknessCm = 10.0,
            sheetsCount = 1000,
            format = "70x100 cm",
            paperType = "Мелованная глянцевая",
            caliperMicrons = 106.0,
            minThresholdSheets = 300,
            location = "Стеллаж А-1",
            notes = "Листовой формат 70х100"
        ),
        PaperItem(
            id = "3",
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
            id = "4",
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
            id = "5",
            name = "Офсетная Сыктывкар ЭКО",
            densityGsm = 80,
            thicknessCm = 15.0,
            sheetsCount = 1500,
            format = "A3 (29.7x42)",
            paperType = "Офсетная",
            caliperMicrons = 100.0,
            minThresholdSheets = 400,
            location = "Палета Б-1",
            notes = "Формат А3 для брошюр"
        ),
        PaperItem(
            id = "6",
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
            id = "7",
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
            id = "8",
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
        ),
        PaperItem(
            id = "9",
            name = "Этикеточная Самоклеящаяся",
            densityGsm = 80,
            thicknessCm = 8.0,
            sheetsCount = 800,
            format = "A4 (21x29.7)",
            paperType = "Этикеточная",
            caliperMicrons = 95.0,
            minThresholdSheets = 200,
            location = "Стеллаж Д-1",
            notes = "Для стикеров и этикеток"
        )
    )

    private val localItems = MutableStateFlow(sampleItems)

    private val firestore: FirebaseFirestore? = try {
        FirebaseFirestore.getInstance()
    } catch (e: Throwable) {
        Log.w("PaperRepository", "Firestore not available, using local storage", e)
        null
    }

    private val collection = firestore?.collection("paper_items")

    val allPapers: Flow<List<PaperItem>> = if (collection != null) {
        callbackFlow {
            val subscription = collection.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PaperRepository", "Firestore listen error, falling back to local", error)
                    trySend(localItems.value)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { it.toObject(PaperItem::class.java) }
                    if (items.isNotEmpty()) {
                        trySend(items)
                    } else {
                        trySend(localItems.value)
                    }
                } else {
                    trySend(localItems.value)
                }
            }
            awaitClose { subscription.remove() }
        }
    } else {
        localItems.asStateFlow()
    }

    suspend fun insert(paper: PaperItem): String {
        val col = collection
        if (col != null) {
            return try {
                val ref = col.document()
                val paperWithId = paper.copy(id = ref.id)
                ref.set(paperWithId).await()
                ref.id
            } catch (e: Exception) {
                Log.e("PaperRepository", "Error inserting to Firestore", e)
                insertLocal(paper)
            }
        } else {
            return insertLocal(paper)
        }
    }

    private fun insertLocal(paper: PaperItem): String {
        val newId = if (paper.id.isNotEmpty()) paper.id else UUID.randomUUID().toString()
        val newItem = paper.copy(id = newId)
        localItems.value = localItems.value + newItem
        return newId
    }

    suspend fun update(paper: PaperItem) {
        val col = collection
        if (col != null && paper.id.isNotEmpty()) {
            try {
                col.document(paper.id).set(paper).await()
            } catch (e: Exception) {
                Log.e("PaperRepository", "Error updating Firestore", e)
                updateLocal(paper)
            }
        } else {
            updateLocal(paper)
        }
    }

    private fun updateLocal(paper: PaperItem) {
        localItems.value = localItems.value.map { if (it.id == paper.id) paper else it }
    }

    suspend fun deleteById(id: String) {
        val col = collection
        if (col != null && id.isNotEmpty()) {
            try {
                col.document(id).delete().await()
            } catch (e: Exception) {
                Log.e("PaperRepository", "Error deleting from Firestore", e)
                deleteLocal(id)
            }
        } else {
            deleteLocal(id)
        }
    }

    private fun deleteLocal(id: String) {
        localItems.value = localItems.value.filter { it.id != id }
    }

    suspend fun prepopulateIfEmpty() {
        val col = collection ?: return
        try {
            val countQuery = col.count().get(com.google.firebase.firestore.AggregateSource.SERVER).await()
            if (countQuery.count == 0L) {
                firestore?.runBatch { batch ->
                    sampleItems.forEach { item ->
                        val ref = col.document()
                        batch.set(ref, item.copy(id = ref.id))
                    }
                }?.await()
            }
        } catch (e: Exception) {
            Log.e("PaperRepository", "Error prepopulating Firestore", e)
        }
    }
}
