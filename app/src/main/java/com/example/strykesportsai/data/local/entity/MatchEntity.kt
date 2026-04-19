package com.example.strykesportsai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val creatorId: Long,
    val turfId: Long?, // Optional if match is not at a specific registered turf
    val location: String,
    val sport: String,
    val startTime: Long,
    val endTime: Long,
    val playersNeeded: Int,
    val currentPlayers: Int,
    val description: String
)
