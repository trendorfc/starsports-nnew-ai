package com.example.strykesportsai.data.local.dao

import androidx.room.*
import com.example.strykesportsai.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Query("SELECT * FROM bookings WHERE userId = :userId")
    fun getBookingsByUser(userId: Long): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE turfId = :turfId")
    fun getBookingsByTurf(turfId: Long): Flow<List<BookingEntity>>

    @Update
    suspend fun updateBooking(booking: BookingEntity)
}
