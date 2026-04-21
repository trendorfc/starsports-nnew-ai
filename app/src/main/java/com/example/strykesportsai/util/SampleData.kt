package com.example.strykesportsai.util

import com.example.strykesportsai.data.local.entity.TurfEntity
import com.example.strykesportsai.data.repository.StrykeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SampleData {
    fun insertSampleTurfs(repository: StrykeRepository) {
        val sampleTurfs = listOf(
            TurfEntity(
                id = 1,
                name = "Stryke Arena Noida",
                location = "Sector 62, Noida",
                sportsSupported = "Football, Cricket",
                pricePerHour = 1200.0,
                description = "World-class turf with modern amenities and floodlights for night matches.",
                ownerId = 1,
                imageUrls = "https://images.unsplash.com/photo-1574629810360-7efbbe195018"
            ),
            TurfEntity(
                id = 2,
                name = "Greater Noida Sports Hub",
                location = "Knowledge Park III, Greater Noida",
                sportsSupported = "Cricket, Tennis",
                pricePerHour = 1500.0,
                description = "Premium multi-sport complex offering high-quality turf and expert coaching.",
                ownerId = 1,
                imageUrls = "https://images.unsplash.com/photo-1531415074968-036ba1b575da"
            ),
            TurfEntity(
                id = 3,
                name = "Yamuna Expressway Turf",
                location = "Pari Chowk, Greater Noida",
                sportsSupported = "Football",
                pricePerHour = 1000.0,
                description = "High-speed turf designed for intense football matches. Open 24/7.",
                ownerId = 1,
                imageUrls = "https://images.unsplash.com/photo-1546519638-68e109498ffc"
            )
        )

        val sampleMatches = listOf(
            com.example.strykesportsai.data.local.entity.MatchEntity(
                id = 1,
                creatorId = 999,
                turfId = 1,
                location = "Sector 62, Noida",
                sport = "Football",
                startTime = System.currentTimeMillis() + 86400000,
                endTime = System.currentTimeMillis() + 90000000,
                playersNeeded = 4,
                currentPlayers = 6,
                description = "Friendly weekend match"
            ),
            com.example.strykesportsai.data.local.entity.MatchEntity(
                id = 2,
                creatorId = 998,
                turfId = 2,
                location = "Knowledge Park III, Greater Noida",
                sport = "Cricket",
                startTime = System.currentTimeMillis() + 172800000,
                endTime = System.currentTimeMillis() + 180000000,
                playersNeeded = 2,
                currentPlayers = 20,
                description = "Casual cricket"
            )
        )

        CoroutineScope(Dispatchers.IO).launch {
            sampleTurfs.forEach { repository.saveTurf(it) }
            sampleMatches.forEach { repository.saveMatch(it) }
        }
    }
}
