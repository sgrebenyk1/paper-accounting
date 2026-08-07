package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PaperDao {
    @Query("SELECT * FROM paper_items ORDER BY name ASC")
    fun getAllPapers(): Flow<List<PaperItem>>

    @Query("SELECT * FROM paper_items WHERE id = :id")
    suspend fun getPaperById(id: Long): PaperItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaper(paper: PaperItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPapers(papers: List<PaperItem>)

    @Update
    suspend fun updatePaper(paper: PaperItem)

    @Query("DELETE FROM paper_items WHERE id = :id")
    suspend fun deletePaperById(id: Long)

    @Query("DELETE FROM paper_items")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM paper_items")
    suspend fun getCount(): Int
}
