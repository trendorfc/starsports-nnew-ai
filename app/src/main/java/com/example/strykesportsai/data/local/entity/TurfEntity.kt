package com.example.strykesportsai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "turfs")
data class TurfEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ownerId: Long,
    val name: String,
    val location: String,
    val sportsSupported: String, // Comma separated
    val pricePerHour: Double,
    val description: String,
    val imageUrls: String, // Comma separated
    val availableTimings: String = "06:00, 07:00, 08:00, 09:00, 10:00, 17:00, 18:00, 19:00, 20:00, 21:00", // Comma separated hours
    val openDays: String = "Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday" // Comma separated days
)
