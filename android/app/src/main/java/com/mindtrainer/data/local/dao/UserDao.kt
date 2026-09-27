package com.mindtrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mindtrainer.data.local.entity.UserEntity
import java.util.UUID

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: UUID): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE provider = :provider AND provider_id = :providerId")
    suspend fun getByProvider(provider: String, providerId: String): UserEntity?

    @Update
    suspend fun update(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun delete(id: UUID)
}