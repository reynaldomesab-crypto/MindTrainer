package com.mindtrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindtrainer.data.local.entity.CorpusVersionEntity
import java.util.UUID

@Dao
interface CorpusVersionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(version: CorpusVersionEntity)

    @Query("SELECT * FROM corpus_versions WHERE exercise_type_id = :exerciseTypeId AND language = :language ORDER BY version DESC LIMIT 1")
    suspend fun getLatest(exerciseTypeId: UUID, language: String): CorpusVersionEntity?

    @Query("SELECT * FROM corpus_versions WHERE exercise_type_id = :exerciseTypeId AND language = :language AND version = :version")
    suspend fun getByVersion(exerciseTypeId: UUID, language: String, version: Int): CorpusVersionEntity?
}