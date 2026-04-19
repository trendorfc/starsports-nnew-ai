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
    val imageUrls: String // Comma separated
)
