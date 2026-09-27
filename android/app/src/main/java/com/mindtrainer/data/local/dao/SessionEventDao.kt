package com.mindtrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindtrainer.data.local.entity.SessionEventEntity
import java.util.UUID

@Dao
interface SessionEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<SessionEventEntity>)

    @Query("SELECT * FROM session_events WHERE session_id = :sessionId ORDER BY timestamp_ms")
    suspend fun getBySession(sessionId: UUID): List<SessionEventEntity>
}