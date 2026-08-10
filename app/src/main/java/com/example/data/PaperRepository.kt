package com.example.data

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

class PaperRepository(private val context: Context? = null) {

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

    private fun paperItemToJson(item: PaperItem): JSONObject {
        return JSONObject().apply {
            put("id", item.id)
            put("name", item.name)
            put("densityGsm", item.densityGsm)
            put("thicknessCm", item.thicknessCm)
            put("sheetsCount", item.sheetsCount)
            put("format", item.format)
            put("paperType", item.paperType)
            put("caliperMicrons", item.caliperMicrons)
            put("minThresholdSheets", item.minThresholdSheets)
            put("location", item.location)
            put("notes", item.notes)
            put("updatedAt", item.updatedAt)
        }
    }

    private fun jsonToPaperItem(obj: JSONObject): PaperItem {
        return PaperItem(
            id = obj.optString("id", ""),
            name = obj.optString("name", ""),
            densityGsm = obj.optInt("densityGsm", 0),
            thicknessCm = obj.optDouble("thicknessCm", 0.0),
            sheetsCount = obj.optInt("sheetsCount", 0),
            format = obj.optString("format", "SRA3"),
            paperType = obj.optString("paperType", "Мелованная"),
            caliperMicrons = obj.optDouble("caliperMicrons", 100.0),
            minThresholdSheets = obj.optInt("minThresholdSheets", 200),
            location = obj.optString("location", ""),
            notes = obj.optString("notes", ""),
            updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
        )
    }

    private fun saveLocalToDisk(items: List<PaperItem>) {
        val ctx = context ?: return
        try {
            val array = JSONArray()
            items.forEach { array.put(paperItemToJson(it)) }
            val file = File(ctx.filesDir, "paper_items_v1.json")
            file.writeText(array.toString())
        } catch (e: Exception) {
            Log.e("PaperRepository", "Error saving items to local disk", e)
        }
    }

    private fun loadLocalFromDisk(): List<PaperItem>? {
        val ctx = context ?: return null
        try {
            val file = File(ctx.filesDir, "paper_items_v1.json")
            if (!file.exists()) return null
            val content = file.readText()
            if (content.isBlank()) return null
            val array = JSONArray(content)
            val list = mutableListOf<PaperItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(jsonToPaperItem(obj))
            }
            return if (list.isNotEmpty()) list else null
        } catch (e: Exception) {
            Log.e("PaperRepository", "Error loading items from local disk", e)
            return null
        }
    }

    private val initialItems: List<PaperItem> = loadLocalFromDisk() ?: run {
        saveLocalToDisk(sampleItems)
        sampleItems
    }

    private val localItems = MutableStateFlow(initialItems)

    val allPapers: StateFlow<List<PaperItem>> = localItems.asStateFlow()

    private val auth = try {
        com.google.firebase.auth.FirebaseAuth.getInstance().apply {
            if (currentUser == null) {
                signInAnonymously().addOnSuccessListener {
                    Log.d("PaperRepository", "Signed in anonymously to Firebase")
                }.addOnFailureListener { e ->
                    Log.e("PaperRepository", "Anonymous sign in failed", e)
                }
            }
        }
    } catch (e: Throwable) {
        Log.w("PaperRepository", "FirebaseAuth not available", e)
        null
    }

    private val firestore: FirebaseFirestore? = try {
        FirebaseFirestore.getInstance().apply {
            enableNetwork()
        }
    } catch (e: Throwable) {
        Log.w("PaperRepository", "Firestore not available, using local storage", e)
        null
    }

    private val collection = firestore?.collection("paper_items")

    init {
        setupFirestoreListener()
    }

    private fun setupFirestoreListener() {
        val col = collection ?: return
        try {
            col.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PaperRepository", "Firestore listen error: ${error.message}", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(PaperItem::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            Log.e("PaperRepository", "Error deserializing document ${doc.id}", e)
                            null
                        }
                    }
                    if (items.isNotEmpty() || !snapshot.metadata.isFromCache) {
                        localItems.value = items
                        saveLocalToDisk(items)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("PaperRepository", "Error setting up Firestore listener", e)
        }
    }

    suspend fun insert(paper: PaperItem): String {
        val col = collection
        val newId = if (paper.id.isNotEmpty()) paper.id else UUID.randomUUID().toString()
        val paperWithId = paper.copy(id = newId)
        
        // Update local immediately for instant UI response
        insertLocal(paperWithId)

        if (col != null) {
            try {
                col.document(newId).set(paperWithId)
                    .addOnFailureListener { e ->
                        Log.e("PaperRepository", "Error inserting to Firestore", e)
                    }
            } catch (e: Throwable) {
                Log.e("PaperRepository", "Error initiating Firestore insert", e)
            }
        }
        return newId
    }

    private fun insertLocal(paper: PaperItem) {
        val updated = localItems.value.filter { it.id != paper.id } + paper
        localItems.value = updated
        saveLocalToDisk(updated)
    }

    suspend fun update(paper: PaperItem) {
        updateLocal(paper)
        val col = collection
        if (col != null && paper.id.isNotEmpty()) {
            try {
                col.document(paper.id).set(paper)
                    .addOnFailureListener { e ->
                        Log.e("PaperRepository", "Error updating Firestore", e)
                    }
            } catch (e: Throwable) {
                Log.e("PaperRepository", "Error initiating Firestore update", e)
            }
        }
    }

    private fun updateLocal(paper: PaperItem) {
        val updated = localItems.value.map { if (it.id == paper.id) paper else it }
        localItems.value = updated
        saveLocalToDisk(updated)
    }

    suspend fun deleteById(id: String) {
        deleteLocal(id)
        val col = collection
        if (col != null && id.isNotEmpty()) {
            try {
                col.document(id).delete()
                    .addOnFailureListener { e ->
                        Log.e("PaperRepository", "Error deleting from Firestore", e)
                    }
            } catch (e: Throwable) {
                Log.e("PaperRepository", "Error initiating Firestore delete", e)
            }
        }
    }

    private fun deleteLocal(id: String) {
        val updated = localItems.value.filter { it.id != id }
        localItems.value = updated
        saveLocalToDisk(updated)
    }

    suspend fun prepopulateIfEmpty() {
        val col = collection ?: return
        try {
            col.limit(1).get(com.google.firebase.firestore.Source.SERVER)
                .addOnSuccessListener { snapshot ->
                    if (snapshot.isEmpty) {
                        firestore?.runBatch { batch ->
                            localItems.value.forEach { item ->
                                val ref = col.document(item.id.ifEmpty { UUID.randomUUID().toString() })
                                batch.set(ref, item.copy(id = ref.id))
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("PaperRepository", "Server fetch for prepopulate failed or offline: ${e.message}")
                }
        } catch (e: Throwable) {
            Log.e("PaperRepository", "Error checking or prepopulating Firestore", e)
        }
    }
}
