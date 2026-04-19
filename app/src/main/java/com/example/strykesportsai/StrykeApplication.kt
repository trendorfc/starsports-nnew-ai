package com.example.strykesportsai

import android.app.Application
import com.example.strykesportsai.data.local.AppDatabase
import com.example.strykesportsai.data.repository.StrykeRepository

class StrykeApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { 
        StrykeRepository(
            database.userDao(),
            database.turfDao(),
            database.bookingDao(),
            database.matchDao()
        )
    }
}
