package com.example.strykesportsai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val turfId: Long,
    val startTime: Long, // Timestamp
    val endTime: Long, // Timestamp
    val status: BookingStatus,
    val totalPrice: Double
)
