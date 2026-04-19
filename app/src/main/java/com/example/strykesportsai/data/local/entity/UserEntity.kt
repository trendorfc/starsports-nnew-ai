package com.example.strykesportsai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    PLAYER,
    TURF_OWNER,
    UNDEFINED
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val dob: String,
    val sportsInterests: String,
    val role: UserRole,
    val profileImageUrl: String? = null
)
