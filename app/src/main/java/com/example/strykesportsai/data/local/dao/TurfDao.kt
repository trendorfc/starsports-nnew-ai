package com.example.strykesportsai.data.local.dao

import androidx.room.*
import com.example.strykesportsai.data.local.entity.TurfEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TurfDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTurf(turf: TurfEntity)

    @Query("SELECT * FROM turfs")
    fun getAllTurfs(): Flow<List<TurfEntity>>

    @Query("SELECT * FROM turfs WHERE id = :id")
    suspend fun getTurfById(id: Long): TurfEntity?

    @Query("SELECT * FROM turfs WHERE ownerId = :ownerId")
    fun getTurfsByOwner(ownerId: Long): Flow<List<TurfEntity>>
}
